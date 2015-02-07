package net.alexgraham.thesis.ui.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.Instrument;
import net.alexgraham.thesis.supercollider.RoutinePlayer;
import net.alexgraham.thesis.supercollider.Synth;
import net.alexgraham.thesis.supercollider.SynthModel.SynthModelListener;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.modules.InstrumentModule;
import net.alexgraham.thesis.ui.modules.RoutinePlayerModule;

public class LineConnectPanel extends JPanel implements SynthModelListener {
	
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
	
	boolean requireMouse = false;
	
	public boolean isRequiringMouse() { return requireMouse || pointHovering || dragging; }

	public LineConnectPanel() {
		
		setOpaque(true);
		setBackground(Color.WHITE);
		setLayout(null);
		
		// Set able to be focusable for key clicks
		setFocusable(true);

		// Create mouse and key listeners
		this.createKeyListeners();
		this.createMouseListeners();
		
		setBorder(BorderFactory.createDashedBorder(Color.black));
		
		// Listen for synths being added
		App.synthModel.addListener(this);
		
		// Create a test Routine Player
		RoutinePlayer player = new RoutinePlayer();
		RoutinePlayerModule playerPanel = new RoutinePlayerModule(player);
		playerPanel.setLocation(10, 10);
		
		boxes.addAll(playerPanel.getConnectablePanels());
		add(playerPanel);

	}
	
	public void createKeyListeners() {
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
	}
	
	public void createMouseListeners() {
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
						
						LineConnectPanel.this.requestFocusInWindow();
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
						Connectable originConnectable = originPanel.getConnector().getConnectable();
						Connectable destinationConnectable = destinationPanel.getConnector().getConnectable();
						
						// We have a destination and an origin
						if (originConnectable.connectWith(destinationConnectable) && 
								destinationConnectable.connectWith(originConnectable)) {
							Connection newConnection = new Connection(originPanel.getConnector(), destinationPanel.getConnector());
							connections.add(newConnection);
						} else {
							System.out.println("Problem connecting");
						}

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
				//System.out.println("CHecking points");
			};

		});
	}
	
	public void checkPoints(MouseEvent e) {
		for (ConnectablePanel box : boxes) {
			
			// If we already are selecting this connector, ignore it
			if (box == originPanel)
				continue;
			
			// Check if we're hovering over a connector
			boolean currentHover = box.checkPointHover(e);

			if (currentHover) {
				// Keep track that this is the connector we found
				currentPanel = box;
				
				// If we're dragging, we found a destinationb ox
				if (dragging) {
					destinationPanel = box;
				}
			} else {
				// We aren't hovering over anything, make sure there's no destination panel
				if (currentPanel == box) {
					destinationPanel = null;
				}
			}
			
			// Only repaint if we've changed hovering states 
			// ** looks like I'm repainting anyway...find out why
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
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

	@Override
	public void synthAdded(Synth synth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void instAdded(Instrument inst) {
//		// TODO Auto-generated method stub
		InstrumentModule instrumentModule = new InstrumentModule(100, 100, inst);
		instrumentModule.setPreferredSize(new Dimension(75, 100));
		instrumentModule.setSize(new Dimension(100, 75));
		instrumentModule.setLocation(200, 200);
		add(instrumentModule);
		boxes.addAll(instrumentModule.getConnectablePanels());
		System.out.println("Instrument added to panel");

		updateUI();
		repaint();
	}
}

