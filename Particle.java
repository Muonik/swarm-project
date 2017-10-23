import java.awt.*;

public class Particle {
  private int x, y, radius;

 public Particle(int x, int y, int radius){
   this.x = x;
   this.y = y;
   this.radius = radius;
 }

 public void draw(Graphics g){
   g.setColor(Color.WHITE);
   g.drawOval(x, y, radius, radius);
 }

 public void getDistance(Particle other){
 }
}
