

class QuadVertex {

  float cx, cy, cz, x, y, z, r;

  public QuadVertex(PVector control, PVector pos, float offset) {
    cx = control.x;
    cy = control.y;
    cz = control.z;
    x = pos.x;
    y = pos.y;
    z = pos.z;
    r = offset;
  }
}
