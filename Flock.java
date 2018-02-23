import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class Flock{
  private ArrayList<Particle> particles;
  public Flock(){
    particles = new ArrayList<Particle>();
  }

  public void addParticle(Particle p){particles.add(p);}

  public void move(){
    for (int i = 0; i < particles.size(); i++){
      int newHeading = particles.get(i).calcHeading(particles);
      particles.get(i).move(newHeading);
    }
  }

  //to do
  public Point calcSomethingIDK(){
    int xSum = 0;
    int ySum = 0;
    for (int i = 0; i < particles.size(); i++) {
      xSum += particles.get(i).location.x;
      ySum += particles.get(i).location.y;
    }

    int n = particles.size();
    int pitch = (int)(xSum / n);
    int loudness = (int)(ySum / n);
    return new Point(pitch, loudness);
  }

  //methods that set the particles' params. for sliders in the simulator class
  public void setSpeed(int speed){
    for (int i = 0; i < particles.size(); i++){particles.get(i).setSpeed(speed);}
  }
  public void setAttractionRange(int att){
    for (int i = 0; i < particles.size(); i++){particles.get(i).setAttractionRange(att);}
  }
  public void setRepulsionRange(int rep){
    for (int i = 0; i < particles.size(); i++){particles.get(i).setRepulsionRange(rep);}
  }

  public void draw(Graphics g) {
      for (int i = 0; i < particles.size(); i++) {particles.get(i).draw(g);}
  }
}
