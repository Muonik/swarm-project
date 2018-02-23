import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class Particle {
  //define these in Particle or Simulator class...??
  // public static final int DEFAULT_SPEED = 20;
  // public static final int MAX_SPEED = 30;
  public static final int DEFAULT_REPULSION_RANGE = 40;
  public static final int DEFAULT_ALIGNMENT_RANGE = 90;
  public static final int DEFAULT_ATTRACTION_RANGE = 150;
  public static final int MAX_TURN_DEGREE = 45;

  public Point location = new Point(0, 0);
  private int radius, speed, heading;
  private Color color = Color.WHITE;
  private int repulsionRange = DEFAULT_REPULSION_RANGE;
  private int alignmentRange = DEFAULT_ALIGNMENT_RANGE;
  private int attractionRange = DEFAULT_ATTRACTION_RANGE;

  public Particle(int radius, int x, int y, int speed, int heading){
    this.radius = radius;
    location.x = x;
    location.y = y;
    this.speed = speed;
    this.heading = heading;
  }

  //rewriting in progress
  public int calcHeading(ArrayList<Particle> particles) {
    Point repVector = new Point(0, 0); 
    Point aliVector = new Point(0, 0); 
    Point attVector = new Point(0, 0);
    Point desiredDir = new Point(0, 0);
    boolean rep = false;
    boolean ali = false;
    boolean att = false;

    for (int i = 0; i < particles.size(); i++) {
      Particle other = particles.get(i);
      int distance = other.calcDistanceToPoint(location);

      if (distance > 0) {
      //not self
        if (distance <= repulsionRange) {
          Point r = normalise(sub(location, other.location));
          //add weight?
          repVector = add(repVector, r);
          rep = true;
        }
        else if (distance <= alignmentRange) {
          Point al = new Point((int)(Math.cos(Math.toRadians(other.heading))), (int)(Math.sin(Math.toRadians(other.heading))));
          aliVector = add(aliVector, al);
          ali = true;
        }
        else if (distance <= attractionRange) {
          Point at = sub(other.location, location);
          attVector = add(attVector, at);
          att = true;
        }
      }
    }//for

    if (rep) desiredDir = repVector;
    else if (ali && att) desiredDir = mult(add(normalise(aliVector), attVector), 0.5);
    else if (ali && !att) desiredDir = aliVector;
    else if (!ali && att) desiredDir = attVector;
    else desiredDir = normalise(location);

    int desiredHeading = (int)Math.toDegrees(Math.atan2(desiredDir.y, desiredDir.x));

    int turnDegree = desiredHeading - heading;
    if (Math.abs(turnDegree) > MAX_TURN_DEGREE){
      if (turnDegree > 0) desiredHeading = heading + MAX_TURN_DEGREE;
      else desiredHeading = heading - MAX_TURN_DEGREE;
    }

    return desiredHeading;
  }

  // public int calcHeading(ArrayList<Particle> particles) {
  //   Point target = new Point(0, 0);
  //   int newHeading = 0;
  //   int turnDegree;
  //   int numOfP = 0;

  //   for(int i = 0; i < particles.size(); i++) {
  //     Particle other = particles.get(i);
  //     int distance = other.calcDistanceToPoint(location);
  //     if(distance < attractionRange && distance > 0){
  //       if (distance > repulsionRange){
  //         //attraction
  //         Point att = other.getLocation();
  //         target = add(target, att);
  //       }
  //       else if (distance < repulsionRange) {
  //         //repulsion
  //         //highest priority (how?)
  //         // Point rep = sumPoints(location, subPoints(location, other.getLocation()));
  //         // target = sumPoints(target, rep);
  //         target = add(location, sub(location, other.getLocation()));
  //       }
  //       else {
  //         //alignment
  //         target = add(target, other.getLocation());
  //       }
  //     }
  //     numOfP++;
  //   }//for

  //   newHeading = (int)Math.toDegrees(Math.atan2(location.y - target.y, target.x - location.x));

  //   turnDegree = newHeading - heading;
  //   if (Math.abs(turnDegree) > MAX_TURN_DEGREE){
  //     if (turnDegree > 0) newHeading = heading + MAX_TURN_DEGREE;
  //     else newHeading = heading - MAX_TURN_DEGREE;
  //   }

  //   return newHeading;
  // }//calcHeading

  //update particle heading and location, taking edges into consideration
  public void move(int newHeading){
    heading = newHeading;
    location.x += speed * Math.cos(Math.toRadians(newHeading));
    location.y += speed * Math.sin(Math.toRadians(newHeading));

    //to do
    //need to get actual canvas width and height..
    if (location.x > 1000) {location.x = 0;}
    if (location.y > 800) {location.y = 0;}
    if (location.x < 0) {location.x = 1000;}
    if (location.y < 0) {location.y = 800;}
  }//move


  public Point add(Point p1, Point p2) {return new Point(p1.x + p2.x, p1.y + p2.y);}
  public Point sub(Point p1, Point p2) {return new Point(p1.x - p2.x, p1.y - p2.y);}
  public Point mult(Point p, double i) {return new Point ((int)(p.x*i), (int)(p.y*i));}
  // public Point normalise(Point p){
  //   //returns a unit vector
  //   double magnitude = Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.y, 2));
  //   return new Point((int)(p.x / magnitude), (int)(p.y / magnitude));
  // }

  public int calcDistanceToPoint(Point point){
    int dx = point.x - location.x;
    int dy = point.y - location.y;
    return (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }

  public void setSpeed(int speed){this.speed = speed;}
  public void setAttractionRange(int att){attractionRange = att;}
  public void setRepulsionRange(int rep){repulsionRange = rep;}

  public Point getLocation(){return location;}
  public int getHeading(){return heading;}

  public void draw(Graphics g){
    g.setColor(color);
    g.fillOval(location.x, location.y, radius, radius);
  }
}
