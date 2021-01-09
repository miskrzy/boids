import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;

public class Boid {

  //weights
  //allign vel
  private final double W1 = 0.1;
  //allign rot
  private final double W2 = 0.01;
  //separate
  private final double W3 = 0.00001;
  //cohesion
  private final double W4 = 0.00003;
  //rotate to movement direction
  private final double W5 = 0.03;

  private final double V_MAX = 10;

  private double x1, x2, x3;
  private double y1, y2, y3;

  private double xc, yc;

  //base
  private double a;
  //height
  private double h;
  //angle
  private double ang;

  //position
  private double x;
  private double y;

  //velocity
  private double vx;
  private double vy;

  //vision
  private double d;
  private double alpha;

  public Boid(double a, double h, double ang, int x0, int y0, double v, double d, double alpha) {
    this.a = a;
    this.h = h;
    this.ang = ang;
    this.d = d;
    this.alpha = alpha;

    x = x0;
    y = y0;

    this.vx = v * Math.sin(Math.toRadians(ang));
    this.vy = -v * Math.cos(Math.toRadians(ang));

    xc = 0;
    yc = 0;

    double angRad = Math.toRadians(ang);

    x1 = xc + h / 2.0 * Math.sin(angRad);
    y1 = yc - h / 2.0 * Math.cos(angRad);

    x2 = xc - h / 2.0 * Math.sin(angRad) + a / 2.0 * Math.sin(Math.PI / 2.0 - angRad);
    y2 = yc + h / 2.0 * Math.cos(angRad) + a / 2.0 * Math.cos(Math.PI / 2.0 - angRad);

    x3 = xc - h / 2.0 * Math.sin(angRad) - a / 2.0 * Math.sin(Math.PI / 2.0 - angRad);
    y3 = yc + h / 2.0 * Math.cos(angRad) - a / 2.0 * Math.cos(Math.PI / 2.0 - angRad);
  }

  private int[] getXs() {
    int[] res = {(int) Math.round(x1), (int) Math.round(x2), (int) Math.round(x3)};
    return res;
  }

  private int[] getYs() {
    int[] res = {(int) Math.round(y1), (int) Math.round(y2), (int) Math.round(y3)};
    return res;
  }


  public void rotate(double beta) {
    ang += beta;
    double angRad = Math.toRadians(ang);

    x1 = xc + h / 2.0 * Math.sin(angRad);
    y1 = yc - h / 2.0 * Math.cos(angRad);

    x2 = xc - h / 2.0 * Math.sin(angRad) + a / 2.0 * Math.sin(Math.PI / 2.0 - angRad);
    y2 = yc + h / 2.0 * Math.cos(angRad) + a / 2.0 * Math.cos(Math.PI / 2.0 - angRad);

    x3 = xc - h / 2.0 * Math.sin(angRad) - a / 2.0 * Math.sin(Math.PI / 2.0 - angRad);
    y3 = yc + h / 2.0 * Math.cos(angRad) - a / 2.0 * Math.cos(Math.PI / 2.0 - angRad);
  }

  private void move(int endX, int endY) {

    x += vx;
    y += vy;

    if (x > endX) {
      x = 0;
    }
    if (x < 0) {
      x = endX;
    }
    if (y > endY) {
      y = 0;
    }
    if (y < 0) {
      y = endY;
    }
  }

  private boolean checkIfInVision(Boid boid, int endX, int endY) {

    //normal
    double xn = boid.getX();
    double yn = boid.getY();
    if (Math.sqrt((xn - x) * (xn - x) + (yn - y) * (yn - y)) < d) {
      double temp = Math.toDegrees(Math.atan((xn - x) / (y - yn)));
      if (y - yn > 0) {
        temp += 180;
      }

      if (Math.abs(90 - ang - temp) % 360 < alpha
          || 360 - Math.abs(90 - ang - temp) % 360 < alpha) {
        return true;
      }
    }

    //periodic boundary 1
    xn = boid.getX() - endX;
    yn = boid.getY();
    if (Math.sqrt((xn - x) * (xn - x) + (yn - y) * (yn - y)) < d) {
      double temp = Math.toDegrees(Math.atan((xn - x) / (y - yn)));
      if (y - yn > 0) {
        temp += 180;
      }

      if (Math.abs(90 - ang - temp) % 360 < alpha
          || 360 - Math.abs(90 - ang - temp) % 360 < alpha) {
        return true;
      }
    }

    //periodic boundary 2
    xn = boid.getX();
    yn = boid.getY() - endY;
    if (Math.sqrt((xn - x) * (xn - x) + (yn - y) * (yn - y)) < d) {
      double temp = Math.toDegrees(Math.atan((xn - x) / (y - yn)));
      if (y - yn > 0) {
        temp += 180;
      }

      if (Math.abs(90 - ang - temp) % 360 < alpha
          || 360 - Math.abs(90 - ang - temp) % 360 < alpha) {
        return true;
      }
    }

    //periodic boundary 3
    xn = boid.getX() - endX;
    yn = boid.getY() - endY;
    if (Math.sqrt((xn - x) * (xn - x) + (yn - y) * (yn - y)) < d) {
      double temp = Math.toDegrees(Math.atan((xn - x) / (y - yn)));
      if (y - yn > 0) {
        temp += 180;
      }

      if (Math.abs(90 - ang - temp) % 360 < alpha
          || 360 - Math.abs(90 - ang - temp) % 360 < alpha) {
        return true;
      }
    }

    return false;
  }

  public void update(List<Boid> others, List<Obstacle> obstacles, int endX, int endY) {
    List<Boid> neighbours = new LinkedList<>();
    for (Boid boid : others) {
      if (this.checkIfInVision(boid, endX, endY)) {
        neighbours.add(boid);
      }
    }
    allignVelocity(neighbours);
    allignRotation(neighbours);
    maxVel();
    separate(neighbours);
    maxVel();
    cohesion(neighbours);


    rotToMovDirect();
    maxVel();

    move(endX, endY);
    getOutOfObstacle(obstacles);


  }

  private void getOutOfObstacle(List<Obstacle> obstacles) {
    for (Obstacle obstacle : obstacles) {
      double xo = obstacle.getX();
      double yo = obstacle.getY();
      double r = obstacle.getR();
      double dist = Math.sqrt((xo - x) * (xo - x) + (yo - y) * (yo - y));

      if (dist < r) {
        x = xo - r / dist * (xo-x);
        y = yo - r / dist * (yo-y);
        vx*=0.99;
        vy*=0.99;
        //System.out.println("x");
      }
    }
  }

  private void cohesion(List<Boid> neighbours) {
    double posAvx = 0;
    double posAvy = 0;
    for (Boid neigh : neighbours) {
      posAvx += neigh.getX();
      posAvy += neigh.getY();
    }
    posAvx /= neighbours.size();
    posAvy /= neighbours.size();
    if (neighbours.size() != 0) {
      vx += W4 * (posAvx - vx);
      vy += W4 * (posAvy - vy);
    }
  }

  private void maxVel() {
    if (Math.sqrt(vx * vx + vy * vy) > V_MAX) {
      vx = V_MAX * Math.sin(Math.toRadians(ang));
      vy = -V_MAX * Math.cos(Math.toRadians(ang));
    }
  }

  private void rotToMovDirect() {
    double direct = vy < 0 ? Math.toDegrees(Math.atan(-vx / vy))
        : Math.toDegrees(Math.atan(-vx / vy) + Math.PI);
    rotate(W5 * (direct - ang));
  }

  private void allignVelocity(List<Boid> neighbours) {

    double velAvx = 0;
    double velAvy = 0;
    for (Boid neigh : neighbours) {
      velAvx += neigh.getVx();
      velAvy += neigh.getVy();
    }
    velAvx /= neighbours.size();
    velAvy /= neighbours.size();
    if (neighbours.size() != 0) {
      vx += W1 * (velAvx - vx);
      vy += W1 * (velAvy - vy);
    }
  }

  private void allignRotation(List<Boid> neighbours) {
    double rotAv = 0;
    for (Boid neigh : neighbours) {
      rotAv += neigh.getAng();
    }
    rotAv /= neighbours.size();
    if (neighbours.size() != 0) {
      rotate(W2 * (rotAv - ang));
    }

  }

  private void separate(List<Boid> neighbours) {

    for (Boid n : neighbours) {
      double xn = n.getX();
      double yn = n.getY();
      double coeff = Math.pow(d / Math.sqrt((xn - x) * (xn - x) + (yn - y) * (yn - y)), 4);

      //translate
      vx -= W3 * (xn - x) * coeff;
      vy -= W3 * (yn - y) * coeff;

    }

  }

  public Polygon getBoid() {
    Polygon polygon = new Polygon(getXs(), getYs(), 3);
    polygon.translate((int) Math.round(x), (int) Math.round(y));
    return polygon;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getVx() {
    return vx;
  }

  public double getVy() {
    return vy;
  }

  public double getAng() {
    return ang;
  }
}
