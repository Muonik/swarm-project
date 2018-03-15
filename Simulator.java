import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class Simulator extends JFrame implements /*MouseListener,*/ MouseMotionListener {
  public static final int DEFAULT_SPEED = 10;
  public static final int MAX_SPEED = 30;
  public static final int DEFAULT_REPULSION_RANGE = 30;
  public static final int MAX_REPULSION_RANGE = 90;
  public static final int DEFAULT_ALIGNMENT_RANGE = 50;
  public static final int MAX_ALIGNMENT_RANGE = 180;
  public static final int DEFAULT_ATTRACTION_RANGE = 210;
  public static final int MAX_ATTRACTION_RANGE = 320;

  public static final int CANVAS_WIDTH = 1200;
  public static final int CANVAS_HEIGHT = 900;
  public static final int RADIUS = 20;
  public static final Color CANVAS_COLOR = new Color(40,40,40);
  public static final Color PARTICLE_COLOR = new Color(242,242,242);
  public static final Color ATT_COLOR = new Color(0.706f, 0.855f, 0.969f);
  public static final int UPDATE_INTERVAL = 30; //milliseconds

  public static final int[] phrygianScale = {4, 5, 8, 9, 11, 0, 14};
  public static final int[] bluesScale = {4, 6, 7, 8, 11, 13};
  public static final int PIANO = 2;
  public static final int HARP = 46;
  public static final int CELESTA = 8;
  public static final int SPACE = 91;

  private static int flockSize /* = 242;  //uncomment dis when running sim directly*/ ;

  private Synthesizer synth;
  private MidiChannel[] channels;
  private int mainChnlNum = 4;
  private int backgroundChnlNum = 6;
  private File sample = new File("Oppenheimer.wav");
  private int[] currentScale;
  private int mainInstr;
  private int backgroundInstr;
  private Particle attractor;

  private DrawCanvas canvas;
  private JPanel controls;
  private Flock flock;
  private Flock flock1;

  public Simulator(int flockSize) {
    this.flockSize = flockSize;
    flock = new Flock();
    for(int i = 0; i < (flockSize - 1); i++) {
      flock.addParticle(new Particle(RADIUS, (int)(Math.random()*CANVAS_WIDTH), (int)(Math.random()*CANVAS_HEIGHT), DEFAULT_SPEED, (int)(Math.random() * 360), PARTICLE_COLOR));
    }
    addAttractor();

    currentScale = phrygianScale;
    mainInstr = PIANO;
    backgroundInstr = HARP;

    canvas = new DrawCanvas();
    canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    canvas.addMouseMotionListener(this);

    initControlPanel();
    Container c = getContentPane();
    c.setLayout(new BorderLayout());
    c.add(canvas, BorderLayout.CENTER);
    c.add(controls, BorderLayout.SOUTH);
    
    setPreferredSize(new Dimension(CANVAS_WIDTH,(CANVAS_HEIGHT+300)));
    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("shittiest swarm simulator on earth");
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

    //playSample();

  }//Simulator

  public void playNote() throws MidiUnavailableException, InvalidMidiDataException {
    Point avr = flock.calcCentre();
    int pitch = (int)(avr.x / 16 + 36);
    int loudness = (int)(-avr.y / 10 + 170);
    
    channels[mainChnlNum].programChange(0,mainInstr); //bank and preset of the instrument 

    int rem = pitch % 12; 
    int rand = (int)(8*Math.random()); //p(note is played) = 1/8
    if(IntStream.of(currentScale).anyMatch(i -> i == rem) && rand == 0){
      channels[mainChnlNum].noteOn(pitch, loudness);
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
  //a second flock that is not drawn. generates background music 
    flock1 = new Flock();
    for(int i = 0; i < 20; i++) {
      flock1.addParticle(new Particle(RADIUS, (int)(Math.random()*CANVAS_WIDTH), (int)(Math.random()*CANVAS_HEIGHT), DEFAULT_SPEED, (int)(Math.random() * 360), PARTICLE_COLOR));
    }
    flock1.move();
    Point avr = flock1.calcCentre();
    int pitch = (int)(avr.x / 16 + 40);
    int loudness = (int)(-avr.y / 10 + 140);

    if (backgroundInstr == SPACE) loudness -= 145;
    
    channels[backgroundChnlNum].programChange(0,backgroundInstr); 

    int rem = pitch % 12; 
    int rand = (int)(8*Math.random());
    if(IntStream.of(currentScale).anyMatch(i -> i == rem) && rand==0) {
      channels[backgroundChnlNum].noteOn(pitch, loudness);
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

  public void initControlPanel(){
    controls = new JPanel();
    JLabel particlesLabel = new JLabel("mess around with the particles");
    JSlider speedSlider = new JSlider(0, MAX_SPEED, DEFAULT_SPEED);
    JSlider repulsionSlider = new JSlider(0, MAX_REPULSION_RANGE, DEFAULT_REPULSION_RANGE);
    JSlider alignmentSlider = new JSlider(0, MAX_ALIGNMENT_RANGE, DEFAULT_ALIGNMENT_RANGE);
    JSlider attractionSlider = new JSlider(0, MAX_ATTRACTION_RANGE, DEFAULT_ATTRACTION_RANGE);
    JLabel speedLabel = new JLabel("speed");
    JLabel repulsionLabel = new JLabel("repulsion");
    JLabel alignmentLabel = new JLabel("alignment");
    JLabel attractionLabel = new JLabel("attraction");
    JLabel musicLabel = new JLabel("mess around with the music");
    JComboBox<String> scaleBox = new JComboBox<>();
    JComboBox<String> mainInstrBox = new JComboBox<>();
    JComboBox<String> backgroundInstrBox = new JComboBox<>();
    JLabel scaleLabel = new JLabel("scale");
    JLabel mainInstrLabel = new JLabel("main instrument");
    JLabel backgroundInstrLabel = new JLabel("background instrument");
    JButton sampleButton = new JButton("play a sample");
    JSeparator separator = new JSeparator();

    scaleBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox scl = (JComboBox)e.getSource();
        String selectedScale = (String)scl.getSelectedItem();
        if (selectedScale.equals("phrygian")) currentScale = phrygianScale;
        else if (selectedScale.equals("blues")) currentScale = bluesScale;      
      }
    });

    mainInstrBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        channels[mainChnlNum].allNotesOff();
        JComboBox instr = (JComboBox)e.getSource();
        String selectedinstr = (String)instr.getSelectedItem();
        if (selectedinstr.equals("piano")) mainInstr = PIANO;
        else if (selectedinstr.equals("harp")) mainInstr = HARP;
        else if (selectedinstr.equals("celesta")) mainInstr = CELESTA;      
      }
    });

    backgroundInstrBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        channels[backgroundChnlNum].allNotesOff();
        JComboBox instr = (JComboBox)e.getSource();
        String selectedinstr = (String)instr.getSelectedItem();
        if (selectedinstr.equals("space")) mainInstr = SPACE;
        else if (selectedinstr.equals("harp")) mainInstr = HARP;
        else if (selectedinstr.equals("celesta")) mainInstr = CELESTA;      
      }
    });

    speedSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
         flock.setSpeed((int)source.getValue());
         //flock1.setSpeed((int)source.getValue());
       }
     }
   });

    repulsionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
       JSlider source = (JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
        flock.setRepulsionRange((int)source.getValue());
        //flock1.setRepulsionRange((int)source.getValue());
      }
    }
  });

    alignmentSlider.addChangeListener(new ChangeListener() {
     public void stateChanged(ChangeEvent e) {
       JSlider source = (JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
        flock.setAlignmentRange((int)source.getValue());
        //flock1.setAlignmentRange((int)source.getValue());
      }
    }
  });

    attractionSlider.addChangeListener(new ChangeListener() {
     public void stateChanged(ChangeEvent e) {
       JSlider source = (JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
        flock.setAttractionRange((int)source.getValue());
        //flock1.setAttractionRange((int)source.getValue());
      }
    }
  });

    sampleButton.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
       playSample();
     }
   });


    //////////////code generated using netbeans swing gui form//////////////

    setFont(new java.awt.Font("Lucida Sans", 0, 24)); 
    setPreferredSize(new java.awt.Dimension(1200, 320));

    scaleBox.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    scaleBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "phrygian", "blues"}));

    particlesLabel.setFont(new java.awt.Font("Lucida Sans", 0, 24)); 
    musicLabel.setFont(new java.awt.Font("Lucida Sans", 0, 24)); 
    repulsionLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    attractionLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    alignmentLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    speedLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    sampleButton.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 

    separator.setOrientation(javax.swing.SwingConstants.VERTICAL);
    separator.setToolTipText("");

    backgroundInstrBox.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    backgroundInstrBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "harp", "space", "celesta" }));

    backgroundInstrLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    scaleLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    mainInstrLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 

    mainInstrBox.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); 
    mainInstrBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "piano", "harp", "celesta" }));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(controls);
    controls.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                  .addGap(87, 87, 87)
                  .addComponent(speedLabel)
                  .addGap(3, 3, 3))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                  .addGap(54, 54, 54)
                  .addComponent(attractionLabel)))
              .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(alignmentLabel))
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(repulsionLabel)))
            .addGap(20, 20, 20)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(attractionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(alignmentSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(repulsionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(layout.createSequentialGroup()
            .addGap(44, 44, 44)
            .addComponent(particlesLabel)))
        .addGap(71, 71, 71)
        .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(musicLabel))
              .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(backgroundInstrLabel)
                .addGap(18, 18, 18)
                .addComponent(backgroundInstrBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(mainInstrLabel)
                .addGap(31, 31, 31))
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scaleLabel)
                .addGap(66, 66, 66)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(scaleBox, 0, 150, Short.MAX_VALUE)
              .addComponent(mainInstrBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
            .addComponent(sampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(40, 40, 40)))));

layout.setVerticalGroup(
  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
  .addGroup(layout.createSequentialGroup()
   .addGap(40, 40, 40)
   .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addComponent(particlesLabel)
    .addComponent(musicLabel, javax.swing.GroupLayout.Alignment.TRAILING))
   .addGap(40, 40, 40)
   .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
     .addGroup(layout.createSequentialGroup()
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(speedLabel))
      .addGap(25, 25, 25)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(attractionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(attractionLabel))
      .addGap(25, 25, 25)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(alignmentLabel)
        .addComponent(alignmentSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGap(25, 25, 25)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(repulsionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(repulsionLabel)))
     .addGroup(layout.createSequentialGroup()
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(scaleBox, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(scaleLabel)
        .addComponent(sampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGap(23, 23, 23)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(mainInstrBox, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(mainInstrLabel))
      .addGap(25, 25, 25)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(backgroundInstrLabel)
        .addComponent(backgroundInstrBox, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))))
   .addContainerGap(54, Short.MAX_VALUE))
  .addComponent(separator));

      /////////////////////////////////////////////

    }//initControlPanel

    // public void resetSliderValues(){
    //   flock.setSpeed(DEFAULT_SPEED);
    //   flock.setRepulsionRange(DEFAULT_REPULSION_RANGE);
    //   flock.setAlignmentRange(DEFAULT_ALIGNMENT_RANGE);
    //   flock.setAttractionRange(DEFAULT_ATTRACTION_RANGE);
    // }

    class DrawCanvas extends JPanel {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(CANVAS_COLOR);
        flock.draw(g);
        //flock1.draw(g);
      }
    }

    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
       @Override
       public void run() {new Simulator(flockSize);}
     });
   }//main

 }
