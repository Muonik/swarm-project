import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

public class Simulator extends JFrame {
  public static final int CANVAS_WIDTH = 1000;
  public static final int CANVAS_HEIGHT = 800;
  public static final int RADIUS = 20;
  public static final Color CANVAS_COLOR = Color.BLACK;
  public static final int UPDATE_INTERVAL = 50; //milliseconds
  public static final int ATTRACTION_RANGE = 60;
  public static final int REPULSION_RANGE = 15;

  private DrawCanvas canvas;
  private Particle p1, p2, p3;
  private ArrayList particles;

  public Simulator() {
    p1 = new Particle(RADIUS, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, 20, 15);
    p2 = new Particle(RADIUS, CANVAS_WIDTH / 2 + 20, CANVAS_HEIGHT / 2 - 20, 20, 350);
    p3 = new Particle(RADIUS, CANVAS_WIDTH / 2 - 100, CANVAS_HEIGHT / 2 - 50, 20, 190);

    particles = new ArrayList<Particle>();
    particles.add(p1);
    particles.add(p2);
    particles.add(p3);

    canvas = new DrawCanvas();
    canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(canvas, BorderLayout.CENTER);

    setTitle("uhh some shitty java program");
    pack();
    setVisible(true);
    requestFocus();

    Thread updateThread = new Thread() {
      @Override
      public void run() {
        while (true) {
          p1.move(calcHeading(p1));
          p2.move(calcHeading(p2));
          p3.move(calcHeading(p3));
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
  private int calcHeading(Particle p) {
    Point target = new Point(0, 0);
    int numOfP = 0;

    for(int i = 0; i < particles.size(); i++) {
      Particle other = (Particle)particles.get(i);
      int distance = p.calcDistance(other);
      if(!p.equals(other) && distance < ATTRACTION_RANGE){
        if (distance > REPULSION_RANGE){
          //attract
          target = other.getLocation();
        }
        // else {
        //   //repulse
        //   target = new Point(p.getLocation().x );
        // }
      }
      numOfP++;
    }//for
    if (numOfP == 0) return p.getHeading();

    return (int)Math.toDegrees(Math.atan2(p.getLocation().y - target.y, target.x - p.getLocation().x));

  }//calcHeading

  class DrawCanvas extends JPanel {
     @Override
     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(CANVAS_COLOR);
        p1.draw(g);
        p2.draw(g);
        p3.draw(g);
    }
  }//class DrawCanvas

  public static void main(String[] args) {
     SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {new Simulator();}
     });
  }//main
}
