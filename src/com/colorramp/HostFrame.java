package com.colorramp;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.exoad.main.UIFactory;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.RenderingHints;
import java.awt.Desktop;
import java.awt.ScrollPane;
import java.awt.GradientPaint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Random;

public class HostFrame extends JPanel implements ActionListener {
  private Random r = new Random();
  private JButton getCurrentLoad;
  private transient Thread worker = new Thread();
  private transient BufferedImage img;
  private int xs, ys, x2s, y2s;
  private Color rand1, rand2;
  private JPanel hostPanel;
  private boolean stabbed = false;

  public HostFrame() {
    setPreferredSize(new Dimension(Size.width - 230, Size.height));
    setMinimumSize(new Dimension(Size.width - 230, Size.height));
    setDoubleBuffered(true);
    hostPanel = new JPanel() {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (img != null) {
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g2d.drawImage(img, 0, 0, null);
        }
      }
    };
    hostPanel.setPreferredSize(getPreferredSize());

    getCurrentLoad = new JButton("Save Current BG");
    getCurrentLoad.addActionListener(this);
    worker = new Thread(() -> {
      while (true) {
        genNew();
        hostPanel.repaint();
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          // DO NOTHING
        }
      }
    });
    worker.start();
    add(hostPanel);
  }

  public void stabWorker() {
    worker.interrupt();
    worker = null;
    stabbed = true;
  }

  public void poke(int xs, int x2s, int ys, int y2s, Color rand1, Color rand2) {
    this.xs = xs;
    this.ys = ys;
    this.x2s = x2s;
    this.y2s = y2s;
    this.rand1 = rand1;
    this.rand2 = rand2;
    genNew();
    repaint();
  }

  public JButton getColorSaveBTN() {
    return getCurrentLoad;
  }

  public void genNew() {
    img = new BufferedImage(Size.width - 230, Size.height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    GradientPaint gp = null;
    if (!stabbed) {
      Color rand_rst = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
      Color rand2_rst = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
      int x = r.nextInt(Size.width - 230);
      int y = r.nextInt(Size.height);
      int x2 = r.nextInt(Size.width - 230);
      int y2 = r.nextInt(Size.height);
      gp = new GradientPaint(x, y, rand_rst, x2, y2, rand2_rst, true);
      this.xs = x;
      this.ys = y;
      this.x2s = x2;
      this.y2s = y2;
      this.rand1 = rand_rst;
      this.rand2 = rand2_rst;
    } else {
      gp = new GradientPaint(xs, ys, rand1, x2s, y2s, rand2, true);
    }
    g.setPaint(gp);
    g.fillRect(0, 0, Size.width - 230, Size.height);
    g.dispose();
  }

  public static BufferedImage upscale(int x, int y, int x2, int y2, Color c1, Color c2, boolean cycle, int width,
      int height) {
    BufferedImage img32 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img32.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    GradientPaint gp = new GradientPaint(x, y, c1, x2, y2, c2, cycle);
    g.setPaint(gp);
    g.fillRect(0, 0, width, height);
    g.dispose();
    return img32;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(getCurrentLoad)) {
      long time = System.currentTimeMillis();

      try {
        File parentDir = new File("gw4_exoad");
        if (!parentDir.exists()) {
          parentDir.mkdirs();
        }
        String currTimeStr = Utility.millisTimeToString(time);
        File file = new File("gw4_exoad/gen_" + currTimeStr);
        if (!file.isDirectory() || !file.exists()) {
          file.mkdirs();
        }

        File fdfs_480x = new File("gw4_exoad/gen_" + currTimeStr + "/gw4_" + time + "_480x.png");
        if (!fdfs_480x.exists()) {
          fdfs_480x.createNewFile();
        }
        ImageIO.write(upscale(xs, ys, x2s, y2s, rand1, rand2, true, Size.RES480.first, Size.RES480.second), "png",
            fdfs_480x);
        System.out.println("Rendered 480");
        File fdfs_720x = new File("gw4_exoad/gen_" + currTimeStr + "/gw4_" + time + "_720x.png");
        if (!fdfs_720x.exists()) {
          fdfs_720x.createNewFile();
        }
        ImageIO.write(upscale(xs, ys, x2s, y2s, rand1, rand2, true, Size.RES720.first, Size.RES720.second), "png",
            fdfs_720x);
        System.out.println("Rendered 720");
        File fdfs_1080x = new File("gw4_exoad/gen_" + currTimeStr + "/gw4_" + time + "_1080x.png");
        if (!fdfs_1080x.exists()) {
          fdfs_1080x.createNewFile();
        }
        ImageIO.write(upscale(xs, ys, x2s, y2s, rand1, rand2, true, Size.RES1080.first, Size.RES1080.second), "png",
            fdfs_1080x);
        System.out.println("Rendered 1080");
        File fdfs_1440x = new File("gw4_exoad/gen_" + currTimeStr + "/gw4_" + time + "_1440x.png");
        if (!fdfs_1440x.exists()) {
          fdfs_1440x.createNewFile();
        }
        ImageIO.write(upscale(xs, ys, x2s, y2s, rand1, rand2, true, Size.RES1440.first, Size.RES1440.second), "png",
            fdfs_1440x);
        System.out.println("Rendered 1440");
        File fdfs_4kx = new File("gw4_exoad/gen_" + currTimeStr + "/gw4_" + time + "_4kx.png");
        if (!fdfs_4kx.exists()) {
          fdfs_4kx.createNewFile();
        }
        ImageIO.write(upscale(xs, ys, x2s, y2s, rand1, rand2, true, Size.RES4K.first, Size.RES4K.second), "png",
            fdfs_4kx);
        System.out.println("Rendered 4k");

        Desktop.getDesktop().open(file);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      System.out.println("OK! Took me: " + (System.currentTimeMillis() - time) + "ms");

    }
  }

}
