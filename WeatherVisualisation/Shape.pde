
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
  float mass = random(0.1, 2);
  int verticesMax = 80;
  float randomOffset;


  public Shape() {
    int d = 600;
    size = new PVector(random(20, 100), random(20, 100), random(20, 100));//w, h, d
    position = new PVector(random(500), random(500), random(500));
    angle = new PVector(random(TWO_PI), random(TWO_PI), random(TWO_PI));//rx, ry, rz
    shapeColourLerp = random(1.);
    randomOffset = random(1000);
    velocity = new PVector(random(-1., 1.), random(-1., 1.), random(-1., 1.));
    acceleration = new PVector(0, 0, 0);
    rtStep = new PVector(0.1, 0.1, 0.1);

    vertices = new ArrayList <QuadVertex>();

    for (int i = 0; i < verticesMax; i ++) {
      PVector tempControl;
      PVector tempPos;
      tempControl = new PVector(random(150), random(150), random(150));
      tempPos = new PVector(random(150), random(150), random(150));
      float r = random(- 5., 5.);
      QuadVertex q = new QuadVertex(tempControl, tempPos, r);
      vertices.add(q);
    }
  }


  void setupShape(PVector initSize, PVector initPos, PVector initRot) {
    size = initSize.copy();
    position = initPos.copy();
    //position.x = random(800);
    angle = initRot.copy();
    //shapeColour = initCol;
  }


  void update() {



    rtStep.x = rotX;
    rtStep.y = rotY;
    rtStep.z = rotZ;

    velocity.add(acceleration);
    position.add(velocity);
    acceleration.mult(0);
    angle.add(rtStep);
    velocity.limit(5);


    boundaries();
    shapeColourLerp = map(sin(frameCount * lerpSpeed + randomOffset), -1, 1, 0., 1.);
    if (lineWidthMovement > 0) {
      lineWidth = map(noise(frameCount * lineWidthMovement + randomOffset), 0, 1, 0.01, 5.);
    }
  }

  void display() {

    szMod();

    pushMatrix();



    translate(position.x, position.y, position.z);
    rotateX(angle.x);
    rotateY(angle.y);
    rotateZ(angle.z);
    size.x += szModX;
    size.y += szModY;
    size.z += szModZ;



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
  void boundaries() {
    if (position.x > 1000 || position.x < -1000) {
      velocity.x *= (-1);
    }
    if (position.y > 1000 || position.y < -1000) {
      velocity.y *= (-1);
    }
    if (position.z > 1000 || position.z < -1000) {
      velocity.z *= (-1);
    }
  }

  void unpackVertices() {

    // float numOfVertices = map(mouseX, 0, width, 2, vertices.size());
    for (int i = 0; i < numOfVertices; i ++) {
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

  PVector attract(Shape s) {
    PVector force = PVector.sub(position, s.position);             // Calculate direction of force
    float distance = force.mag();                                 // Distance between objects
    distance = constrain(distance, 5.0, 25.0);                             // Limiting the distance to eliminate "extreme" results for very close or very far objects
    force.normalize();                                            // Normalize vector (distance doesn't matter here, we just want this vector for direction

    float strength = (g * mass * s.mass) / (distance * distance); // Calculate gravitional force magnitude
    force.mult(strength);                                         // Get force vector --> magnitude * direction
    return force;
  }

  void applyForce(PVector force) {
    PVector f = PVector.div(force, mass);
    acceleration.add(f);
  }
}


void szMod() {
  szModX = map(sin(frameCount * szModSpeedX), -1, 1, -szModRangeX, szModRangeX);
  szModY = map(sin(frameCount * szModSpeedY), -1, 1, -szModRangeY, szModRangeY);
  szModZ = map(sin(frameCount * szModSpeedZ), -1, 1, -szModRangeZ, szModRangeZ);
}
