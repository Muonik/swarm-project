import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.awt.event.*;
import javax.swing.event.*;

public class TipsDialog extends JDialog{
	//// generated using netbeans gui form ////
	public TipsDialog(Frame parent, boolean modal){
		super(parent, modal);

		setTitle("some random bullshit");
		JScrollPane jScrollPane1 = new JScrollPane();
        JTextArea tipsText = new JTextArea();
        JButton fineIDontGiveAFuckButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tipsText.setEditable(false);
        tipsText.setBackground(new Color(240, 240, 240));
        tipsText.setColumns(20);
        tipsText.setFont(new Font("Lucida Sans", 0, 21)); 
        tipsText.setRows(5);
        tipsText.setText("the white particles are members of the main swarm. its \naverage is represented by the greenish square, whose \nposition determines which note is being played by the main \ninstrument\n\nthere is a hidden swarm which is half the size of the main \nswarm. its members are not visualised but the average is \nrepresented by the grey square. it is associated with the \nbackground instrument\n\nthe blue particle acts as an attractor in the main swarm, and \nit follows mouse movement\n\nthe sliders change the parameters of both swarms");
        tipsText.setAutoscrolls(false);
        tipsText.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportView(tipsText);

        fineIDontGiveAFuckButton.setFont(new Font("Lucida Sans", 0, 16)); // NOI18N
        fineIDontGiveAFuckButton.setText("but what if the opposite is true?");
        fineIDontGiveAFuckButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(fineIDontGiveAFuckButton, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                .addGap(250, 250, 250))
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 640, GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 371, GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(fineIDontGiveAFuckButton, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );

        pack();
	
	}
}