int numOfShapes;
ArrayList <Shape> shapes;
Shape s1;
Shape s2;



class shapeSystem {

  color fromCol;
  color toCol;


  void setupSystem() {
    numOfShapes = 30;

    shapes = new ArrayList <Shape>();


    for (int i = 0; i < numOfShapes; i++) {
      Shape s = new Shape();
      shapes.add(s);
    }
  }

  void update() {



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

  void display() {

    pushStyle();

    for (int i = 0; i < shapes.size(); i++) {

      Shape s = shapes.get(i);

      //translate(random(500), random(500), random(500));
      color c = lerpColor(fromCol, toCol, s.shapeColourLerp);
      stroke(c);
      s.display();
    }
    popStyle();
  }
}
