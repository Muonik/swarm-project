import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.ArrayList;

public class Simulator extends JFrame implements /*MouseListener,*/ MouseMotionListener {
  public static final int DEFAULT_SPEED = 10;
  public static final int MAX_SPEED = 30;
  public static final int DEFAULT_REPULSION_RANGE = 30;
  public static final int MAX_REPULSION_RANGE = 120;
  public static final int DEFAULT_ALIGNMENT_RANGE = 80;
  public static final int MAX_ALIGNMENT_RANGE = 180;
  public static final int DEFAULT_ATTRACTION_RANGE = 210;
  public static final int MAX_ATTRACTION_RANGE = 320;

  public static final int CANVAS_WIDTH = 1200;
  public static final int CANVAS_HEIGHT = 900;
  public static final int RADIUS = 20;
  public static final Color CANVAS_COLOR = new Color(40,40,40);
  public static final Color PARTICLE_COLOR = Color.WHITE;
  public static final Color ATT_COLOR = new Color(0.706f, 0.855f, 0.969f);
  public static final int UPDATE_INTERVAL = 30; //milliseconds
  
  private static int flockSize; /* = 242  uncomment dis when running sim directly*/

  private Synthesizer synth;
  private MidiChannel[] channels;
  private File sample = new File("Oppenheimer.wav");
  private int instrument;
  private Particle attractor;
  // private JToggleButton toggleAttractor;

  private DrawCanvas canvas;
  private JPanel controls;
  private Flock flock;
  private Flock flock1;

  public Simulator(int flockSize) {
    this.flockSize = flockSize;
    flock = new Flock();
    for(int i = 0; i < flockSize; i++) {
      flock.addParticle(new Particle(RADIUS, (int)(Math.random()*1200), (int)(Math.random()*900), DEFAULT_SPEED, (int)(Math.random() * 360), PARTICLE_COLOR));
    }
    addAttractor();

    canvas = new DrawCanvas();
    canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    canvas.addMouseMotionListener(this);

    addControlPanel();
    Container c = getContentPane();
    c.setLayout(new BorderLayout());
    c.add(canvas, BorderLayout.CENTER);
    c.add(controls, BorderLayout.EAST);

    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Shittiest Swarm Simulator on Earth");
    pack();
    setVisible(true);
    requestFocus();

    try {
      synth = MidiSystem.getSynthesizer();
      synth.open();
      channels = synth.getChannels();
    }
    catch (MidiUnavailableException e) {}

    Thread updateThread = new Thread() {
      @Override
      public void run() {
        while (true) {
          flock.move();
          repaint(); 
          try {
            playNote();
            invisibleFlock();
          }
          catch (MidiUnavailableException e) {}
          catch (InvalidMidiDataException e) {}
          try {
            Thread.sleep(UPDATE_INTERVAL); 
            //channels[4].allNotesOff();
            }
          catch (InterruptedException e) {/*System.exit(0);*/}
        }
      } 
    };
    updateThread.start();

    playSample();

  }//Simulator

  public void playNote() throws MidiUnavailableException, InvalidMidiDataException {
    Point avr = flock.calcCentre();
    int pitch = (int)(avr.x / 16 + 40);
    int loudness = (int)(-avr.y / 10 + 180);
    
    channels[4].programChange(0,8); //bank and preset of the instrument 

    int rem = pitch % 12; 
    int rand = (int)(8*Math.random()); //p(note is played) = 1/8
    if(rem==4|rem==5|rem==8|rem==9|rem==11|rem==0|rem==14 && rand == 0) {
      channels[4].noteOn(pitch, loudness);
    }

    // try {Thread.sleep(200);} 
    // catch (InterruptedException e) {}
    // channels[5].allNotesOff();
    // synth.close();
  }


  public void playSample() {
    try {
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(sample);
      AudioFormat format = audioStream.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, format);
      Clip audioClip = (Clip) AudioSystem.getLine(info);
      audioClip.open(audioStream);
      audioClip.start();           
    // try {
    //   Thread.sleep(2000);
    // } catch (InterruptedException e) {e.printStackTrace();}
    //audioClip.close();
    }
    catch (UnsupportedAudioFileException e) {e.printStackTrace();} 
    catch (LineUnavailableException e) {e.printStackTrace();} 
    catch (IOException e) {e.printStackTrace();}
  }

  public void invisibleFlock() throws MidiUnavailableException, InvalidMidiDataException {
    flock1 = new Flock();
    for(int i = 0; i < 20; i++) {
      flock1.addParticle(new Particle(RADIUS, (int)(Math.random()*1200), (int)(Math.random()*900), DEFAULT_SPEED, (int)(Math.random() * 360), PARTICLE_COLOR));
    }
    Point avr = flock1.calcCentre();
    int pitch = (int)(avr.x / 16 + 40);
    int loudness = (int)(-avr.y / 10 + 100);
    
    channels[6].programChange(0,91); 

    int rem = pitch % 12; 
    int rand = (int)(8*Math.random());
    if(rand==0) {
      channels[6].noteOn(pitch, loudness);
    }
  }

  // @Override
  // public void mouseExited(MouseEvent e) {
  //   if (hasAttractor()) removeAttractor();
  // }

  // @Override
  // public void mouseEntered(MouseEvent e) {
  //   if (hasAttractor()) addAttractor();
  // }

  @Override
  public void mouseMoved(MouseEvent e) {
    attractor.setLocation(new Point(e.getX(), e.getY()));
  }

  public void mouseDragged(MouseEvent e){}
  // public void mouseReleased(MouseEvent e){}
  // public void mousePressed(MouseEvent e){}
  // public void mouseClicked(MouseEvent e){}

  public void addAttractor() {
    attractor = new Particle(RADIUS, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, DEFAULT_SPEED, (int)(Math.random() * 360), ATT_COLOR);
    flock.addParticle(attractor);
  }
  // public void removeAttractor() {
  //   flock.removeParticle(attractor);
  // }
  // public boolean hasAttractor() {
  //   return flock.containsParticle(attractor);
  // }

  public void addControlPanel(){
    controls = new JPanel(new GridLayout());
    JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_SPEED, DEFAULT_SPEED);
    JSlider repulsionSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_REPULSION_RANGE, DEFAULT_REPULSION_RANGE);
    JSlider alignmentSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_ALIGNMENT_RANGE, DEFAULT_ALIGNMENT_RANGE);
    JSlider attractionSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_ATTRACTION_RANGE, DEFAULT_ATTRACTION_RANGE);
    JLabel speedLabel = new JLabel("Particle Speed", JLabel.CENTER);
    JLabel repulsionLabel = new JLabel("Repulsion Range", JLabel.CENTER);
    JLabel alignmentLabel = new JLabel("Alignment Range", JLabel.CENTER);
    JLabel attractionLabel = new JLabel("Attraction Range", JLabel.CENTER);
    //toggleAttractor = new JToggleButton("Attractor Please");

    //speedLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
    //toggleAttractor.setMaximumSize(new Dimension(180, 40));

    speedSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          flock.setSpeed((int)source.getValue());
        }//if
      }//stateChanged
    });

    repulsionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          flock.setRepulsionRange((int)source.getValue());
        }
      }
    });

    alignmentSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          flock.setAlignmentRange((int)source.getValue());
        }
      }
    });

    attractionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          flock.setAttractionRange((int)source.getValue());
        }
      }
    });

    // toggleAttractor.addChangeListener(new ChangeListener() {
    //     public void stateChanged(ChangeEvent e) {
    //         if (toggleAttractor.isSelected()){
    //           addAttractor();
    //           toggleAttractor.setText("Remove Attractor");
    //         } else {
    //           removeAttractor();
    //           toggleAttractor.setText("Attractor Please");
    //         }
    //     }
    // });

    controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS)/*GridLayout(4, 2)*/);
    controls.setBackground(Color.WHITE);

    controls.add(Box.createRigidArea(new Dimension(0,50)));
    controls.add(speedLabel);
    controls.add(Box.createRigidArea(new Dimension(0,10)));
    controls.add(speedSlider);
    controls.add(Box.createRigidArea(new Dimension(0,50)));
    controls.add(repulsionLabel);
    controls.add(Box.createRigidArea(new Dimension(0,10)));
    controls.add(repulsionSlider); 
    controls.add(Box.createRigidArea(new Dimension(0,50)));
    controls.add(alignmentLabel);
    controls.add(Box.createRigidArea(new Dimension(0,10)));
    controls.add(alignmentSlider);
    controls.add(Box.createRigidArea(new Dimension(0,50)));
    controls.add(attractionLabel);
    controls.add(Box.createRigidArea(new Dimension(0,10)));
    controls.add(attractionSlider);
    controls.add(Box.createRigidArea(new Dimension(0,50)));
    //controls.add(toggleAttractor);

  }

  public void resetSliderValues(){
    flock.setSpeed(DEFAULT_SPEED);
    flock.setRepulsionRange(DEFAULT_REPULSION_RANGE);
    flock.setAlignmentRange(DEFAULT_ALIGNMENT_RANGE);
    flock.setAttractionRange(DEFAULT_ATTRACTION_RANGE);
  }

  class DrawCanvas extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      setBackground(CANVAS_COLOR);
      flock.draw(g);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {new Simulator(flockSize);}
    });
  }//main

}
