import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

public class Particle {
  //define these in Particle or Simulator class...??
  public static final int DEFAULT_REPULSION_RANGE = 30;
  public static final int DEFAULT_ALIGNMENT_RANGE = 50;
  public static final int DEFAULT_ATTRACTION_RANGE = 210;
  public static final int MAX_TURN_DEGREE = 30;
  public static final int CANVAS_WIDTH = 1200;
  public static final int CANVAS_HEIGHT = 900;

  public Point location = new Point(0, 0);
  private int radius, speed, heading;
  private Color color;
  private int repulsionRange = DEFAULT_REPULSION_RANGE;
  private int alignmentRange = DEFAULT_ALIGNMENT_RANGE;
  private int attractionRange = DEFAULT_ATTRACTION_RANGE;

  public Particle(int radius, int x, int y, int speed, int heading, Color color){
    this.radius = radius;
    location.x = x;
    location.y = y;
    this.speed = speed;
    this.heading = heading;
    this.color = color;
  }

  public int calcHeading(ArrayList<Particle> particles) {
    Point repVector = new Point(0, 0); 
    Point aliVector = new Point(0, 0); 
    Point attVector = new Point(0, 0);
    //Point attractorVector = new Point(0,0);
    Point desiredDir = new Point(0, 0);
    boolean rep = false;
    boolean ali = false;
    boolean att = false;
    //boolean attractor = false;

    for (int i = 0; i < particles.size(); i++) {
      Particle other = particles.get(i);
      Point realLoc = realLocation(location, other.location);
      int distance = calcDistanceToPoint(realLoc);

      if (distance > 0 && !other.equals(this)) {
        // if (other.color != Color.WHITE && distance < attractionRange) {
        //   attractorVector = mult(sub(other.location, location),15);
        //   attractor = true;
        // } 
        if (distance <= repulsionRange) {
          Point r = sub(location, realLoc);
          repVector = add(repVector, r);
          rep = true;
        }
        else if (distance <= alignmentRange) {
          double h = Math.toRadians(other.heading);
          Point al = new Point((int)(Math.cos(h)), (int)(Math.sin(h)));
          aliVector = add(aliVector, al);
          ali = true;
        }
        else if (distance <= attractionRange) {
          Point at = sub(realLoc, location);
          if (other.color != Color.WHITE) at = mult(at, 2000);
          attVector = add(attVector, at);
          att = true;
        }
      }
    }//for

    if (rep) desiredDir = repVector;
    else if (ali && att) desiredDir = mult(add(aliVector, attVector), 0.5); 
    else if (ali && !att) desiredDir = aliVector;
    else if (!ali && att) desiredDir = attVector;
    else desiredDir = location;

    int desiredHeading = (int)Math.toDegrees(Math.atan2(desiredDir.y, desiredDir.x));
    return desiredHeading;
  }

  public void move(int desiredHeading){
    int delta = desiredHeading - heading;
    if (Math.abs(delta) > MAX_TURN_DEGREE) {
      if (delta > 0) desiredHeading = heading +  MAX_TURN_DEGREE; 
      else desiredHeading = heading - MAX_TURN_DEGREE;
    } 
    heading = desiredHeading;

    location.x += speed * Math.cos(Math.toRadians(heading));
    location.y += speed * Math.sin(Math.toRadians(heading));

    if (location.x > CANVAS_WIDTH + radius) {location.x = 0;}
    if (location.y > CANVAS_HEIGHT + radius) {location.y = 0;}
    if (location.x < -radius) {location.x = CANVAS_WIDTH;}
    if (location.y < -radius) {location.y = CANVAS_HEIGHT;}
  }

  public int calcDistanceToPoint(Point p) {
    int dx = p.x - location.x;
    int dy = p.y - location.y;
    return (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }

  public Point realLocation(Point p,Point other) {
    int dx = other.x - p.x;
    int dy = other.y - p.y;
    int x = other.x;
    int y = other.y;

    if(dx >= (CANVAS_WIDTH - Math.abs(dx))) {
      if(dx > 0) x = other.x - CANVAS_WIDTH;
      else x = other.x + CANVAS_WIDTH;
    }

    if(dy >= (CANVAS_HEIGHT - Math.abs(dy))) {
      if(dy > 0) y = other.y -  CANVAS_HEIGHT;
      else y = other.y + CANVAS_HEIGHT;
    }
    return new Point(x,y);
  }

  public Point add(Point p1, Point p2) {return new Point(p1.x + p2.x, p1.y + p2.y);}
  public Point sub(Point p1, Point p2) {return new Point(p1.x - p2.x, p1.y - p2.y);}
  public Point mult(Point p, double i) {return new Point ((int)(p.x*i), (int)(p.y*i));}
  //to do
  public boolean inBlindSpot(Point p) {
    // if(calcDistanceToPoint(p) <= attractionRange) {

    // }

    return false;
  }

  public boolean equals(Particle p) {
    if(p==this) return true;
    else return false;
  }

  public int hashCode() {
    return Objects.hash(radius, location.x, location.y, speed, heading, color);
  }

  public void setSpeed(int speed) {this.speed = speed;}
  public void setRepulsionRange(int rep) {repulsionRange = rep;}
  public void setAlignmentRange(int ali) {alignmentRange = ali;}
  public void setAttractionRange(int att) {attractionRange = att;}

  public Point getLocation(){return location;}
  public void setLocation(Point p){location = p;}

  public void draw(Graphics g){
    g.setColor(color);
    g.fillOval(location.x, location.y, radius, radius);
  }
}
