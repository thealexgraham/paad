package net.alexgraham.thesis.ui.old;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;


public class DialNoModel extends JComponent {
	int minValue, nvalue, maxValue, value, radius;

	public enum Movement {
		EXACT, VERTICAL
	}
	
	public enum Behavior {
		CENTER, NORMAL
	}

	public interface DialListener extends java.util.EventListener {
		void dialAdjusted(DialEvent e);
	}

	public class DialEvent extends java.util.EventObject {
		int value;

		DialEvent(DialNoModel source, int value) {
			super(source);
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private Movement movementType = Movement.VERTICAL;
	private Behavior behaviorType = Behavior.NORMAL;
	private int startX = 0;
	private int startY = 0;
	private int pixelsMoved = 0;
	private int lastValue = 0;
	private String name = "";

	public DialNoModel() {
		this(-10, 10, 5);
	}

	public DialNoModel(int minValue, int maxValue, int value) {
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
						setBorder(BorderFactory.createEmptyBorder());
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
	
	public void setBehavior(Behavior behavior) {
		this.behaviorType = behavior;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	protected void press(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
		startX = e.getXOnScreen();
		startY = e.getYOnScreen();
		pixelsMoved = 0;
		lastValue = getValue();
				
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		getRootPane().setCursor(blankCursor);
		
		setBorder(BorderFactory.createLineBorder(Color.black));		
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
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		
		FontMetrics fontInfo = g2.getFontMetrics();
		int stringWidth =  fontInfo.stringWidth(name);
		int stringHeight = fontInfo.getHeight();
		
		
		int tick = 0;
		int offset = (int) (stringHeight / 2);
		radius = Math.min(getSize().width, getSize().height) / 2 - tick;
		radius -= offset;

		
		int range = maxValue - minValue;

		double th = getnValue() * (1.7 * Math.PI) / range;
		double scale = (double) getnValue() / range;
		th += (Math.PI / 2) * 1.3; // Start at the bottom
						
		g2.setStroke(new BasicStroke(2));

		Point center = new Point(getSize().width / 2 - tick, 
				getSize().height / 2 - tick + offset);

		Point end = new Point(center.x + (int) (Math.cos(th) * (radius)),
				center.y + (int) (Math.sin(th) * (radius)));
		
		drawDial(g2, 0, 0, radius, center, scale, true);
		
		
		g2.setStroke(new BasicStroke(2));
		//g2.setPaint(getForeground().darker());
		g2.setPaint(Color.black);
		g2.drawLine(end.x, end.y, center.x, center.y);
		
		g.drawString(name, (getSize().width / 2) - (stringWidth / 2), 0 + (int)(stringHeight / 1.2));
		
		String stringValue = getValueString();
		stringWidth =  fontInfo.stringWidth(stringValue);
		g.drawString(stringValue, (getSize().width / 2) - (stringWidth / 2), getHeight()- (int)(stringHeight / 4));
	}
	
	protected String getValueString() {
		return String.valueOf(getValue());
	}

	private void drawDial(Graphics g,
			int x,
			int y,
			int radius,
			Point center,
			double scale,
			boolean raised) {
		
		int start = 117;
		
		Color foreground = getForeground();
		
		// Draw Bottom Circle
		g.setColor(Color.BLACK);
		fillCircle(g, center.x, center.y, (int)(radius));
		

		// Draw Filled in area 
		g.setColor(Color.RED);
		
		if (behaviorType == Behavior.CENTER) {
			
			int range = maxValue - minValue;
			int middle = maxValue - (range / 2);
			scale = (double) getValue() / (maxValue - middle);
			
			int arcAngle = (int) (scale / 2 * -(360 - (int) Math.floor(start / 2) + 4));
			g.fillArc(center.x - radius, center.y - radius, radius * 2, radius * 2, 90, arcAngle);	
			
		} else if (behaviorType == Behavior.NORMAL) {
			int arcAngle = (int) (scale * -(360 - (int) Math.floor(start / 2) + 4));
			g.fillArc(center.x - radius, center.y - radius, radius * 2, radius * 2, 360 - start, arcAngle);		
		}
		
		// Draw outside line
		((Graphics2D) g).setStroke(new BasicStroke(1));
		g.setColor(Color.BLACK);
		//drawCircle(g, center.x, center.y, (int) (radius * 1.01));

		// Draw Center Cover
		g.setColor(getBackground());
		fillCircle(g, center.x, center.y, (int) (radius * 0.7));
		
		// Draw inside line
//		g.setColor(Color.BLACK);
//		drawCircle(g, center.x, center.y, (int) (radius * 0.7));
		
		// Draw empty area at bottom
		g.setColor(getBackground());
		g.fillArc(center.x - radius, center.y - radius, radius * 2, radius * 2, 360 - start, (int) Math.floor(start / 2) - 4);
		
		//g.fillArc(center.x - radius, center.y - radius + 2, radius * 2, radius * 2, 360 - start, (int) Math.floor(start / 2) - 4);

		g.drawArc(center.x - radius, center.y - radius, radius * 2, radius * 2, 360 - start, (int) Math.floor(start / 2) - 4);
		
	}
	
	// Convenience method to draw from center with radius
	public void drawCircle(Graphics cg, int xCenter, int yCenter, int r) {
		cg.drawOval(xCenter-r, yCenter-r, 2*r, 2*r);
	
	}
	
	// Convenience method to draw from center with radius
	public void fillCircle(Graphics cg, int xCenter, int yCenter, int r) {
		cg.fillOval(xCenter-r, yCenter-r, 2*r, 2*r);
	
	}

	public Dimension getPreferredSize() {
		return new Dimension(45, 45);
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
		final DialNoModel dial = new DialNoModel();
		dial.setName("Amp");
		dial.setBehavior(DialNoModel.Behavior.CENTER);
		frame.setLayout(new FlowLayout());
		frame.getContentPane().add(dial);
		frame.getContentPane().add(new DialNoModel());
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
