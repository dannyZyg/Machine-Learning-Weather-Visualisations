// Danny Keig 2017

// Template from Lior Ben Gai's Artifact Lab (Programming for Artists II)
// Particle system behaviours inspired by Daniel Shiffman's Nature of Code Material
// 
// GUI functionality disabled, please use Wekinator Project, or the openWeatherAPI.pde along with Wekinator as intended.
//
// Main patch handles the GUI, easyCam, grid and axis.


import peasy.*;
import controlP5.*;

//Necessary for OSC communication with Wekinator:
import oscP5.*;
import netP5.*;
OscP5 oscP5;
NetAddress dest;

PeasyCam cam;
ControlP5 cp5;
ColorPicker cpFrom;
ColorPicker cpTo;
ColorPicker cpBG;
boolean drawGUI;

float bgR, bgG, bgB, bgA;
float frR, frG, frB, frA;
float toR, toG, toB, toA;

float [] params = new float [26];


// GUI vars
Textlabel fpsLbl;
boolean toggle_axis = true, toggle_grid = true;
float param_A, param_B;
float s = 0;
boolean drawAxis;
boolean drawGrid;
int snapshotCount = 0;

float g = 0.4;

shapeSystem sys;

void setup() {

  //Initialize OSC communication

  oscP5 = new OscP5(this, 12000); //listen for OSC messages on port 12000 (Wekinator default)
  dest = new NetAddress("127.0.0.1", 6448); //send messages back to Wekinator on port 6448, localhost (this machine) (default)


  // GUI was initially useful for creating my mappable parameters, replaced by Wekinator GUI in the end.
  //setupGUI();

  initParams();
  size(1200, 800, P3D);
  //fullScreen(P3D);
  background(bgR, bgG, bgB, bgA);

  cam = new PeasyCam(this, 4000);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(5000);


  sys = new shapeSystem();

  sys.setupSystem();
}


void draw() {


  background(bgR, bgG, bgB, bgA);

  lightSpecular(204, 204, 204);
  sys.update();
  sys.display();




  //optional axis and grid
  if (drawAxis)drawAxis();
  if (drawGrid)drawGrid(30, 100);

  // drawGUI overlayed
  if (drawGUI)drawGUI();
}


void drawGrid(int numCells, float cellSize) {

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

void drawAxis() {
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


void keyPressed() {
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

  //if (key == 'i') {
  //  drawGUI = !drawGUI;
  //}
}



void setupGUI() {
  cp5 = new ControlP5(this);
  cp5.setAutoDraw(false);

  // draw current framerate as a label
  fpsLbl = new Textlabel(cp5, "fps", 10, 10, 128, 16);

  // float sliders
  cp5.addSlider("szModSpeedX")
    .setPosition(10, 100)
    .setSize(128, 16)
    .setRange(0., 0.5);

  cp5.addSlider("szModSpeedY")
    .setPosition(10, 120)
    .setSize(128, 16)
    .setRange(0., 0.5);

  cp5.addSlider("szModSpeedZ")
    .setPosition(10, 140)
    .setSize(128, 16)
    .setRange(0., 0.5);

  cp5.addSlider("szModRangeX")
    .setPosition(10, 160)
    .setSize(128, 16)
    .setRange(0., 10.);

  cp5.addSlider("szModRangeY")
    .setPosition(10, 180)
    .setSize(128, 16)
    .setRange(0., 10.);

  cp5.addSlider("szModRangeZ")
    .setPosition(10, 200)
    .setSize(128, 16)
    .setRange(0., 10.);

  cp5.addSlider("numOfVertices")
    .setPosition(10, 220)
    .setSize(128, 16)
    .setRange(1, 80);

  cp5.addSlider("lineWidth")
    .setPosition(10, 240)
    .setSize(128, 16)
    .setRange(0.01, 5.);

  cp5.addSlider("lineWidthMovement")
    .setPosition(10, 260)
    .setSize(128, 16)
    .setRange(0, 0.5);

  cp5.addSlider("lerpSpeed")
    .setPosition(10, 280)
    .setSize(128, 16)
    .setRange(0., 0.5);

  cp5.addSlider("g")
    .setPosition(10, 300)
    .setSize(128, 16)
    .setRange(0, 50.);

  cp5.addSlider("rotX")
    .setPosition(10, 320)
    .setSize(128, 16)
    .setRange(0, 1.5);

  cp5.addSlider("rotY")
    .setPosition(10, 340)
    .setSize(128, 16)
    .setRange(0, 1.5);

  cp5.addSlider("rotZ")
    .setPosition(10, 360)
    .setSize(128, 16)
    .setRange(0, 1.5);


  cpFrom = cp5.addColorPicker("fromCol")
    .setPosition(width - 300, 50)
    .setColorValue(color(frR, frG, frB, frA))
    ;

  cpTo = cp5.addColorPicker("toCol")
    .setPosition(width - 300, 140)
    .setColorValue(color(toR, toG, toB, toA))
    ;
}

void drawGUI() {
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
void oscEvent(OscMessage theOscMessage) {
  if (theOscMessage.checkAddrPattern("/wek/outputs")==true) {
    if (theOscMessage.checkTypetag("ffffffffffffffffffffffffff")) { //Now looking for 2 parameters

      for (int i = 0; i < 26; i ++) {
        params[i] = theOscMessage.get(i).floatValue();
      }

      println("Received new params value from Wekinator");
    } else {
      println("Error: unexpected params type tag received by Processing");
    }


    szModSpeedX =  map(params[0], 0, 1, 0, 0.5);
    szModSpeedY = map(params[1], 0, 1, 0, 0.5);
    szModSpeedY = map(params[2], 0, 1, 0, 0.5);
    szModRangeX = map(params[3], 0, 1, 0, 10.);
    szModRangeY = map(params[4], 0, 1, 0, 10.);
    szModRangeZ = map(params[5], 0, 1, 0, 10.);
    numOfVertices = int(map(params[6], 0, 1, 0, 80));
    lineWidth = map(params[7], 0, 1, 0.01, 5.);
    lineWidthMovement = map(params[8], 0, 1, 0., 0.5);
    lerpSpeed = map(params[9], 0, 1, 0., 0.5);
    g = map(params[10], 0, 1, 0, 50);
    rotX = map(params[11], 0, 1, 0, 1.5);
    rotY = map(params[12], 0, 1, 0, 1.5);
    rotZ = map(params[13], 0, 1, 0, 1.5);
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


void initParams() {

  //initialise parameters to random values in their range


  szModSpeedX =  random(0, 0.5);
  szModSpeedY = random(0, 0.5);
  szModSpeedY = random(0, 0.5);
  szModRangeX = random(0, 10);
  szModRangeY = random(0, 10);
  szModRangeZ = random(0, 10);
  numOfVertices = int(random(1, 80));
  lineWidth = random(0.06, 5.);
  lineWidthMovement = random(0., 0.5);
  lerpSpeed = random( 0., 0.5);
  g = random(0, 3);
  rotX = random( 0, 1.5);
  rotY = random( 0, 1.5);
  rotZ = random(0, 1.5);

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
