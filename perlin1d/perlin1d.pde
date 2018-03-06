//float offset1 = 0;
//float offset2 = 1000;
float inc = 0.01;
float y = 0;

void setup(){
  size(1600, 1200);
  background(40);
}

void draw() {
  //background(0);
  stroke(255);
  noFill();
  
  beginShape();
  float offset = 0;
  stroke(255);
  for(int x = 0; x < width; x++) {
    vertex(x*5, (noise(offset,y)*height/2)+500);
    offset += inc;
  }
  endShape();
  y += inc;
}