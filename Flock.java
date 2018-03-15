import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class Flock{
  private ArrayList<Particle> particles;
  public Flock(){
    particles = new ArrayList<Particle>();
  }

  public void addParticle(Particle p){particles.add(p);}
  public void removeParticle(Particle p){particles.remove(p);}

  public void move(){
    for (int i = 0; i < particles.size(); i++){
      int newHeading = particles.get(i).calcHeading(particles);
      particles.get(i).move(newHeading);
    }
  }

  public Point calcCentre(){
    int xSum = 0;
    int ySum = 0;
    for (int i = 0; i < particles.size(); i++) {
      xSum += particles.get(i).location.x;
      ySum += particles.get(i).location.y;
    }

    int n = particles.size();
    int x = (int)(xSum / n);
    int y = (int)(ySum / n);
    return new Point(x, y);
  }

  public boolean containsParticle(Particle p){
    return particles.contains(p);
  }

  public int getSize() {
    return particles.size();
  }
  
  public void setSpeed(int speed){
    for (int i = 0; i < particles.size(); i++){particles.get(i).setSpeed(speed);}
  }
  public void setRepulsionRange(int rep){
    for (int i = 0; i < particles.size(); i++){particles.get(i).setRepulsionRange(rep);}
  }
  public void setAlignmentRange(int ali){
    for (int i = 0; i < particles.size(); i++){particles.get(i).setAlignmentRange(ali);}
  }
  public void setAttractionRange(int att){
    for (int i = 0; i < particles.size(); i++){particles.get(i).setAttractionRange(att);}
  }

  public void draw(Graphics g) {
      for (int i = 0; i < particles.size(); i++) {particles.get(i).draw(g);}
  }
}
