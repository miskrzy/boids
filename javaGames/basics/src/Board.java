

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Board extends JPanel implements Runnable {

  private final int BOID_NUMBER = 20;
  private final int OBSTACLE_NUMBER = 4;

  private final int B_WIDTH = 1000;
  private final int B_HEIGHT = 1000;
  private final int DELAY = 25;

  private Thread animator;

  private List<Boid> boids;
  private List<Obstacle> obstacles;

  private Random random;

  private SaveToFile save;

  public Board() {
    random = new Random();
    boids = new LinkedList<Boid>();
    obstacles = new LinkedList<Obstacle>();

    save = new SaveToFile();
    initBoard();

  }

  private void initBoard() {
    setBackground(Color.WHITE);
    setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

    for (int i = 0; i < BOID_NUMBER; i++) {
      Boid boidToAdd = new Boid(B_WIDTH / 40.0, B_HEIGHT / 40.0 * 1.5, random.nextDouble() * 360,
          (int) (random.nextDouble() * (double) B_WIDTH),
          (int) (random.nextDouble() * (double) B_HEIGHT), random.nextInt(2) + 1, B_WIDTH / 10.0,
          120);
      boids.add(boidToAdd);
    }

    for (int i = 0; i < OBSTACLE_NUMBER; i++) {
      Obstacle obstacleToAdd = new Obstacle(B_WIDTH / 15.0 * (1 + random.nextDouble()),
          (int) (random.nextDouble() * (double) B_WIDTH),
          (int) (random.nextDouble() * (double) B_HEIGHT));
      obstacles.add(obstacleToAdd);
    }

    //boids.add(new Boid(B_WIDTH / 20.0, B_HEIGHT / 20.0 * 1.5,180,160,170,0,0,0));
    //boids.add(new Boid(B_WIDTH / 20.0, B_HEIGHT / 20.0 * 1.5,0,100,100,0,100,30));
  }

  @Override
  public void addNotify() {
    super.addNotify();

    animator = new Thread(this);
    animator.start();
  }


  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    drawBoids(g);
    drawObstacles(g);
  }

  private void drawBoids(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    RenderingHints rh
        = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    rh.put(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHints(rh);

    g2d.setStroke(new BasicStroke(2));
    g2d.setColor(Color.black);

    for (Boid boid : boids) {
      g2d.draw(boid.getBoid());
    }
  }

  private void drawObstacles(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    RenderingHints rh
        = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    rh.put(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHints(rh);

    g2d.setStroke(new BasicStroke(2));
    g2d.setColor(Color.red);

    for (Obstacle obstacle : obstacles) {
      g2d.draw(obstacle.getObstacle());
    }
  }

  private void cycle() {

    //double avVel = 0;
    for (Boid boid : boids) {

      List<Boid> copyBoids = new LinkedList<>(boids);
      copyBoids.remove(boid);

      boid.update(copyBoids, obstacles, B_WIDTH, B_HEIGHT);

      //avVel += Math.sqrt(Math.pow(boid.getVx(),2) + Math.pow(boid.getVy(),2));
    }

    //avVel/=boids.size();


    //save.save(avVel);

  }


  @Override
  public void run() {

    long beforeTime, timeDiff, sleep;

    beforeTime = System.currentTimeMillis();

    while (true) {

      cycle();
      repaint();

      timeDiff = System.currentTimeMillis() - beforeTime;
      sleep = DELAY - timeDiff;

      if (sleep < 0) {
        sleep = 2;
      }

      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
        String msg = String.format("Thread interrupted: %s", e.getMessage());

        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
      }

      beforeTime = System.currentTimeMillis();
    }

  }
}
