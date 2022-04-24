package com.colorramp;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.JComponent;

import com.exoad.main.UIFactory;

public class Main {
  public static final LoadingWindow load = new LoadingWindow();
  
  private UIFactory ui;

  public Main(JComponent mainDef) {
    ui = new UIFactory(false, null, mainDef);
    ui.setTitle("~ Gradient BG Wrapper4J");
    try {
      ui.setIconImage(new ImageIcon("resource/unknown.png").getImage());
    } catch (Exception e) {
      // DO NOTHING
    }
    ui.setPreferredSize(new Dimension(Size.width, Size.height));
  }

  public void run() {
    ui.run();
  }

  public static synchronized void main(String... args) throws Exception {
    UIManager.setLookAndFeel(com.jtattoo.plaf.fast.FastLookAndFeel.class.getName());
    HostFrame hf = new HostFrame();
    JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, hf, new ControlPanel(hf));
    jsp.setPreferredSize(new Dimension(Size.width, Size.height));
    jsp.setDividerSize(10);
    jsp.setDividerLocation(Size.width - 230);
    Main m = new Main(jsp);
    m.run();
  }
}