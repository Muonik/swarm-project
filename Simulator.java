import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Simulator extends JFrame {
  public static final int CANVAS_WIDTH = 1000;
  public static final int CANVAS_HEIGHT = 800;
  public static final Color CANVAS_BG_COLOR = Color.BLACK;

  public DrawCanvas canvas;
  public Particle particle;

  public Simulator() {
    particle = new Particle(CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, 20);

    canvas = new DrawCanvas();
    canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(canvas, BorderLayout.CENTER);

    setTitle("A Particle I Suppose");
    pack();
    setVisible(true);
    requestFocus();
  }//Simulator constructor

  class DrawCanvas extends JPanel {
     @Override
     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(CANVAS_BG_COLOR);
        particle.draw(g);
    }
  }//class DrawCanvas

  public static void main(String[] args) {
     SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {new Simulator();}
     });
  }//main
}
