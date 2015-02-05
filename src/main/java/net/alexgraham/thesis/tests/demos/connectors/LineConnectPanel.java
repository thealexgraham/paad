package net.alexgraham.thesis.tests.demos.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.alexgraham.thesis.tests.demos.connectors.Connector.Location;

public class LineConnectPanel extends JPanel {
	
	boolean pointHovering = false;
	boolean dragging = false;
	ConnectablePanel currentPanel;
	
	ConnectablePanel originPanel;
	ConnectablePanel destinationPanel;
	
	Connection originConnection;
	Connection destinationConnection;
	
	Point connectOrigin;
	Point connectDestination;
	Rectangle currentConnector;

	ArrayList<ConnectablePanel> boxes = new ArrayList<ConnectablePanel>();
	ArrayList<Connection> connections = new ArrayList<Connection>();
	
	Connection clicked;

	public LineConnectPanel() {
		
		setOpaque(true);
		setBackground(Color.WHITE);
		setLayout(null);

		setBorder(BorderFactory.createDashedBorder(Color.black));
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Key typed");
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println("key pressed");

				if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					System.out.println("Delete key pressed");
					if (clicked != null) {
						connections.remove(clicked);
						clicked = null;
						repaint();
					}
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (pointHovering && !dragging) {
					originPanel = currentPanel;
					connectOrigin = originPanel.getConnectionLocation(); // new
																		// Point(e.getX(),
																		// e.getY());
					connectDestination = new Point(e.getPoint());
					dragging = true;
				}
				
				if (!pointHovering) {
					clicked = getClickedConnection(e.getPoint(), 10);
					if (clicked != null) {
						clicked.setClicked(true);
					} else {
						for (Connection connection : connections) {
							connection.setClicked(false);
						}
					}
					repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseReleased(e);
				if (dragging) {
					dragging = false;

					
					if (destinationPanel != null) {
						System.out.println("Destination box exists");
						// We have a destination and an origin
						Connection newConnection = new Connection(originPanel.getConnector(), destinationPanel.getConnector());
						connections.add(newConnection);
					}
					
					originPanel = null;
					destinationPanel = null;
					
					for (ConnectablePanel connectableBox : boxes) {
						connectableBox.checkPointHover(e);
					}
					
					repaint();
				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				connectDestination = new Point(e.getPoint());
				repaint();
				checkPoints(e);
				
			}

			public void mouseMoved(MouseEvent e) {
				checkPoints(e);
			};

		});
		
		TestMoveable moveable = new TestMoveable(300, 300);
		moveable.setLocation(5, 5);
		moveable.setSize(300, 300);
		add(moveable);
		boxes.addAll(moveable.getConnectablePanels());
		
//		ConnectableBox otherBox = new ConnectableBox(Location.LEFT);
//		add(otherBox);
//		otherBox.setX(290);
//		otherBox.setY(200);
//		boxes.add(otherBox);
//
//		ConnectableBox box = new ConnectableBox(Location.RIGHT);
//		add(box);
//		box.setX(0);
//		box.setY(0);
//		boxes.add(box);
//		
//		box = new ConnectableBox(Location.RIGHT);
//		add(box);
//		box.setX(50);
//		box.setY(50);
//		boxes.add(box);

	}
	
	public void checkPoints(MouseEvent e) {
		for (ConnectablePanel box : boxes) {
			
			if (box == originPanel)
				continue;
			
			boolean currentHover = box.checkPointHover(e);

			if (currentHover) {
				currentPanel = box;
				
				if (dragging) {
					destinationPanel = box;
				}
			}

			if (box == currentPanel && (currentHover != pointHovering)) {
				pointHovering = currentHover;
				
				repaint();
			}

			repaint();
		}
	}
	
	public Connection getClickedConnection(Point point, int distance) {
		
		for (Connection connection : connections) {
			Line2D line = connection.getLine();
			if (line.ptLineDist(point) < distance) {
				return connection;
			}
		}
		
		return null;
	}
//	
//	private static final int HIT_BOX_SIZE = 2;
//	public Line2D getClickedLine(Point point) {
//		int boxX = x - HIT_BOX_SIZE / 2;
//		int boxY = y - HIT_BOX_SIZE / 2;
//
//		int width = HIT_BOX_SIZE;
//		int height = HIT_BOX_SIZE;
//		
//		for (Connection connection : connections) {
//			Line2D line = connection.getLine();
//			if (line.ptLineDist(new Point))
//			if (line.intersects(boxX, boxY, width, height)) {
//				return line;
//			}		
//		}
//		
//		return null;
//	}



	public Dimension getPreferredSize() {
		return new Dimension(250, 200);
	}

	public Dimension getMaximumSize() {
		return new Dimension(250, 200);
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g);
		g2.setStroke(new BasicStroke(0));
		g.drawString("This is my custom Panel!", 10, 20);
			
		
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paint(g);
		for (ConnectablePanel box : boxes) {
			box.paintConnectors(g);
		}
		
		
		if (dragging) {
			g.drawLine(connectOrigin.x,
					connectOrigin.y,
					connectDestination.x,
					connectDestination.y);

		}
		
		for (Connection connection : connections) {
			//Line2D line = new Line2D.Float(connection.getOrigin().getCurrentCenter(), connection.getDestination().getCurrentCenter());
					//new Line2D(connection.getOrigin().getCurrentCenter(), connection.getDestination().getCurrentCenter());
			Line2D line = connection.getLine();
			if (connection.isClicked())
				g2.setStroke(new BasicStroke(3));
			else
				g2.setStroke(new BasicStroke(1));
			g2.draw(line);
			g2.setStroke(new BasicStroke(1));
		}
	}
	
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		System.out.println("LineConnect Created GUI on EDT? "
				+ SwingUtilities.isEventDispatchThread());
		JFrame f = new JFrame("Swing Paint Demo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		LineConnectPanel panel = new LineConnectPanel();
//		panel.setOpaque(true);
//		panel.setBackground(Color.WHITE);
//		panel.setLayout(null);
		
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		f.add(panel);
		f.setSize(500, 500);
		f.setVisible(true);
	}
}

