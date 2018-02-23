import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import javax.swing.event.*;
import java.util.ArrayList;

public class Simulator extends JFrame {
  public static final int DEFAULT_SPEED = 20;
  public static final int MAX_SPEED = 30;
  public static final int DEFAULT_ATTRACTION_RANGE = 180;
  public static final int MAX_ATTRACTION_RANGE = 300;
  public static final int DEFAULT_REPULSION_RANGE = 120;
  public static final int MAX_REPULSION_RANGE = 250;

  public static final int CANVAS_WIDTH = 1000;
  public static final int CANVAS_HEIGHT = 800;
  public static final int RADIUS = 20;
  public static final Color CANVAS_COLOR = Color.BLACK;
  public static final int UPDATE_INTERVAL = 20; //milliseconds
  public static final int NUM_OF_PARTICLES = 42;

  private Synthesizer synth;
  private MidiChannel[] channels;
  private File sample = new File("C:/Users/muriel/Desktop/swarm/slowly but surely.wav");

  private DrawCanvas canvas;
  private JPanel controls;
  private Flock flock;

  public Simulator() {
    flock = new Flock();
    for(int i = 0; i < NUM_OF_PARTICLES; i++) {
      flock.addParticle(new Particle(RADIUS, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, DEFAULT_SPEED, (int)Math.random() * 360));
    }

    canvas = new DrawCanvas();
    canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    addControlPanel();

    Container c = getContentPane();
    c.setLayout(new BorderLayout());
    c.add(canvas, BorderLayout.CENTER);
    c.add(controls, BorderLayout.EAST);

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
          try {playNote();}
          catch (MidiUnavailableException e) {}
          catch (InvalidMidiDataException e) {}
          try {Thread.sleep(UPDATE_INTERVAL);}
        catch (InterruptedException e) {/*System.exit(0);*/}
        }//while
      }//run
    };
    updateThread.start();

    //playSample();

  }//Simulator

  public void playNote() throws MidiUnavailableException, InvalidMidiDataException {
    Point avr = flock.calcSomethingIDK();
    int pitch = (int)(avr.x / 15 + 20);
    int loudness = (int)(-avr.y / 10 + 110);

    channels[4].programChange(0,22); //bank, preset of the instrument 
    channels[4].noteOn(pitch, loudness);
   // try {Thread.sleep(UPDATE_INTERVAL);} 
   // catch (InterruptedException e) {}
   // channels[4].noteOff(pitch, loudness);
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

  public void addControlPanel(){
    controls = new JPanel(new GridLayout());
    JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_SPEED, DEFAULT_SPEED);
    JSlider attractionSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_ATTRACTION_RANGE, DEFAULT_ATTRACTION_RANGE);
    JSlider repulsionSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_REPULSION_RANGE, DEFAULT_REPULSION_RANGE);
    JLabel speedLabel = new JLabel("Speed");
    JLabel attractionLabel = new JLabel("Attraction Range");
    JLabel repulsionLabel = new JLabel("Repulsion Range");

    //TO DO
    //JButtom reset;

    speedSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          flock.setSpeed((int)source.getValue());
        }//if
      }//stateChanged
    });

    attractionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          flock.setAttractionRange((int)source.getValue());
        }
      }//stateChanged
    });

    repulsionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          flock.setRepulsionRange((int)source.getValue());
        }
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

  public void resetSliderValues(){
    flock.setSpeed(DEFAULT_SPEED);
    flock.setAttractionRange(DEFAULT_ATTRACTION_RANGE);
    flock.setRepulsionRange(DEFAULT_REPULSION_RANGE);
  }

  class DrawCanvas extends JPanel {
   @Override
   public void paintComponent(Graphics g) {
    super.paintComponent(g);
    setBackground(CANVAS_COLOR);
    flock.draw(g);
  }
  }//class DrawCanvas

  public static void main(String[] args) {
   SwingUtilities.invokeLater(new Runnable() {
    @Override
    public void run() {new Simulator();}
  });
  }//main
}
