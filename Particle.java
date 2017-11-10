import java.awt.*;

public class Particle {
  private Point location = new Point(0, 0);
  private int radius, speed, heading;
  private Color color = Color.WHITE;
  //private int maxSpeed = 20;

  public Particle(int radius, int x, int y, int speed, int heading){
    this.radius = radius;
    location.x = x;
    location.y = y;
    this.speed = speed;
    this.heading = heading;
  }

  public int calcDistance(Particle other){
    int dx = other.getLocation().x - location.x;
    int dy = other.getLocation().y - location.y;
    return (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2 ));
  }

  public void move(int newHeading){
    heading = newHeading;
    location.x += speed * Math.cos(Math.toRadians(newHeading));
    location.y += speed * Math.sin(Math.toRadians(newHeading));
    //need to get canvas width and height.. or just ignore it i guess
    if (location.x > 1000 - radius) {location.x -= 1000;}
    if (location.y > 800 - radius) {location.y -= 800;}
    if (location.x < 0) {location.x += 1000;}
    if (location.y < 0) {location.y += 800;}
  }//move

  public void draw(Graphics g){
    g.setColor(color);
    g.drawOval(location.x, location.y, radius, radius);
  }

  public int getSpeed(){return speed;}
  public void setSpeed(int newSpeed){speed = newSpeed;}

  public Point getLocation(){return location;}
  public int getHeading(){return heading;}
}
