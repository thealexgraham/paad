package net.alexgraham.thesis.ui.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.models.PlayerModel.PlayerModelListener;
import net.alexgraham.thesis.supercollider.models.SynthModel.SynthModelListener;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.supercollider.synths.ChangeFunc;
import net.alexgraham.thesis.supercollider.synths.Chooser;
import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.PatternGen;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.TaskRunner;
import net.alexgraham.thesis.tests.demos.simplemvc.Model;
import net.alexgraham.thesis.ui.modules.ChangeFuncModule;
import net.alexgraham.thesis.ui.modules.ChooserModule;
import net.alexgraham.thesis.ui.modules.EffectModule;
import net.alexgraham.thesis.ui.modules.InstrumentModule;
import net.alexgraham.thesis.ui.modules.PatternGenModule;
import net.alexgraham.thesis.ui.modules.RoutinePlayerModule;
import net.alexgraham.thesis.ui.modules.SynthModule;
import net.alexgraham.thesis.ui.modules.TaskRunnerModule;

public class LineConnectPanel extends JPanel implements SynthModelListener, PlayerModelListener {
	
	public interface SynthSelectListener {
		public void selectSynth(Synth synth);
		public void deselectSynth();
	}
	
	boolean pointHovering = false;
	boolean dragging = false;
	ConnectablePanel currentPanel;
	
	ConnectablePanel originPanel;
	ConnectablePanel destinationPanel;
	
	Point connectOrigin;
	Point connectDestination;
	Connector originConnector;
	

	ArrayList<ConnectablePanel> boxes = new ArrayList<ConnectablePanel>();
	//CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();
	CopyOnWriteArrayList<Connection> connections = App.connectionModel.getCopyConnections();
	
	CopyOnWriteArrayList<ModulePanel> modules = new CopyOnWriteArrayList<ModulePanel>();
	
	Connection clicked;
	
	private CopyOnWriteArrayList<SynthSelectListener> synthSelectListeners = 
			new CopyOnWriteArrayList<SynthSelectListener>();
	
	boolean requireMouse = false;
	
	public boolean isRequiringMouse() { return requireMouse || pointHovering || dragging; }
	
	public CopyOnWriteArrayList<Connection> getConnections() {
		return App.connectionModel.getCopyConnections();
	}

	public LineConnectPanel() {
		
		setOpaque(true);
		setBackground(Color.DARK_GRAY.darker());
		setLayout(null);
		
		// Set able to be focusable for key clicks
		setFocusable(true);

		// Create mouse and key listeners
		this.createKeyListeners();
		this.createMouseListeners();
		
		setBorder(BorderFactory.createDashedBorder(Color.black));
		
		// Listen for synths being added
		App.synthModel.addListener(this);
		//App.playerModel.addListener(this);
	}
	
	public void addSynthSelectListener(SynthSelectListener listener) {
		synthSelectListeners.add(listener);
	}
	
	/**
	 * Creates key listeners for delete key, backspace and server reboot
	 */
	public void createKeyListeners() {
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
				System.out.println("Key typed");
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_F9) {
					if (e.isControlDown()) {
						try {
							App.sc.rebootServer();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}		
					}

				}

				if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					System.out.println("Delete key pressed");
					if (clicked != null) {
						removeConnection(clicked);
						repaint();
					}
				}
			}
		});
	}
	
	
	/**
	 * Creates mouse listenres for dragging,
	 */
	public void createMouseListeners() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				
				// Change current focus
				LineConnectPanel.this.requestFocusInWindow();
				
				// Deselect all children
				for (Component component : getComponents()) {
					if (component instanceof ModulePanel) {
						((ModulePanel) component).deselect();
					}
				}
				
				// Notify of synth deselecting
				fireSynthDeselectedEvent();
				
				// Start dragging 
				if (pointHovering && !dragging) {
					originPanel = currentPanel;
					connectOrigin = originPanel.getConnectionLocation(); // new
																		// Point(e.getX(),
																		// e.getY());
					connectDestination = new Point(e.getPoint());
					dragging = true;
				}
				
				// Check if clicking a line
				if (!pointHovering) {
					clicked = getClickedConnection(e.getPoint(), 10);
					if (clicked != null) {
						clicked.setClicked(true);
					} else {
						for (Connection connection : getConnections()) {
							connection.setClicked(false);
						}
						
						LineConnectPanel.this.requestFocusInWindow();
					}
					repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				super.mouseReleased(e);
				
				// If the mouse was dragging, see if we landed on anything
				if (dragging) {
					dragging = false;
					
					// Found a destination panel, try and connect the points
					if (destinationPanel != null) {
						System.out.println("Destination box exists");
						connectPoints(originPanel.getConnector(), destinationPanel.getConnector());
					}
					
					// Reset origin and destination
					originPanel = null;
					destinationPanel = null;
					
					// Check for hovering
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
	
	public void connectPoints(Connector origin, Connector destination) {
		Connection newConnection = new Connection(origin, destination);

		// We have a destination and an origin
		if (newConnection.connectModules()) {
			// If either of them was able to connect, that means the connection was good		
			getConnections().add(newConnection);
		} else {
			System.out.println("Problem connecting");
		}
	}
	
	public void removeConnection(Connection theConnection) {
		
		if (theConnection.disconnectModules()) {
			System.out.println("Disconnection successful");
		} else {
			System.out.println("Disconnection probleM");
		}
		getConnections().remove(theConnection);
		clicked = null;
	}

	public void removeModule(ModulePanel panel) {
		
		// Check if there are any current connections in this panel's connectables
		for (ConnectablePanel connectablePanel : panel.getConnectablePanels()) {
			Connector currentConnector = connectablePanel.getConnector();
			
			// Go through all of the connections
			for (Connection	connection : getConnections()) {
				
				// If either of the connections are from this connector, 
				if (currentConnector == connection.getOrigin() ||
					currentConnector == connection.getDestination()) {
					this.removeConnection(connection);
				}
			}
		}
		
		boxes.removeAll(panel.getConnectablePanels());
		remove(panel);
		
		App.data.removeModule(panel);
		App.synthModel.removeInstance(panel.getInstance());
		
		repaint();
	}
	
	/**
	 * Check if points are hovering
	 * @param e
	 */
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
		
		for (Connection connection : getConnections()) {
			Line2D line = connection.getLine();
			if (line.ptLineDist(point) < distance) {
				return connection;
			}
		}
		
		return null;
	}
	
	// Painting 
	//--------------------

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g);	
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paint(g);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Paint connectors
		for (ConnectablePanel box : boxes) {
			box.paintConnectors(g);
		}
		
		// Paint dragging l9ine
		if (dragging) {
			g.setColor(originPanel.getConnectorUI().getColor());
			g.drawLine(connectOrigin.x,
					connectOrigin.y,
					connectDestination.x,
					connectDestination.y);

		}
		
		// Paint connections
		for (Connection connection : getConnections()) {
					
			if (connection.isFlashing()) {
				g2.setColor(Color.white);
			} else {
				g2.setColor(connection.getOrigin().getColor());

			}
			
			Line2D line = connection.getLine();
			
			if (connection.isClicked())
				g2.setStroke(new BasicStroke(3));
			else
				g2.setStroke(new BasicStroke(1));
			
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.draw(line);
			g2.setStroke(new BasicStroke(1));
		}
	}
	
	
	// SynthSelectListener Notifications
	// -------------------------------------
	
	public void fireSynthSelectedEvent(Synth synth) {
		for (SynthSelectListener synthSelectListener : synthSelectListeners) {
			synthSelectListener.selectSynth(synth);
		}
	}
	
	public void fireSynthDeselectedEvent() {
		for (SynthSelectListener synthSelectListener : synthSelectListeners) {
			synthSelectListener.deselectSynth();
		}
	}
	
	//TODO: refactor
	public void moduleSelected(ModulePanel module) {
		if (module instanceof InstrumentModule) {
			fireSynthSelectedEvent(((InstrumentModule) module).getInstrument());
		} else if (module instanceof EffectModule) {
			fireSynthSelectedEvent(((EffectModule) module).getEffect());
		} else if (module instanceof ChangeFuncModule) {
			fireSynthSelectedEvent(((ChangeFuncModule) module).getChangeFunc());
		} else if (module instanceof SynthModule) {
			fireSynthSelectedEvent(((SynthModule)module).getSynth());
		}
	}
	
	public void addModuleForInstance(Instance instance) {
		
		ModulePanel module = null;
		
		if (instance.getClass() == Synth.class) {
			module = new SynthModule(100, 100, (Synth) instance);
			
		} else if (instance.getClass() == Instrument.class) {
			
			module = new InstrumentModule(100, 100, (Instrument) instance);
//			module.setPreferredSize(new Dimension(75, 100));
//			module.setSize(new Dimension(100, 75));
		
		} else if (instance.getClass() == Effect.class) {
			module = new EffectModule(100, 300, (Effect) instance);
		} else if (instance.getClass() == ChangeFunc.class) {
			module = new ChangeFuncModule(100, 300, (ChangeFunc) instance);
		} else if (instance.getClass() == PatternGen.class) {
			module = new PatternGenModule(100, 300, (PatternGen) instance);
		} else if (instance.getClass() == RoutinePlayer.class){
			module = new RoutinePlayerModule((RoutinePlayer) instance);
		} else if (instance.getClass() == Chooser.class){
			module = new ChooserModule(100, 300, (Chooser)instance); 
		} else if (instance.getClass() == TaskRunner.class) {
			module = new TaskRunnerModule((TaskRunner)instance);
		} else {
			System.err.println("No module for class");
			return;
		}
		

		
		module.setInstance(instance);
		module.setLocation(instance.getLocation());
		module.setOwner(this);

		add(module);
		boxes.addAll(module.getConnectablePanels());
		modules.add(module);
		
		instance.setCurrentModule(module);
		
		App.data.addModule(module);
		
		updateUI();
		repaint();
	}
	
	public void addConnectablePanels(ArrayList<ConnectablePanel> panels) {
		boxes.addAll(panels);
	}
	
	public void refreshModules() {
		// Remove anything currently here
		removeAll();
		boxes.removeAll(boxes);
		
//		for (ModulePanel panel : App.data.getModulePanels()) {
//			// Add the actual panel to the surface
//			add(panel);
//			// Add the connectable panels to the list
//			boxes.addAll(panel.getConnectablePanels());
//			panel.refresh();
//		}
		
		updateUI();
		repaint();
	}
	
	public void removeConnectablePanels(List<ConnectablePanel> panels) {
		boxes.removeAll(panels);
	}
	
	public void addConnectablePanels(List<ConnectablePanel> panels) {
		boxes.addAll(panels);
	}


	// SynthModelListener
	// --------------------------
	
	@Override
	public
	void instanceAdded(Instance instance) {
		addModuleForInstance(instance);
	}

	
	// PlayerModelListener
	// -------------------------
	@Override
	public void playerAdded(RoutinePlayer player) {
		// Create the routine player's panel
		RoutinePlayerModule playerPanel = new RoutinePlayerModule(player);
		playerPanel.setInstance(player);
		playerPanel.setLocation(10, 10);
		
		boxes.addAll(playerPanel.getConnectablePanels());
		add(playerPanel);
		
		modules.add(playerPanel);
		
		updateUI();
	}

	@Override
	public void playerRemoved(RoutinePlayer player) {
		
		
	}
	
	// Saving and Loading
	// ------------------------
	
	public void saveModules() throws IOException {
    	// Write to disk with FileOutputStream
    	String filename = "test.modules";
    	File out = new File(System.getProperty("user.home") + "\\test\\" + filename);
    	
    	FileOutputStream f_out = new 
    		FileOutputStream(out);

    	// Write object with ObjectOutputStream
    	ObjectOutputStream obj_out = new
    		ObjectOutputStream (f_out);

    	// Write object out to disk
    	obj_out.writeObject ( modules );
    	obj_out.close();
    	f_out.close();
	}
	

	public void loadModules() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
    	String filename = "test.modules";
    	File in = new File(System.getProperty("user.home") + "\\test\\" + filename);
		
        FileInputStream fileIn = new FileInputStream(in);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        Object o = objectIn.readObject();
        if (o instanceof CopyOnWriteArrayList<?>) {
        	modules = (CopyOnWriteArrayList<ModulePanel>) o;
        	removeAll();
        	for (ModulePanel modulePanel : modules) {
        		add(modulePanel);
			}
        }
        objectIn.close();
        fileIn.close();	
        
		updateUI();
		repaint();
	}

// SynthModel Listener Old
	@Override
	public void synthAdded(Synth synth) {
		addModuleForInstance(synth);
		
//		SynthModule module = new SynthModule(100, 100, synth);
//		module.setInstance(synth);
//		
//		module.setPreferredSize(new Dimension(75, 100));
//		module.setSize(new Dimension(100, 75));
//		module.setLocation(200, 200);
//		add(module);
//		boxes.addAll(module.getConnectablePanels());
//		
//		module.setOwner(this);
//		System.out.println("Instrument added to panel");
//
//		modules.add(module);
//			
//		updateUI();
//		repaint();
	}

	@Override
	public void instAdded(Instrument inst) {
		addModuleForInstance(inst);

//		InstrumentModule module = new InstrumentModule(100, 100, inst);
//		module.setInstance(inst);
//		
//		module.setPreferredSize(new Dimension(75, 100));
//		module.setSize(new Dimension(100, 75));
//		module.setLocation(200, 200);
//		add(module);
//		boxes.addAll(module.getConnectablePanels());
//		
//		module.setOwner(this);
//		System.out.println("Instrument added to panel");
//
//		modules.add(module);
//		
//		updateUI();
//		repaint();
	}
	
	@Override
	public void effectAdded(Effect effect) {
//		
//		EffectModule module = new EffectModule(100, 300, effect);
//		module.setInstance(effect);
//		
//		module.setSize(module.getPreferredSize());
//		module.validate();
//		
//		module.setLocation(200, 200);
//		add(module);
//		boxes.addAll(module.getConnectablePanels());
//		
//		module.setOwner(this);
//		
//		modules.add(module);
//		
//		updateUI();
//		repaint();
	}
	
	//TODO: This is also literally the same thing....
	@Override
	public void changeFuncAdded(ChangeFunc changeFunc) {
		// TODO Auto-generated method stub
//		ChangeFuncModule module = new ChangeFuncModule(100, 300, changeFunc);
//		module.setInstance(changeFunc);
//		
//		module.setSize(module.getPreferredSize());
//		module.validate();
//		
//		module.setLocation(200, 200);
//		add(module);
//		boxes.addAll(module.getConnectablePanels());
//		
//		module.setOwner(this);
//		
//		modules.add(module);
//		
//		updateUI();
//		repaint();
	}
	
	@Override
	public void patternGenAdded(PatternGen patternGen) {
//		PatternGenModule module = new PatternGenModule(100, 300, patternGen);
//		module.setInstance(patternGen);
//		
//		module.setSize(module.getPreferredSize());
//		module.validate();
//		
//		module.setLocation(200, 200);
//		add(module);
//		boxes.addAll(module.getConnectablePanels());
//		
//		module.setOwner(this);
//		
//		modules.add(module);
//		
//		updateUI();
//		repaint();
	}


}

