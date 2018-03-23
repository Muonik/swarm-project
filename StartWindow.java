import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class StartWindow extends JFrame {
	private int flockSize = 100;
	private int width = 1200;
	private int height = 900;
	private JPanel mainPanel;
	private JPanel defaultScreen;
	private JPanel aboutScreen;
	private Image backgroundImg, scaledImg;

	public StartWindow() {
		try {                
			backgroundImg = ImageIO.read(new File("perlin2.png"));
			scaledImg = backgroundImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		}
		catch (IOException e){}

		initComponents();
		setSize(width, height);
		setResizable(false);
		setTitle("pseudo experimental music generator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//pack();
		setVisible(true);
	}

	private void initComponents() {
		mainPanel = new Background();
		defaultScreen = new JPanel();
		aboutScreen = new JPanel();
		JLabel titleLabel = new JLabel();
		JButton startButton = new JButton("start");
		JSlider flockSizeSlider = new JSlider();
		JLabel flockSizeLabel = new JLabel("uhh how many particles you need?");
		JButton whatIsThisButton = new JButton("what is this?");
		JScrollPane textScrollPane = new JScrollPane();
		JTextArea aboutText = new JTextArea();
		JButton okayGotItButton = new JButton("okay got it");

		flockSizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				flockSize = (int)source.getValue();
			}
		});

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulator sim = new Simulator(flockSize);
				sim.setVisible(true);
				setVisible(false);
			}
		});

		whatIsThisButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				CardLayout card = (CardLayout)mainPanel.getLayout();
				card.show(mainPanel, "aboutScreen");
			}
		});

		okayGotItButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				CardLayout card = (CardLayout)mainPanel.getLayout();
				card.show(mainPanel, "defaultScreen");
			}
		});

        ////////// code generated using netbeans gui form //////////
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setLayout(new java.awt.CardLayout());

        defaultScreen.setMaximumSize(new java.awt.Dimension(1200, 800));
        defaultScreen.setMinimumSize(new java.awt.Dimension(1200, 800));
        defaultScreen.setOpaque(false);

        titleLabel.setFont(new java.awt.Font("Lucida Sans", 0, 48)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(242, 242, 242));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("pseudo experimental music generator");

        startButton.setBackground(new java.awt.Color(233,233,233));
        startButton.setFont(new java.awt.Font("Lucida Sans", 0, 24)); // NOI18N
        // startButton.setForeground(new Color(242,242,242));
        startButton.setText("start");
        startButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        flockSizeSlider.setBackground(new java.awt.Color(40, 40, 40));
        flockSizeSlider.setForeground(new Color(233,233,233));
        flockSizeSlider.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        flockSizeSlider.setMajorTickSpacing(100);
        flockSizeSlider.setMaximum(242);
        flockSizeSlider.setMinimum(10);
        flockSizeSlider.setMinorTickSpacing(10);
        flockSizeSlider.setPaintLabels(true);
        flockSizeSlider.setPaintTicks(true);
        flockSizeSlider.setToolTipText("");

        flockSizeLabel.setFont(new java.awt.Font("Lucida Sans", 0, 21)); // NOI18N
        flockSizeLabel.setForeground(new java.awt.Color(233, 233, 233));
        flockSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        flockSizeLabel.setText("uhh how many particles you need?");

        whatIsThisButton.setBackground(new java.awt.Color(204, 204, 204));
        whatIsThisButton.setFont(new java.awt.Font("Lucida Sans", 0, 21)); // NOI18N
        whatIsThisButton.setForeground(new java.awt.Color(233, 233, 233));
        whatIsThisButton.setText("what is this?");
        whatIsThisButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        whatIsThisButton.setOpaque(false);

        javax.swing.GroupLayout defaultScreenLayout = new javax.swing.GroupLayout(defaultScreen);
        defaultScreen.setLayout(defaultScreenLayout);
        defaultScreenLayout.setHorizontalGroup(
            defaultScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 1200, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(defaultScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                        .addComponent(flockSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(370, 370, 370))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                        .addComponent(flockSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(379, 379, 379))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(480, 480, 480))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                        .addComponent(whatIsThisButton, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))))
        );
        defaultScreenLayout.setVerticalGroup(
            defaultScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(defaultScreenLayout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(145, 145, 145)
                .addComponent(flockSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(flockSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(whatIsThisButton, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );

        mainPanel.add(defaultScreen, "defaultScreen");

        aboutScreen.setOpaque(false);

        textScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        textScrollPane.setOpaque(false);
        textScrollPane.getViewport().setOpaque(false);

        aboutText.setEditable(false);
        aboutText.setBackground(new java.awt.Color(240, 240, 240));
        aboutText.setColumns(20);
        aboutText.setFont(new java.awt.Font("Lucida Sans", 0, 24)); 
        aboutText.setForeground(new java.awt.Color(233, 233, 233));
        aboutText.setRows(5);
        aboutText.setText("a java program that interactively plays\ntrashy experimental hipster music\nbased on couzin's model of flocking\n");
        aboutText.setAutoscrolls(false);
        aboutText.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        aboutText.setOpaque(false);
        textScrollPane.setViewportView(aboutText);

        okayGotItButton.setBackground(new java.awt.Color(204, 204, 204));
        okayGotItButton.setFont(new java.awt.Font("Lucida Sans", 0, 21)); 
        okayGotItButton.setForeground(new java.awt.Color(233, 233, 233));
        okayGotItButton.setText("okay got it");
        okayGotItButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        okayGotItButton.setOpaque(false);

        javax.swing.GroupLayout aboutScreenLayout = new javax.swing.GroupLayout(aboutScreen);
        aboutScreen.setLayout(aboutScreenLayout);
        aboutScreenLayout.setHorizontalGroup(
            aboutScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutScreenLayout.createSequentialGroup()
                .addContainerGap(401, Short.MAX_VALUE)
                .addGroup(aboutScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutScreenLayout.createSequentialGroup()
                        .addComponent(textScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(341, 341, 341))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutScreenLayout.createSequentialGroup()
                        .addComponent(okayGotItButton, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))))
        );
        aboutScreenLayout.setVerticalGroup(
            aboutScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutScreenLayout.createSequentialGroup()
                .addContainerGap(314, Short.MAX_VALUE)
                .addComponent(textScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(307, 307, 307)
                .addComponent(okayGotItButton, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );

        mainPanel.add(aboutScreen, "aboutScreen");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        
        /////////////////////////////////////////////////
    }

    class Background extends JPanel {
    	@Override
    	public void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		g.drawImage(scaledImg, 0, 0, null);
    	}
    }

    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
    		@Override
    		public void run() {new StartWindow();}
    	});
    }
}
