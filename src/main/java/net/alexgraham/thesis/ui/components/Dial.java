package net.alexgraham.thesis.ui.components;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.*;

public class Dial extends JComponent {
  int minValue, nvalue, maxValue, radius;
  
  public interface DialListener extends java.util.EventListener {
		void dialAdjusted( DialEvent e );
	}
  
  public class DialEvent extends java.util.EventObject {
		int value;

		DialEvent( Dial source, int value ) {
			super( source );
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}

  public Dial() { this(50, 100, 0); }

  public Dial(int minValue, int maxValue, int value) {
    setMinimum( minValue );
    setMaximum( maxValue );
    setValue( value );
    setForeground( Color.lightGray );

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) { spin(e); }
    });
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) { spin(e); }
    });
  }

  protected void spin( MouseEvent e ) {
    int y = e.getY();
    int x = e.getX();
    double th = Math.atan((1.0 * y - radius) / (x - radius));
    int value=(int)(th / (2 * Math.PI) * (maxValue - minValue));
    if (x < radius)
      setValue( value + (maxValue-minValue) / 2 + minValue);
    else if (y < radius)
      setValue( value + maxValue );
    else
      setValue( value + minValue);
  }
  
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;
    int tick = 10;
    radius = Math.min( getSize().width, getSize().height) / 2 - tick;

    g2.setPaint( getForeground().darker() );
    
    // 0 line
    g2.drawLine( radius * 2 + tick / 2, radius, 
       radius * 2 + tick, radius);
    
    g2.setStroke( new BasicStroke(2) );
    draw3DCircle( g2, 0, 0, radius, true );
    

    double th = nvalue * (2 * Math.PI) / (maxValue - minValue);
    
    Point center = new Point(getSize().width / 2 - tick, 
    		getSize().height / 2 - tick);
    
    Point end = new Point((int)(Math.cos(th) * (radius)), (int)(Math.sin(th) * (radius)));
    g2.setStroke(new BasicStroke(1));

    g2.setStroke(new BasicStroke(2));
    g2.setPaint(getForeground().darker());
    g2.drawLine(end.x + radius, end.y + radius, center.x, center.y);
  }

  private void draw3DCircle( 
      Graphics g, int x, int y, int radius, boolean raised) 
  {
    Color foreground = getForeground();
    Color light = foreground.brighter();
    Color dark = foreground.darker();
    g.setColor(foreground);
    g.fillOval(x, y, radius * 2, radius * 2);
    g.setColor(raised ? light : dark);
    g.drawArc(x, y, radius * 2, radius * 2, 45, 180);
    g.setColor(raised ? dark : light);
    g.drawArc(x, y, radius * 2, radius * 2, 225, 180);
  }

  public Dimension getPreferredSize() {
    return new Dimension(100, 100);
  }

  public void setValue( int value ) {
    this.nvalue = value - minValue;
    repaint();
    fireEvent();
  }
  public int getValue()  { return nvalue+minValue; }
  public void setMinimum(int minValue)  { this.minValue = minValue; }
  public int getMinimum()  { return minValue; }
  public void setMaximum(int maxValue)  { this.maxValue = maxValue; }
  public int getMaximum()  { return maxValue; }

  public void addDialListener(DialListener listener) {
    listenerList.add( DialListener.class, listener );
  }
  public void removeDialListener(DialListener listener) {
    listenerList.remove( DialListener.class, listener );
  }

  void fireEvent() {
    Object[] listeners = listenerList.getListenerList();
    for ( int i = 0; i < listeners.length; i += 2 )
      if ( listeners[i] == DialListener.class )
        ((DialListener)listeners[i + 1]).dialAdjusted(
          new DialEvent(this, getValue()) );
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Dial v1.0");
    final JLabel statusLabel = new JLabel("Welcome to Dial v1.0");
    final Dial dial = new Dial();
    frame.setLayout(new FlowLayout());
    frame.getContentPane().add(dial);
    frame.getContentPane().add(new Dial());
    frame.getContentPane().add(statusLabel);

    dial.addDialListener(new DialListener() {
      public void dialAdjusted(DialEvent e) {
        statusLabel.setText("Value is " + e.getValue());
      }
    });

    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setSize( 1000, 1000 );
    frame.setVisible( true );
  }
}
