package net.alexgraham.thesis.ui.components;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.time.Year;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import sun.net.www.content.audio.x_aiff;


public class Dial extends JComponent {
	int minValue, nvalue, maxValue, value, radius;

	public enum Movement {
		EXACT, VERTICAL
	}

	public interface DialListener extends java.util.EventListener {
		void dialAdjusted(DialEvent e);
	}

	public class DialEvent extends java.util.EventObject {
		int value;

		DialEvent(Dial source, int value) {
			super(source);
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private Movement movementType = Movement.VERTICAL;
	private int startX = 0;
	private int startY = 0;
	private int pixelsMoved = 0;
	private int lastValue = 0;

	public Dial() {
		this(0, 100, 0);
	}

	public Dial(int minValue, int maxValue, int value) {
		setMinimum(minValue);
		setMaximum(maxValue);
		setValue(value);
		setForeground(Color.lightGray);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				switch (movementType) {
					case EXACT:
						spin(e);
						break;
					case VERTICAL:
						press(e);
						break;
					default:
						spin(e);
						break;
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				switch (movementType) {
					case EXACT:
						break;
					case VERTICAL:
						getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						break;
					default:
						spin(e);
						break;
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				switch (movementType) {
					case EXACT:
						spin(e);
						break;
					case VERTICAL:
						try {
							difference(e);
						} catch (AWTException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					default:
						spin(e);
						break;
				}
			}
		});
		setBorder(BorderFactory.createLineBorder(Color.black));
	}

	protected void press(MouseEvent e) {
		System.out.println("Pressed");
		startX = e.getX();
		startY = e.getY();
		startX = e.getXOnScreen();
		startY = e.getYOnScreen();
		pixelsMoved = 0;
		lastValue = getValue();
		
		System.out.println("started At + "+  startX + " X " + startY + " Y ");
		
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		
		getRootPane().setCursor(blankCursor);
		
	}
	
	protected void difference(MouseEvent e) throws AWTException {

		float pixelRange = getPreferredSize().height / 0.25f;
		int range = maxValue - minValue;
		
		float mod = pixelRange / (float) range;
		
		int y = e.getY();
		int x = e.getX();
		
		y = e.getYOnScreen();
	
		int diff = startY - y;
		
		pixelsMoved += diff;
		
	    // Move the cursor
	    Robot robot = new Robot();
	    robot.mouseMove(startX, startY);
		
		int change = (int) (pixelsMoved / mod);
		
		setValue(lastValue + change);
		//setValue(getValue() + change);
	}

	protected void spin(MouseEvent e) {
		int y = e.getY();
		int x = e.getX();
		double th = Math.atan((1.0 * y - radius) / (x - radius));
		int value = (int) (th / (2 * Math.PI) * (maxValue - minValue));
		if (x < radius)
			setValue(value + (maxValue - minValue) / 2 + minValue);
		else if (y < radius)
			setValue(value + maxValue);
		else
			setValue(value + minValue);
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int tick = 0;
		radius = Math.min(getSize().width, getSize().height) / 2 - tick;

		g2.setPaint(getForeground().darker());
		// 0 line
		g2.drawLine(radius * 2 + tick / 2, radius, radius * 2 + tick, radius);

		double th = getnValue() * (1.7 * Math.PI) / (maxValue - minValue);
		double scale = (double) getnValue() / (maxValue - minValue);
		
		g2.setStroke(new BasicStroke(2));
		draw3DCircle(g2, 0, 0, radius, scale, true);


		
		th += (Math.PI / 2) * 1.3; // Start at the bottom
		
//		System.out.println("Theta is " + Math.toDegrees(th));
		//System.out.println("Theta is: " + Math.toDegrees(th - ((Math.PI / 2) * 1.3)));

		Point center = new Point(getSize().width / 2 - tick, getSize().height
				/ 2 - tick);

		Point end = new Point((int) (Math.cos(th) * (radius)),
				(int) (Math.sin(th) * (radius)));
		
		//g2.setColor(Color.BLUE);
		//drawCircle(g2, center.x, center.y, (int) (radius * 0.6));

		
		//System.out.println(end.toString());
		g2.setStroke(new BasicStroke(1));

		g2.setStroke(new BasicStroke(2));
		g2.setPaint(getForeground().darker());
		g2.drawLine(end.x + radius, end.y + radius, center.x, center.y);
	}

	private void draw3DCircle(Graphics g,
			int x,
			int y,
			int radius,
			double scale,
			boolean raised) {
		
		int start = 117;
		
		Color foreground = getForeground();
		Color light = foreground.brighter();
		Color dark = foreground.darker();
		g.setColor(foreground);
		g.fillOval(x, y, radius * 2, radius * 2);

		g.setColor(Color.RED);
		int arcAngle = (int) (scale * -(360 - (int) Math.floor(start / 2) + 4));
		g.fillArc(0, 0, radius * 2, radius * 2, 360 - start, arcAngle);
		
//		g.setColor(Color.BLUE);
//		g.fillOval(x, y,(int) (radius * 1.7), (int) (radius * 1.7));
//		
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, radius * 2, radius * 2);
		g.fillArc(0, 0, radius * 2, radius * 2, 360 - start, (int) Math.floor(start / 2) - 4);
		
//		g.setColor(raised ? light : dark);
//		g.drawArc(x, y, radius * 2, radius * 2, 45, 180);
//		
//		g.setColor(raised ? dark : light);
//		g.drawArc(x, y, radius * 2, radius * 2, 225, 180);
	}
	
	// Convenience method to draw from center with radius
	public void drawCircle(Graphics cg, int xCenter, int yCenter, int r) {
		cg.drawOval(xCenter-r, yCenter-r, 2*r, 2*r);
	
	}
//	private void drawRotaryCircle(Graphics g,
//			int x,
//			int y,
//			int radius,
//			Dimension center,
//			boolean raised) {
//		Color foreground = getForeground();
//		Color light = foreground.brighter();
//		Color dark = foreground.darker();
//		g.setColor(foreground);
//		g.fillOval(x, y, radius * 2, radius * 2);
//		
//		g.setColor(Color.WHITE);
//		g.fillArc(x, y, radius, radius, 45, 180);
////		g.setColor(raised ? light : dark);
////		g.drawArc(x, y, radius * 2, radius * 2, 45, 180);
////		
////		g.setColor(raised ? dark : light);
////		g.drawArc(x, y, radius * 2, radius * 2, 225, 180);
//	}

	public Dimension getPreferredSize() {
		return new Dimension(80, 80);
	}

	public void setValue(int value) {

		if (value == this.value) {
			return;
		}
		
		if (value > maxValue)
			value = maxValue;
		if (value < minValue)
			value = minValue;
		
		this.value = value;
		repaint();
		fireEvent();

	}

	public int getValue() {
		return value;
	}
	
	private int getnValue() {
		return value - minValue;
	}

	public void setMinimum(int minValue) {
		this.minValue = minValue;
	}

	public int getMinimum() {
		return minValue;
	}

	public void setMaximum(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getMaximum() {
		return maxValue;
	}

	public void addDialListener(DialListener listener) {
		listenerList.add(DialListener.class, listener);
	}

	public void removeDialListener(DialListener listener) {
		listenerList.remove(DialListener.class, listener);
	}

	void fireEvent() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2)
			if (listeners[i] == DialListener.class)
				((DialListener) listeners[i + 1]).dialAdjusted(new DialEvent(
						this, getValue()));
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

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
