import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import peasy.*; 
import controlP5.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class WeatherVisualisation extends PApplet {

// Danny Keig 2017

//inspired by futuristic neon high rise

//You get the best view from street level, further back!


// Main patch handles the GUI, easyCam, grid and axis.





//Necessary for OSC communication with Wekinator:


OscP5 oscP5;
NetAddress dest;

PeasyCam cam;
ControlP5 cp5;
ColorPicker cpFrom;
ColorPicker cpTo;
ColorPicker cpBG;
boolean drawGUI;

//Incoming Weki parameters

// float p1 = 0.5;
// float p2 = 0.5;
// float p3 = 0.5;

float bgR, bgG, bgB, bgA;
float frR, frG, frB, frA;
float toR, toG, toB, toA;

float [] params = new float [26];

float test = 100;



// GUI vars
Textlabel fpsLbl;
boolean toggle_axis = true, toggle_grid = true;
float param_A, param_B;
float s = 0;
boolean drawAxis;
boolean drawGrid;
int snapshotCount = 0;

float g = 0.4f;

shapeSystem sys;



public void setup() {

  //Initialize OSC communication



oscP5 = new OscP5(this,12000); //listen for OSC messages on port 12000 (Wekinator default)
dest = new NetAddress("127.0.0.1",6448); //send messages back to Wekinator on port 6448, localhost (this machine) (default)

  // setupGUI();

  initParams();
  
  background(bgR,bgG, bgB, bgA);

  cam = new PeasyCam(this, 4000);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(5000);


  sys = new shapeSystem();

  sys.setupSystem();
}


public void draw() {


  // background(144, 197, 212);
  background(bgR, bgG, bgB, bgA);

lightSpecular(204, 204, 204);
  sys.update();
  sys.display();




  //optional axis and grid
  if (drawAxis)drawAxis();
  if (drawGrid)drawGrid(30, 100);

  // drawGUI overlayed
  if(drawGUI)drawGUI();


}


public void drawGrid(int numCells, float cellSize) {

  pushMatrix();
  pushStyle();
  translate(-numCells * cellSize /2, -numCells * cellSize /2, 0);
  for (int i = 0; i <= numCells; i++) {
    line(i*cellSize, 0, 0, i*cellSize, numCells * cellSize, 0);//rows
    line(0, i*cellSize, 0, numCells * cellSize, i*cellSize, 0);//columns
  }

  popMatrix();
  popStyle();
}

public void drawAxis() {
  int len = 300;
  pushStyle();
  strokeWeight(2);
  stroke(255, 0, 0 );
  line(0, 0, 0, len, 0, 0);//x
  stroke (0, 255, 0);
  line(0, 0, 0, 0, len, 0);//y
  stroke(0, 0, 255);
  line(0, 0, 0, 0, 0, len);//z


  strokeWeight(10);
  stroke(255, 0, 0 );
  point(len, 0, 0);//x
  stroke (0, 255, 0);
  point(0, len, 0);//y
  stroke(0, 0, 255);
  point(0, 0, len);//z
  popStyle();
}


public void keyPressed() {
  if (key == 'a') {
    drawAxis = !drawAxis;
  }
  if (key == 'g') {
    drawGrid = !drawGrid;
  }

  if (key == 's') {
    save("snapshot" + snapshotCount + ".jpg");
    snapshotCount++;
  }

  if (key == 'i'){
    drawGUI = !drawGUI;
  }
}



public void setupGUI() {
  cp5 = new ControlP5(this);
  cp5.setAutoDraw(false);

  // draw current framerate as a label
  fpsLbl = new Textlabel(cp5, "fps", 10, 10, 128, 16);

  // float sliders
  cp5.addSlider("szModSpeedX")
    .setPosition(10, 100)
    .setSize(128, 16)
    .setRange(0.f, 0.5f);

  cp5.addSlider("szModSpeedY")
    .setPosition(10, 120)
    .setSize(128, 16)
    .setRange(0.f, 0.5f);

  cp5.addSlider("szModSpeedZ")
    .setPosition(10, 140)
    .setSize(128, 16)
    .setRange(0.f, 0.5f);

  cp5.addSlider("szModRangeX")
    .setPosition(10, 160)
    .setSize(128, 16)
    .setRange(0.f, 10.f);

  cp5.addSlider("szModRangeY")
    .setPosition(10, 180)
    .setSize(128, 16)
    .setRange(0.f, 10.f);

  cp5.addSlider("szModRangeZ")
    .setPosition(10, 200)
    .setSize(128, 16)
    .setRange(0.f, 10.f);

  cp5.addSlider("numOfVertices")
    .setPosition(10, 220)
    .setSize(128, 16)
    .setRange(1, 80);

  cp5.addSlider("lineWidth")
    .setPosition(10, 240)
    .setSize(128, 16)
    .setRange(0.01f, 5.f);

  cp5.addSlider("lineWidthMovement")
      .setPosition(10, 260)
      .setSize(128, 16)
      .setRange(0, 0.5f);

  cp5.addSlider("lerpSpeed")
    .setPosition(10, 280)
    .setSize(128, 16)
    .setRange(0.f, 0.5f);

  cp5.addSlider("g")
    .setPosition(10, 300)
    .setSize(128, 16)
    .setRange(0, 50.f);

  cp5.addSlider("rotX")
  .setPosition(10, 320)
  .setSize(128, 16)
  .setRange(0, 1.5f);

  cp5.addSlider("rotY")
  .setPosition(10, 340)
  .setSize(128, 16)
  .setRange(0, 1.5f);

  cp5.addSlider("rotZ")
  .setPosition(10, 360)
  .setSize(128, 16)
  .setRange(0, 1.5f);


  cpFrom = cp5.addColorPicker("fromCol")
          .setPosition(width - 300, 50)
          .setColorValue(color(frR, frG, frB, frA))
          ;

  cpTo = cp5.addColorPicker("toCol")
          .setPosition(width - 300, 140)
          .setColorValue(color(toR, toG, toB, toA))
          ;

  // cpBG = cp5.addColorPicker("bgCol")
  //         .setPosition(width - 300, 220)
  //         .setColorValue(color(bgR, bgG, bgB, bgA))
  //         ;

}

public void drawGUI() {
  // do not move the camera while manipulating the gui
  if (cp5.isMouseOver()) {
    cam.setActive(false);
  } else {
    cam.setActive(true);
  }
  // draw the GUI outside of the camera's view
  hint(DISABLE_DEPTH_TEST);
  cam.beginHUD();
  cp5.draw();
  fpsLbl.setValueLabel("fps: " + floor(frameRate));
  fpsLbl.draw(this);
  cam.endHUD();
  hint(ENABLE_DEPTH_TEST);
}


//This is called automatically when OSC message is received
public void oscEvent(OscMessage theOscMessage) {
 if (theOscMessage.checkAddrPattern("/wek/outputs")==true) {
     if(theOscMessage.checkTypetag("ffffffffffffffffffffffffff")) { //Now looking for 2 parameters
        // p1 = theOscMessage.get(0).floatValue(); //get this parameter
        // p2 = theOscMessage.get(1).floatValue(); //get 2nd parameter
        // p3 = theOscMessage.get(2).floatValue(); //get third parameters

        for(int i = 0; i < 26; i ++){
        params[i] = theOscMessage.get(i).floatValue();
        }

        println("Received new params value from Wekinator");
      } else {
        println("Error: unexpected params type tag received by Processing");
      }


        // test = map(params[0], 0, 1, 0, 100);
        // float test2 = map(params[1], 0, 1, 0, 1000);

        szModSpeedX =  map(params[0], 0, 1, 0, 0.5f);
        szModSpeedY = map(params[1], 0, 1, 0, 0.5f);
        szModSpeedY = map(params[2], 0, 1, 0, 0.5f);
        szModRangeX = map(params[3], 0, 1, 0, 10.f);
        szModRangeY = map(params[4], 0, 1, 0, 10.f);
        szModRangeZ = map(params[5], 0, 1, 0, 10.f);
        numOfVertices = PApplet.parseInt(map(params[6], 0, 1, 0, 80));
        lineWidth = map(params[7], 0, 1, 0.01f, 5.f);
        lineWidthMovement = map(params[8], 0, 1, 0.f, 0.5f);
        lerpSpeed = map(params[9], 0, 1, 0.f, 0.5f);
        g = map(params[10], 0, 1, 0, 50);
        rotX = map(params[11], 0, 1, 0, 1.5f);
        rotY = map(params[12], 0, 1, 0, 1.5f);
        rotZ = map(params[13], 0, 1, 0, 1.5f);
        frR = map(params[14], 0, 1, 0, 255);
        frG = map(params[15], 0, 1, 0, 255);
        frB = map(params[16], 0, 1, 0, 255);
        frA = map(params[17], 0, 1, 0, 255);
        toR = map(params[18], 0, 1, 0, 255);
        toG = map(params[19], 0, 1, 0, 255);
        toB = map(params[20], 0, 1, 0, 255);
        toA = map(params[21], 0, 1, 0, 255);
        bgR = map(params[22], 0, 1, 0, 255);
        bgG = map(params[23], 0, 1, 0, 255);
        bgB = map(params[24], 0, 1, 0, 255);
        bgA = map(params[25], 0, 1, 0, 255);

        println(bgA);


 }
}


public void initParams(){


  szModSpeedX =  random(0, 0.5f);
  szModSpeedY = random(0, 0.5f);
  szModSpeedY = random(0, 0.5f);
  szModRangeX = random(0, 10);
  szModRangeY = random(0, 10);
  szModRangeZ = random(0, 10);
  numOfVertices = PApplet.parseInt(random(0, 80));
  lineWidth = random(0.01f, 5.f);
  lineWidthMovement = random(0.f, 0.5f);
  lerpSpeed = random( 0.f, 0.5f);
  g = random(0, 50);
  rotX = random( 0, 1.5f);
  rotY = random( 0, 1.5f);
  rotZ = random(0, 1.5f);

  frR = random(255);
  frG = random(255);
  frB = random(255);
  frA = random(255);
  toR = random(255);
  toG = random(255);
  toB = random(255);
  toA = random(255);
  bgR = random(255);
  bgG = random(255);
  bgB = random(255);
  bgA = random(255);




}
class Attractor{
   PVector position;
   float strength;
   float distance;
   float mass = 1;

   public Attractor(PVector pos, float s){
      position = pos.copy();
      strength = s;
   }

   public PVector attract(Shape s) {
    PVector force = PVector.sub(position ,s.position);   // Calculate direction of force
    float d = force.mag();                              // Distance between objects
    d = constrain(d,1.0f,10.0f);                        // Limiting the distance to eliminate "extreme" results for very close or very far objects
    force.normalize();                                  // Normalize vector (distance doesn't matter here, we just want this vector for direction)
    float str = (strength * mass * s.mass) / (d * d * 4 * PI);      // Calculate gravitional force magnitude
    force.mult(str);                                  // Get force vector --> magnitude * direction
    return force;
  }

}


class QuadVertex{

  float cx, cy, cz, x, y, z, r;

public QuadVertex(PVector control, PVector pos, float offset){
   cx = control.x;
   cy = control.y;
   cz = control.z;
   x = pos.x;
   y = pos.y;
   z = pos.z;
   r = offset;
 }
}

// Attractor function and apply force function directly from Daniel Shiffman's Nature of Code!

float szModX, szModY, szModZ;
float szModSpeedX = 0;
float szModSpeedY = 0;
float szModSpeedZ = 0;
float szModRangeX = 1;
float szModRangeY = 1;
float szModRangeZ = 1;
float lineWidth = 1;
float lineWidthMovement;


float rotX;
float rotY;
float rotZ;

int numOfVertices;
float massScaler;
float lerpSpeed;

class Shape {


  ArrayList <QuadVertex> vertices;

  PVector size;
  PVector position;
  PVector acceleration;
  PVector angle;
  PVector velocity;
  PVector rtStep;
  float shapeColourLerp;
  float mass = random(0.1f,2);
  int verticesMax = 80;
  float randomOffset;


  public Shape() {
    int d = 600;
    size = new PVector(random(20, 100), random(20, 100), random(20, 100));//w, h, d
    position = new PVector(random(500), random(500), random(500));
    angle = new PVector(random(TWO_PI), random(TWO_PI), random(TWO_PI));//rx, ry, rz
    shapeColourLerp = random(1.f);
    randomOffset = random(1000);
    velocity = new PVector(random(-1.f,1.f),random(-1.f,1.f),random(-1.f,1.f));
    acceleration = new PVector(0,0,0);
    rtStep = new PVector(0.1f, 0.1f, 0.1f);

    vertices = new ArrayList <QuadVertex>();

    for(int i = 0; i < verticesMax; i ++){
      PVector tempControl;
      PVector tempPos;
      tempControl = new PVector(random(150),random(150),random(150));
      tempPos = new PVector(random(150),random(150),random(150));
      float r = random(- 5.f, 5.f);
      QuadVertex q = new QuadVertex(tempControl, tempPos, r);
      vertices.add(q);
      // vertices.add(new QuadVertex(tempControl, tempPos));
      // QuadVertex q = new QuadVertex(tempControl, tempPos);
    }
  }


  public void setupShape(PVector initSize, PVector initPos, PVector initRot) {
    size = initSize.copy();
    position = initPos.copy();
    //position.x = random(800);
    angle = initRot.copy();
    //shapeColour = initCol;
  }


  public void update(){



    rtStep.x = rotX;
    rtStep.y = rotY;
    rtStep.z = rotZ;

    velocity.add(acceleration);
    position.add(velocity);
    acceleration.mult(0);
    angle.add(rtStep);

    // angle.z += sin(frameCount * 0.3);

    boundaries();
    shapeColourLerp = map(sin(frameCount * lerpSpeed + randomOffset), -1, 1, 0.f, 1.f);
    if(lineWidthMovement > 0){
    lineWidth = map(noise(frameCount * lineWidthMovement + randomOffset), 0, 1, 0.01f, 5.f);
    }
  }

  public void display() {

    szMod();
    //shapeColour =  color(random(255), random(255), random(255));

    pushMatrix();

    //position.x = random(800);
    // rtStep = map(mouseX, 0, width, 0, 100.);

    translate(position.x,position.y,position.z);
    rotateX(angle.x);
    rotateY(angle.y);
    rotateZ(angle.z);
    size.x += szModX;
    size.y += szModY;
    size.z += szModZ;
    //box(size.x  - (i*szModX), size.y  - (i*szModY), size.z  - (i*szModZ));
    // box(size.x, size.y, size.z);


    noFill();
    strokeWeight(lineWidth);
    beginShape();
    vertex(20, 20);
    unpackVertices();
    vertex(80, 60);
    endShape();
    popMatrix();
  }


// keep the shapes on the canvas if heading away!
  public void boundaries(){
    if(position.x > 1000 || position.x < -1000){
      velocity.x *= (-1);
    }
    if(position.y > 1000 || position.y < -1000){
      velocity.y *= (-1);
      }
    if(position.z > 1000 || position.z < -1000){
      velocity.z *= (-1);
       }
  }

  public void unpackVertices(){

    // float numOfVertices = map(mouseX, 0, width, 2, vertices.size());
    for(int i = 0; i < numOfVertices; i ++){
      QuadVertex q = vertices.get(i);
      float r = q.r;
      float cx = q.cx + (szModX * i * r);
      float cy = q.cy + (szModY * i * r);
      float cz = q.cz + (szModZ * i * r);
      float x = q.x + (szModX * i * r);
      float y = q.y + (szModY * i * r);
      float z = q.z + (szModZ * i * r);
      quadraticVertex(cx, cy, cz, x, y, z);

    }


  }

  public PVector attract(Shape s) {
    PVector force = PVector.sub(position, s.position);             // Calculate direction of force
    float distance = force.mag();                                 // Distance between objects
    distance = constrain(distance, 5.0f, 25.0f);                             // Limiting the distance to eliminate "extreme" results for very close or very far objects
    force.normalize();                                            // Normalize vector (distance doesn't matter here, we just want this vector for direction

    float strength = (g * mass * s.mass) / (distance * distance); // Calculate gravitional force magnitude
    force.mult(strength);                                         // Get force vector --> magnitude * direction
    return force;
  }

  public void applyForce(PVector force) {
    PVector f = PVector.div(force, mass);
    acceleration.add(f);
  }





}


public void szMod() {
  szModX = map(sin(frameCount * szModSpeedX), -1, 1, -szModRangeX, szModRangeX);
  szModY = map(sin(frameCount * szModSpeedY), -1, 1, -szModRangeY, szModRangeY);
  szModZ = map(sin(frameCount * szModSpeedZ), -1, 1, -szModRangeZ, szModRangeZ);
}
int numOfShapes;
ArrayList <Shape> shapes;
Shape s1;
Shape s2;



class shapeSystem {

  int fromCol;
  int toCol;


  public void setupSystem() {
    numOfShapes = 30;

    shapes = new ArrayList <Shape>();

    // fromCol = color(random(255), random(255), random(255));
    // toCol = color(random(255), random(255), random(255));


    //  //initialise them
    //positions = new ArrayList<PVector>();
    //sizes = new ArrayList<PVector>();
    //rotations = new ArrayList<PVector>();
    //shapeColour = new ArrayList<PVector>();

    //building dimensions
    for (int i = 0; i < numOfShapes; i++) {
      Shape s = new Shape();
      //s.setupShape(tempSiz, tempPos, tempRot);
      shapes.add(s);

    }
  }

public void update(){



fromCol =  color(frR, frG, frB, frA);
toCol =  color(toR, toG, toB, toA);

  for (int i = 0; i < shapes.size(); i++) {
    for (int j = 0; j < shapes.size(); j++) {
      if (i != j) {
        Shape Si = shapes.get(i);
        Shape Sj = shapes.get(j);
        PVector force = Sj.attract(Si);
        Si.applyForce(force);
      }
    }
  }


  for (int i = 0; i < shapes.size(); i++) {
    Shape s = shapes.get(i);
    s.update();
  }

}

  public void display() {

    pushStyle();

    for (int i = 0; i < shapes.size(); i++) {

      //pushMatrix();

      Shape s = shapes.get(i);

//translate(random(500), random(500), random(500));
      int c = lerpColor(fromCol, toCol, s.shapeColourLerp);
      stroke(c);
      s.display();

      //popMatrix();

    }
    popStyle();
  }
}
  public void settings() {  size(1200, 800, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "WeatherVisualisation" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
