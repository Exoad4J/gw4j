package com.colorramp;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.BorderLayout;
import java.awt.geom.AffineTransform;

import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class LoadingWindow extends JPanel {
  private double lastRotation = 0.0111111, dualAxisOther = -0.03, decFirst, decSecond;
  private final JFrame jf;
  private Color fg, bg;
  private transient Thread worker;

  public LoadingWindow(Color foreground, Color background, String text, long refreshRate, double firstRotSpeed,
      double secondRotSpeed, double decrementSpeedFirst, double decrementSpeedSecond, boolean isCenter) {
    jf = new JFrame();
    jf.setTitle(text);
    jf.setUndecorated(true);
    jf.setPreferredSize(new Dimension(500, 300));
    jf.setResizable(false);
    jf.setLocationByPlatform(true);
    jf.setLayout(new BorderLayout());
    if(isCenter) {
      Dimension screenSpace = new Dimension(Size.width, Size.height);
      jf.setLocation(screenSpace.width / 2 - 300, screenSpace.height / 2);
    }
    JLabel jl = new JLabel(text);
    jl.setForeground(foreground == null ? new Color(56, 56, 56) : foreground);
    jl.setAlignmentX(Component.RIGHT_ALIGNMENT);
    jl.setAlignmentY(Component.CENTER_ALIGNMENT);
    jl.setFont(jl.getFont().deriveFont(30f));
    jf.getContentPane().add(this);
    jf.add(jl, BorderLayout.EAST);
    new Thread(() -> {
      while (true) {
        repaint();
        try {
          Thread.sleep(refreshRate);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
    this.fg = foreground == null ? new Color(56, 56, 56) : foreground;
    this.bg = background == null ? new Color(247, 170, 69) : background;
    this.lastRotation = firstRotSpeed;
    this.dualAxisOther = secondRotSpeed;
    this.decFirst = decrementSpeedFirst;
    this.decSecond = decrementSpeedSecond;
  }

  public LoadingWindow() {
    jf = new JFrame();
    jf.setTitle("Loading...");
    jf.setPreferredSize(new Dimension(500, 300));
    jf.setResizable(false);
    jf.setLocationByPlatform(true);
    jf.setUndecorated(true);
    jf.setAlwaysOnTop(true);
    Dimension screenSpace = new Dimension(Size.width, Size.height);
    jf.setLocation(screenSpace.width / 2 - 300, screenSpace.height / 2 - 100);
    jf.setLayout(new BorderLayout());
    JLabel jl = new JLabel("Loading...     ");
    jl.setForeground(new Color(56, 56, 56));
    jl.setAlignmentX(Component.RIGHT_ALIGNMENT);
    jl.setAlignmentY(Component.CENTER_ALIGNMENT);
    jl.setFont(jl.getFont().deriveFont(30f));
    jf.getContentPane().add(this);
    jf.add(jl, BorderLayout.EAST);
    new Thread(() -> {
      while (true) {
        repaint();
        try {
          Thread.sleep(30);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
    decFirst = Math.PI;
    decSecond = Math.PI;
    this.fg = new Color(56, 56, 56);
    this.bg = new Color(247, 170, 69);
  }

  @Override
  public synchronized void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    AffineTransform at = new AffineTransform();
    AffineTransform at2 = new AffineTransform();
    Rectangle shape = new Rectangle(-110, 40, 220, 220);
    Rectangle shapeWireframe = new Rectangle(-110, 40, 230, 230);
    at.rotate(lastRotation, shape.getX() + shape.getWidth() / 2, shape.getY() + shape.getHeight() / 2);
    at2.rotate(dualAxisOther, shapeWireframe.getX() + shapeWireframe.getWidth() / 2,
        shapeWireframe.getY() + shapeWireframe.getHeight() / 2);
    dualAxisOther -= decFirst / 100;
    lastRotation += decSecond / 150;
    g2.setColor(bg);
    Shape s = at.createTransformedShape(shape);
    g2.fill(s);
    g2.setColor(fg);
    Shape s2Shape = at2.createTransformedShape(shapeWireframe);
    g2.setStroke(new BasicStroke(3));
    g2.draw(s2Shape);
  }

  public synchronized void start(Runnable r) {
    jf.pack();
    jf.setVisible(true);
    worker = new Thread(r);
    try {
      worker.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public synchronized void start() {
    jf.pack();
    jf.setVisible(true);
  }

  public synchronized void kill() {
    jf.dispose();
    if (worker != null)
      worker.interrupt();
  }
}
