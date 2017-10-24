import java.awt.*;

public class Particle {
  private Point location = new Point(0, 0);
  private int speedx, speedy, radius;
  private Color color = Color.WHITE;
  private int maxSpeed = 20;

  public Particle(int radius, int x, int y, int speedx, int speedy){
    this.radius = radius;
    location.x = x;
    location.y = y;
    this.speedx = speedx;
    this.speedy = speedy;
  }

  public int calcDistance(Particle other){
    int dx = other.getLocation().x - location.x;
    int dy = other.getLocation().y - location.y;
    return (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2 ));
  }

  public void move(){
    location.x += speedx;
    location.y += speedy;
    //need to get canvas width and height.. or just ignore it i guess
    if (location.x > 1000 - radius || location.x <= 0) {speedx = - speedx;}
    if (location.y > 800 - radius || location.y <= 0) {speedy = - speedy;}
  }//move

  public void draw(Graphics g){
    g.setColor(color);
    g.fillOval(location.x, location.y, radius, radius);
  }

  public Point getLocation(){return location;}

  // public int getSpeedx() {return speedx;}
  // public int getSpeedy() {return speedy;}
  //
  // public void updateSpeedx(int newSpeed) {
  //   if(newSpeed <= maxSpeed) speedx = newSpeed;
  // }
  // public void updateSpeedy(int newSpeed) {
  //   if(newSpeed <= maxSpeed) speedy = newSpeed;
  // }
}
