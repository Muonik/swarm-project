import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Simulator extends JFrame {
  public static final int CANVAS_WIDTH = 1000;
  public static final int CANVAS_HEIGHT = 800;
  public static final int RADIUS = 20;
  public static final Color CANVAS_COLOR = Color.BLACK;
  public static final int UPDATE_INTERVAL = 50; //milliseconds

  public DrawCanvas canvas;
  public Particle p1, p2, p3;

  public Simulator() {
    p1 = new Particle(RADIUS, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, 15 ,10);
    p2 = new Particle(RADIUS, CANVAS_WIDTH / 2 + 20, CANVAS_HEIGHT / 2 - 20, 10 ,5);
    p3 = new Particle(RADIUS, CANVAS_WIDTH / 2 + 20, CANVAS_HEIGHT / 2 - 20, 10 ,20);

    canvas = new DrawCanvas();
    canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(canvas, BorderLayout.CENTER);

    setTitle("uhh some shitty java program");
    pack();
    setVisible(true);
    //requestFocus();

    Thread updateThread = new Thread() {
      @Override
      public void run() {
        while (true) {
          p1.move();
          p2.move();
          p3.move();
          repaint();  // Refresh the JFrame. Called back paintComponent()
          try {
            Thread.sleep(UPDATE_INTERVAL);  // milliseconds
          } catch (InterruptedException ignore) {}
        }//while
      }
    };
    updateThread.start();
  }//Simulator constructor

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
