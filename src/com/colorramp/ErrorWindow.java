package com.colorramp;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

public class ErrorWindow extends JFrame implements WindowListener {
  private transient Thread worker;
  public ErrorWindow(String content) {
    JTextArea jta = new JTextArea(content);
    jta.setColumns(30);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Uh oh...");
    setPreferredSize(new Dimension(300, 200));
    setLocationByPlatform(true);
    setAlwaysOnTop(true);
    getContentPane().add(jta);
    setResizable(false);
    addWindowListener(this);
  }

  public void run() {
    pack();
    setVisible(true);
    worker = new Thread(() -> {
      while(true) {
        toFront();
        try {
          Thread.sleep(70);
        } catch (InterruptedException e) {
          // DO NOTHING
        }
      }
    });
    worker.start();
  }

  public void kill() {
    worker.interrupt();
    setVisible(false);
    dispose();
  }

  @Override
  public void windowActivated(WindowEvent arg0) {
    
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    kill();
  }

  @Override
  public void windowClosing(WindowEvent arg0) {
    kill();
  }

  @Override
  public void windowDeactivated(WindowEvent arg0) {
    
  }

  @Override
  public void windowDeiconified(WindowEvent arg0) {
    
  }

  @Override
  public void windowIconified(WindowEvent arg0) {
    
  }

  @Override
  public void windowOpened(WindowEvent arg0) {
    
  }
}
