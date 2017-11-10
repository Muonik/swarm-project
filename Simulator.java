import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;

public class Simulator extends JFrame {
  public static final int CANVAS_WIDTH = 1000;
  public static final int CANVAS_HEIGHT = 800;
  public static final int RADIUS = 20;
  public static final int DEFAULT_SPEED = 10;
  public static final Color CANVAS_COLOR = Color.BLACK;
  public static final int UPDATE_INTERVAL = 30; //milliseconds
  public static final int DEFAULT_ATTRACTION_RANGE = 150;
  public static final int MAX_ATTRACTION_RANGE = 200;
  public static final int DEFAULT_REPULSION_RANGE = 100;
  public static final int MAX_REPULSION_RANGE = 120;
  public static final int MAX_SPEED = 40;
  public static final int MAX_TURN_DEGREE = 60;

  private int speed, attractionRange, repulsionRange;
  private DrawCanvas canvas;
  private JPanel controls;
  private Particle p1, p2, p3, p4;
  private ArrayList<Particle> particles;

  public Simulator() {
    p1 = new Particle(RADIUS, CANVAS_WIDTH / 2 + 80, CANVAS_HEIGHT / 2, 90, 60);
    p2 = new Particle(RADIUS, CANVAS_WIDTH / 2 + 200, CANVAS_HEIGHT / 2 - 200, 20, 0);
    p3 = new Particle(RADIUS, CANVAS_WIDTH / 2 - 100, CANVAS_HEIGHT / 2 - 50, 20, 190);
    p4 = new Particle(RADIUS, CANVAS_WIDTH / 2 + 100, CANVAS_HEIGHT / 2 - 50, 20, 300);

    particles = new ArrayList<Particle>();
    particles.add(p1);
    particles.add(p2);
    particles.add(p3);
    particles.add(p4);

    canvas = new DrawCanvas();
    canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    addControlPanel();
    setValues();

    Container c = getContentPane();
    c.setLayout(new BorderLayout());
    c.add(canvas, BorderLayout.CENTER);
    c.add(controls, BorderLayout.EAST);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Shitty Swarm Simulator");
    pack();
    setVisible(true);
    requestFocus();

    Thread updateThread = new Thread() {
      @Override
      public void run() {
        while (true) {
          for(int i = 0; i < particles.size(); i++) {
            particles.get(i).move(calcHeading(particles.get(i)));
          }
          repaint();
          try {
            Thread.sleep(UPDATE_INTERVAL);  // milliseconds
          } catch (InterruptedException ignore) {}
        }//while
      }
    };
    updateThread.start();
  }//Simulator constructor

  //compute a new heading
  //need to add weighing
  public int calcHeading(Particle p) {
    Point target= new Point(0, 0);
    int numOfP = 0;
    int newHeading, turnDegree;

    for(int i = 0; i < particles.size(); i++) {
      Particle other = particles.get(i);
      int distance = p.calcDistance(other);
      if(!p.equals(other) && distance < attractionRange && distance > 0){
        if (distance > repulsionRange){
          //attraction
          target = other.getLocation();
        }
        else {
          //repulsion
          target = sumPoints(p.getLocation() , subPoints(p.getLocation(), other.getLocation()));
        }
      }
      numOfP++;
    }//for

    newHeading = (int)Math.toDegrees(Math.atan2(p.getLocation().y - target.y, target.x - p.getLocation().x));
    turnDegree = newHeading - p.getHeading();

    //blind volume
    if (Math.abs(turnDegree) > MAX_TURN_DEGREE){
      if (turnDegree > 0) newHeading = p.getHeading() + MAX_TURN_DEGREE;
      else newHeading = p.getHeading() - MAX_TURN_DEGREE;
    }

    return newHeading;
  }//calcHeading

  public int calcRealDistance(Point p1, Point p2){
    //to do
    //need to take edges into consideration
    return 0;
  }

  public Point sumPoints(Point p1, Point p2) {return new Point(p1.x + p2.x, p1.y + p2.y);}
  public Point subPoints(Point p1, Point p2) {return new Point(p1.x - p2.x, p1.y - p2.y);}

  public Point normalisePoint(Point p){
    int magnitude = (int)Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.y, 2));
    return new Point(p.x / magnitude, p.y / magnitude);
  }

  public void addControlPanel(){
    controls = new JPanel(new GridLayout());
    JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_SPEED, DEFAULT_SPEED);
    JSlider attractionSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_ATTRACTION_RANGE, DEFAULT_ATTRACTION_RANGE);
    JSlider repulsionSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_REPULSION_RANGE, DEFAULT_REPULSION_RANGE);
    JLabel speedLabel = new JLabel("Speed");
    JLabel attractionLabel = new JLabel("Attraction Range");
    JLabel repulsionLabel = new JLabel("Repulsion Range");
    //JButtom reset;

    speedSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          for(int i = 0; i < particles.size(); i++){
            particles.get(i).setSpeed((int)source.getValue());
          }
        }//if
      }//stateChanged
    });

    attractionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {attractionRange = (int)source.getValue();}
      }//stateChanged
    });

    repulsionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {repulsionRange = (int)source.getValue();}
      }//stateChanged
    });

    controls.setLayout(new GridLayout(3, 2));
    controls.setBackground(Color.WHITE);

    controls.add(speedLabel);
    controls.add(speedSlider);
    controls.add(attractionLabel);
    controls.add(attractionSlider);
    controls.add(repulsionLabel);
    controls.add(repulsionSlider);
  }

  public void setValues(){
    speed = DEFAULT_SPEED;
    attractionRange = DEFAULT_ATTRACTION_RANGE;
    repulsionRange = DEFAULT_REPULSION_RANGE;
    for(int i = 0; i < particles.size(); i++) {
      particles.get(i).setSpeed(speed);
    }
  }

  class DrawCanvas extends JPanel {
     @Override
     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(CANVAS_COLOR);
        p1.draw(g);
        p2.draw(g);
        p3.draw(g);
        p4.draw(g);
    }
  }//class DrawCanvas

  public static void main(String[] args) {
     SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {new Simulator();}
     });
  }//main
}
