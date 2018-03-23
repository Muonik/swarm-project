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
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setLayout(new java.awt.CardLayout());

        defaultScreen.setMaximumSize(new java.awt.Dimension(1200, 800));
        defaultScreen.setMinimumSize(new java.awt.Dimension(1200, 800));
        defaultScreen.setOpaque(false);

        titleLabel.setFont(new java.awt.Font("Lucida Sans", 0, 48)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(242, 242, 242));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
        flockSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        flockSizeLabel.setText("uhh how many particles you need?");

        whatIsThisButton.setBackground(new java.awt.Color(204, 204, 204));
        whatIsThisButton.setFont(new java.awt.Font("Lucida Sans", 0, 21)); // NOI18N
        whatIsThisButton.setForeground(new java.awt.Color(233, 233, 233));
        whatIsThisButton.setText("what is this?");
        whatIsThisButton.setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));
        whatIsThisButton.setOpaque(false);

        GroupLayout defaultScreenLayout = new GroupLayout(defaultScreen);
        defaultScreen.setLayout(defaultScreenLayout);
        defaultScreenLayout.setHorizontalGroup(
            defaultScreenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, 1200, Short.MAX_VALUE)
            .addGroup(GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(defaultScreenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                        .addComponent(flockSizeLabel, GroupLayout.PREFERRED_SIZE, 455, GroupLayout.PREFERRED_SIZE)
                        .addGap(370, 370, 370))
                    .addGroup(GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                        .addComponent(flockSizeSlider, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE)
                        .addGap(379, 379, 379))
                    .addGroup(GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                        .addComponent(startButton, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
                        .addGap(480, 480, 480))
                    .addGroup(GroupLayout.Alignment.TRAILING, defaultScreenLayout.createSequentialGroup()
                        .addComponent(whatIsThisButton, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))))
        );
        defaultScreenLayout.setVerticalGroup(
            defaultScreenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(defaultScreenLayout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addComponent(titleLabel, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
                .addGap(145, 145, 145)
                .addComponent(flockSizeLabel, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(flockSizeSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(startButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(whatIsThisButton, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );

        mainPanel.add(defaultScreen, "defaultScreen");

        aboutScreen.setOpaque(false);

        textScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
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
        aboutText.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        aboutText.setOpaque(false);
        textScrollPane.setViewportView(aboutText);

        okayGotItButton.setBackground(new java.awt.Color(204, 204, 204));
        okayGotItButton.setFont(new java.awt.Font("Lucida Sans", 0, 21)); 
        okayGotItButton.setForeground(new java.awt.Color(233, 233, 233));
        okayGotItButton.setText("okay got it");
        okayGotItButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        okayGotItButton.setOpaque(false);

        GroupLayout aboutScreenLayout = new GroupLayout(aboutScreen);
        aboutScreen.setLayout(aboutScreenLayout);
        aboutScreenLayout.setHorizontalGroup(
            aboutScreenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(aboutScreenLayout.createSequentialGroup()
                .addContainerGap(401, Short.MAX_VALUE)
                .addGroup(aboutScreenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, aboutScreenLayout.createSequentialGroup()
                        .addComponent(textScrollPane, GroupLayout.PREFERRED_SIZE, 458, GroupLayout.PREFERRED_SIZE)
                        .addGap(341, 341, 341))
                    .addGroup(GroupLayout.Alignment.TRAILING, aboutScreenLayout.createSequentialGroup()
                        .addComponent(okayGotItButton, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))))
        );
        aboutScreenLayout.setVerticalGroup(
            aboutScreenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(aboutScreenLayout.createSequentialGroup()
                .addContainerGap(314, Short.MAX_VALUE)
                .addComponent(textScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(307, 307, 307)
                .addComponent(okayGotItButton, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );

        mainPanel.add(aboutScreen, "aboutScreen");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
