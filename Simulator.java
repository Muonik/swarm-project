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
  public static final int RECT_EDGE = 30;
  public static final Color CANVAS_COLOR = new Color(40,40,40);
  public static final Color PARTICLE_COLOR = new Color(242,242,242);
  public static final Color ATT_COLOR = new Color(0.706f, 0.855f, 0.969f); //blueish
  public static final Color AVR_COLOR = new Color(188, 221, 189); //greenish
  public static final Color INVISIBLE_AVR_COLOR = new Color(152,152,152); //grey
  public static final int UPDATE_INTERVAL = 30; //milliseconds

  public static final int[] phrygianScale = {4, 5, 8, 9, 11, 0, 2};
  public static final int[] bluesScale = {4, 6, 7, 8, 11, 1};
  public static final int[] hirajoshiScale = {4, 8, 10, 11, 3};
  public static final int PIANO = 2;
  public static final int HARP = 46;
  public static final int CELESTA = 8;
  public static final int SPACE = 91;

  private static int flockSize /* = 242;  //uncomment dis when running sim directly*/ ;

  private Clip audioClip;
  private Synthesizer synth;
  private MidiChannel[] channels;
  private int mainChnlNum = 4;
  private int backgroundChnlNum = 6;
  private File sample = new File("Oppenheimer.wav");
  private boolean samplePlaying = false;
  private int[] currentScale;
  private int mainInstr;
  private int backgroundInstr;
  private Particle attractor;
  private Point avr;
  private Point invisibleAvr;

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

    flock1 = new Flock();
    for(int i = 0; i < (int)(flockSize/2); i++) {
      flock1.addParticle(new Particle(RADIUS, (int)(Math.random()*CANVAS_WIDTH), (int)(Math.random()*CANVAS_HEIGHT), DEFAULT_SPEED, (int)(Math.random() * 360), PARTICLE_COLOR));
    }

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
          flock1.move();
          repaint(); 
          try {
            playNote();   
            playInvisibleFlock();
          }
          catch (MidiUnavailableException e) {}
          catch (InvalidMidiDataException e) {}
          try {
            Thread.sleep(UPDATE_INTERVAL); 
          }
        catch (InterruptedException e) {/*System.exit(0);*/}
      }
    } 
  };
  updateThread.start();
  }//Simulator

  public void playNote() throws MidiUnavailableException, InvalidMidiDataException {
    avr = flock.calcCentre();
    int pitch = (int)(avr.x / 16 + 30);
    int loudness = (int)(-avr.y / 10 + 160);
    
    channels[mainChnlNum].programChange(0,mainInstr); //bank and preset of the instrument 

    int rem = pitch % 12; 
    int rand = (int)(10*Math.random()); //p(note is played) = 1/8
    if(IntStream.of(currentScale).anyMatch(i -> i == rem) && rand == 0){
      channels[mainChnlNum].noteOn(pitch, loudness);
    }
    // synth.close();
  }

  public void playSample() {
    try {
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(sample);
      AudioFormat format = audioStream.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, format);
      audioClip = (Clip) AudioSystem.getLine(info);
      audioClip.open(audioStream);
      audioClip.start();
      samplePlaying = true;           
    }
    catch (UnsupportedAudioFileException e) {e.printStackTrace();} 
    catch (LineUnavailableException e) {e.printStackTrace();} 
    catch (IOException e) {e.printStackTrace();}
  }

  public void playInvisibleFlock() throws MidiUnavailableException, InvalidMidiDataException {
    invisibleAvr = flock1.calcCentre();
    int pitch = (int)(invisibleAvr.x / 16 + 30);
    int loudness = (int)(invisibleAvr.y / 10 + 110);

    if (backgroundInstr == SPACE) loudness -= 110;
    
    channels[backgroundChnlNum].programChange(0,backgroundInstr); 

    int rem = pitch % 12; 
    int rand = (int)(10*Math.random());
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
    JButton tipsButton = new JButton("tips");
    JSeparator separator = new JSeparator();

    scaleBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox scl = (JComboBox)e.getSource();
        String selectedScale = (String)scl.getSelectedItem();
        if (selectedScale.equals("phrygian")) currentScale = phrygianScale;
        else if (selectedScale.equals("blues")) currentScale = bluesScale;
        else if (selectedScale.equals("hirajoshi")) currentScale = hirajoshiScale;      
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
         flock1.setSpeed((int)source.getValue());
       }
     }
   });

    repulsionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
       JSlider source = (JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
        flock.setRepulsionRange((int)source.getValue());
        flock1.setRepulsionRange((int)source.getValue());
      }
    }
  });

    alignmentSlider.addChangeListener(new ChangeListener() {
     public void stateChanged(ChangeEvent e) {
       JSlider source = (JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
        flock.setAlignmentRange((int)source.getValue());
        flock1.setAlignmentRange((int)source.getValue());
      }
    }
  });

    attractionSlider.addChangeListener(new ChangeListener() {
     public void stateChanged(ChangeEvent e) {
       JSlider source = (JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
        flock.setAttractionRange((int)source.getValue());
        flock1.setAttractionRange((int)source.getValue());
      }
    }
  });

    sampleButton.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
       if(!samplePlaying) {
        playSample();
        sampleButton.setText("stop");
      } 
      else {
        audioClip.close();
        samplePlaying = false;
        sampleButton.setText("play a sample");
      } 
    }
  });

    tipsButton.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
      JDialog tipsDialog = new TipsDialog(new JFrame(), true);
      tipsDialog.setVisible(true);
      } 
    
  });

    //////////////code generated using netbeans swing gui form//////////////

    setFont(new Font("Lucida Sans", 0, 24)); 
    setPreferredSize(new Dimension(1200, 320));

    scaleBox.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    scaleBox.setModel(new DefaultComboBoxModel<>(new String[] { "phrygian", "blues", "hirajoshi"}));

    particlesLabel.setFont(new Font("Lucida Sans", 0, 24)); 
    musicLabel.setFont(new Font("Lucida Sans", 0, 24)); 
    repulsionLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    attractionLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    alignmentLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    speedLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    sampleButton.setFont(new Font("Lucida Sans Unicode", 0, 18)); 

    separator.setOrientation(SwingConstants.VERTICAL);
    separator.setToolTipText("");

    backgroundInstrBox.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    backgroundInstrBox.setModel(new DefaultComboBoxModel<>(new String[] { "harp", "space", "celesta" }));

    backgroundInstrLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    scaleLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    mainInstrLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); 

    mainInstrBox.setFont(new Font("Lucida Sans Unicode", 0, 18)); 
    mainInstrBox.setModel(new DefaultComboBoxModel<>(new String[] { "piano", "harp", "celesta" }));

    tipsButton.setFont(new Font("Lucida Sans Unicode", 0, 18)); // NOI18N
    tipsButton.setBackground(new Color(204,204,204));
    tipsButton.setForeground(new Color(69, 69, 69));
    tipsButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    tipsButton.setOpaque(false);

    separator.setOrientation(SwingConstants.VERTICAL);
    separator.setToolTipText("");

        backgroundInstrBox.setFont(new Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        backgroundInstrBox.setModel(new DefaultComboBoxModel<>(new String[] { "harp", "space", "celesta" }));

        backgroundInstrLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        backgroundInstrLabel.setText("background instrument");

        scaleLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        scaleLabel.setText("scale");

        mainInstrLabel.setFont(new Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        mainInstrLabel.setText("main instrument");

        mainInstrBox.setFont(new Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        mainInstrBox.setModel(new DefaultComboBoxModel<>(new String[] { "piano", "harp", "celesta" }));
        mainInstrBox.setPreferredSize(new Dimension(150, 45));

        sampleButton.setFont(new Font("Lucida Sans Unicode", 0, 18)); // NOI18N

        GroupLayout layout = new GroupLayout(controls);
        controls.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addComponent(speedLabel)
                                    .addGap(3, 3, 3))
                                .addComponent(attractionLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(alignmentLabel)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(repulsionLabel)))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(attractionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(alignmentSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(repulsionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(93, 93, 93))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(particlesLabel)
                        .addGap(57, 57, 57)))
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(musicLabel)
                        .addContainerGap(274, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(231, 231, 231)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(scaleBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mainInstrBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(mainInstrLabel)
                                    .addComponent(backgroundInstrLabel)
                                    .addComponent(scaleLabel))
                                .addGap(27, 27, 27)
                                .addComponent(backgroundInstrBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tipsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(989, Short.MAX_VALUE)
                    .addComponent(sampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(30, 30, 30)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(musicLabel)
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scaleBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scaleLabel))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainInstrBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainInstrLabel))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backgroundInstrBox, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backgroundInstrLabel))
                .addContainerGap(38, Short.MAX_VALUE))
            .addComponent(separator)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(particlesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(speedLabel))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(attractionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(attractionLabel))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(alignmentLabel)
                            .addComponent(alignmentSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(repulsionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(repulsionLabel))
                        .addGap(43, 43, 43))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(tipsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(103, 103, 103)
                    .addComponent(sampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(169, Short.MAX_VALUE)))
        );
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
        g.setColor(AVR_COLOR);
        g.drawRect((int)(avr.x - RECT_EDGE/2), (int)(avr.y - RECT_EDGE/2), RECT_EDGE, RECT_EDGE);
        g.setColor(INVISIBLE_AVR_COLOR);
        g.drawRect((int)(invisibleAvr.x - RECT_EDGE/2), (int)(invisibleAvr.y - RECT_EDGE/2), RECT_EDGE, RECT_EDGE);
      }
    }

    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
       @Override
       public void run() {new Simulator(flockSize);}
     });
   }//main

 }
