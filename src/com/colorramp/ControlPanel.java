package com.colorramp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.exoad.main.UIFactory;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ControlPanel extends JPanel implements ActionListener, ChangeListener {
  public static final int DEF_SIZE = Size.width - 200;
  private JEditorPane notice;
  private JComboBox<String> amountOfShades;
  private JLabel jl;
  private JButton confirmColors;
  private JScrollPane colorsPanel;
  private static final String X_LINE = "X pos", Y_LINE = "Y pos";
  private HostFrame hf;

  private static class DocumentLimit extends PlainDocument {
    private int limit;

    public DocumentLimit(int limit) {
      super();
      this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
      if (str == null) {
        return;
      }
      if ((getLength() + str.length()) <= limit) {
        super.insertString(offset, str, attr);
      }

    }
  }

  public static class ColorAccessPanel extends JPanel {
    private JLabel jl;
    private JTextField r, g, b;

    public ColorAccessPanel(int i) {
      jl = new JLabel("Color (r,g,b)" + i);
      r = new JTextField(2);
      r.setText("255");
      r.setToolTipText("Red");
      r.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      r.setDocument(new DocumentLimit(3));
      new ControlPanel.KeyLoggedListener(r);
      g = new JTextField(2);
      g.setText("255");
      g.setToolTipText("Green");
      g.setDocument(new DocumentLimit(3));
      g.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      new ControlPanel.KeyLoggedListener(g);
      b = new JTextField(2);
      b.setText("255");
      b.setToolTipText("Blue");
      b.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      b.setDocument(new DocumentLimit(3));
      new ControlPanel.KeyLoggedListener(b);
      setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
      add(jl);
      add(r);
      add(g);
      add(b);
      constCheck();
    }

    public transient Thread worker;

    private void constCheck() {
      worker = new Thread(() -> {
        while (true) {
          check(r);
          check(g);
          check(b);
          try {
            Thread.sleep(300);
          } catch (InterruptedException e) {
            // DO NOTHING
          }
        }
      });
      worker.start();
    }

    public Color getColor() {
      return new Color(Integer.parseInt(r.getText()), Integer.parseInt(g.getText()), Integer.parseInt(b.getText()));
    }

    public static void check(JTextField r) {
      try {
        String str = r.getText();
        int i = Integer.parseInt(str);
        if (i > 255) {
          r.setBackground(Color.RED);
          r.setForeground(Color.WHITE);
        } else {
          r.setBackground(Color.WHITE);
          r.setForeground(Color.BLACK);
        }
      } catch (NumberFormatException e) {
        // DO NOTHING
      }
    }

    public static boolean oustCheck(JTextField rxc) {
      if (rxc.getText().isBlank() || rxc.getText().isEmpty())
        return false;
      else {
        int i = Integer.parseInt(rxc.getText());
        return i >= 0 && i <= 255;
      }
    }
  }

  public static class KeyLoggedListener {
    public KeyLoggedListener(JTextField jtf) {
      jtf.addKeyListener(new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
          char c = e.getKeyChar();
          if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
            e.consume();
          }

        }

        @Override
        public void keyPressed(KeyEvent e) {
          char c = e.getKeyChar();
          if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
            e.consume();
          }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

      });
    }
  }

  public ControlPanel(HostFrame hostFrame) {
    setPreferredSize(new Dimension(DEF_SIZE, Size.height));
    setOpaque(true);
    setBackground(Color.WHITE);
    this.hf = hostFrame;
    notice = new JEditorPane();
    notice.setEditable(false);
    notice.setBackground(new Color(34, 169, 242));
    notice.setContentType("text/html");
    notice.setFont(notice.getFont().deriveFont(13f));
    notice.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    notice.setForeground(Color.WHITE);
    notice.setText(
        "<html><body style=\"text-align: center;\"><p><strong>Gradient Wallpaper Generator<br><u>Made by Exoad</u></strong></p></body></html>");

    jl = new JLabel("Amount of Shades (1-10)");

    amountOfShades = new JComboBox<>(new String[] { "2", "2" });
    amountOfShades.setSelectedIndex(0);
    amountOfShades.addActionListener(this);
    amountOfShades.setToolTipText("Set the number of shades to have in your gradient.");

    colorsPanel = new JScrollPane();
    colorsPanel.setPreferredSize(new Dimension(DEF_SIZE, Size.height - 400));
    colorsPanel.setBorder(BorderFactory.createEmptyBorder());
    colorsPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    colorsPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    confirmColors = new JButton("Confirm Colors");
    confirmColors.addActionListener(this);

    add(notice);
    add(jl);
    add(hostFrame.getColorSaveBTN());
    add(amountOfShades);
  }

  private ColorAccessPanel[] colorAccessPanels;
  private Color[] colorsGrad;
  private boolean generated = false;
  private JSlider[] colorDeviation;
  private int xs = 0, ys = 0, xs2 = Size.width - 230, ys2 = Size.height;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(amountOfShades) && !generated) {
      int amount = Integer.parseInt((String) amountOfShades.getSelectedItem());
      colorAccessPanels = new ColorAccessPanel[amount];
      for (int i = 0; i < amount; i++) {
        colorAccessPanels[i] = new ColorAccessPanel(i + 1);
        add(colorAccessPanels[i]);
        add(Box.createVerticalStrut(20));
      }
      colorDeviation = new JSlider[amount * 2];
      for (int i = 0; i < amount * 2; i++) {
        colorDeviation[i] = new JSlider(SwingConstants.HORIZONTAL, 0, i % 2 == 0 ? Size.width - 230 : Size.height, 0);
        colorDeviation[i].addChangeListener(this);
        colorDeviation[i].setToolTipText(i % 2 == 0 ? Y_LINE : X_LINE);
        add(colorDeviation[i]);
        if(i == i / 2) {
          add(Box.createVerticalStrut(20));
        }
      }
      add(confirmColors);
      repaint();
      revalidate();
      generated = true;
    } else if (e.getSource().equals(confirmColors)) {
      int i = 0;
      colorsGrad = new Color[colorAccessPanels.length];
      for (ColorAccessPanel cap : colorAccessPanels) {
        if (!ColorAccessPanel.oustCheck(cap.b) || !ColorAccessPanel.oustCheck(cap.r)
            || !ColorAccessPanel.oustCheck(cap.g)) {
          new ErrorWindow(
              "One or more of the entered RGB values are incorrect\nMost likely > 255!\nPlease correct this :(").run();
          break;
        } else {
          colorsGrad[i] = cap.getColor();
          System.out.println(cap.getColor());
          i++;
        }
      }
      hf.stabWorker();
      hf.poke(xs, xs2, ys, ys2, colorsGrad[0], colorsGrad[1]);
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if (e.getSource().equals(colorDeviation[0])) {
      xs = colorDeviation[0].getValue();
      colorDeviation[0].setToolTipText("X position: " + xs);
    } else if (e.getSource().equals(colorDeviation[1])) {
      ys = colorDeviation[1].getValue();
      colorDeviation[1].setToolTipText("Y position: " + ys);
    } else if (e.getSource().equals(colorDeviation[2])) {
      xs2 = colorDeviation[2].getValue();
      colorDeviation[2].setToolTipText("X position: " + xs2);
    } else if (e.getSource().equals(colorDeviation[3])) {
      ys2 = colorDeviation[3].getValue();
      colorDeviation[3].setToolTipText("Y position: " + ys2);
    }
  }
}
