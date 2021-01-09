import java.awt.geom.Ellipse2D;

public class Obstacle {
  //radius
  private double r;

  //position
  private double x;
  private double y;


  public Obstacle(double r, int x0, int y0) {
    this.r = r;

    x = x0;
    y = y0;
  }

  public Ellipse2D getObstacle() {
    Ellipse2D circle = new Ellipse2D.Double(x-r,y-r,2*r,2*r);
    return circle;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getR() {
    return r;
  }
}
