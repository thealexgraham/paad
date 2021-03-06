package net.alexgraham.thesis.ui.connectors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import com.sun.java.swing.plaf.motif.MotifBorders.BevelBorder;
import com.sun.swing.internal.plaf.metal.resources.metal;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion.Setting;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.AGHelper.TestEnum;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.ui.modules.ModuleFactory;

public abstract class ModulePanel extends JPanel {

	private JPanel interior;
	private ArrayList<ConnectablePanel> connectables = new ArrayList<ConnectablePanel>();
	
	protected Instance instance;
	
	public Instance getInstance() { return instance; }
	public void setInstance(Instance instance) { this.instance = instance; }

	public ArrayList<ConnectablePanel> getConnectablePanels() {
		return connectables;
	}

	public void addConnectablePanel(ConnectablePanel panel) {
		connectables.add(panel);
	}

//	private LineConnectPanel owner;

	class ModulePopup extends JPopupMenu {
		JMenuItem anItem;

		public ModulePopup() {

			anItem = new JMenuItem("Delete");
			anItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ModulePanel.this.removeSelf();
				}
			});
			add(anItem);
			
			anItem = new JMenuItem("Edit Definition");
			anItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean shouldRefresh = getInstance().editDef();
					if (shouldRefresh) {
						setupWindow(getInterior());
					}
				}
			});
			
			add(anItem);
		}
	}

	private boolean selected = false;

	public ModulePanel(int width, int height) {
		setup();
		setSize(new Dimension(width, height));
	}
	
	public ModulePanel(int width, int height, Instance instance) {
		this.setInstance(instance);
		setup();
//		setPreferredSize(new Dimension(width, height));
	}

	public ModulePanel() {
		setup();
	}
	
	public void refreshSize() {
		// Refresh size
		setSize(getPreferredSize());
		validate();
	}
	
	protected JLabel getTitleLabel() {
		
		JLabel label = new JLabel(instance.getName().replaceAll("\\d",""));
		
//		String type = instance.getDef().getType().toLowerCase();
//		
//	    URL imageResource = getClass().getResource("/images/" + type + ".png");
//	    
//	    if (imageResource != null) {
//			ImageIcon icon = ModuleFactory.getScaledIcon(imageResource, 25, 25);
//			
//			if (icon != null) {
//		        label.setHorizontalTextPosition(SwingConstants.LEADING);
//		        label.setAlignmentX(SwingConstants.RIGHT);
//		        label.setIcon(icon);
//			}	
//	    } else {
//	    	System.err.println("No image icon");
//	    }
		
//        label.setHorizontalTextPosition(SwingConstants.CENTER);
//        label.setAlignmentX(SwingConstants.CENTER);
		

        
		return label;
	}

	public void setOwner(LineConnectPanel owner) {
//		this.owner = owner;
	}
	
	public LineConnectPanel getOwner() {
		return (LineConnectPanel) this.getParent();
	}

	public void setup() {
		setFocusable(true);
		interior = new JPanel();
		setLayout(new GridLayout(1, 1));
		add(interior);

		DragListener listener = new DragListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		
		setBorder(createDefaultBorder());
		createKeyListeners();
	}
	
	public void refreshInterior() {
		instance.removeConnectorUIs();
		
		getOwner().removeConnectablePanels(connectables);
		// remove all Connectables since they will be added again
		connectables = new ArrayList<ConnectablePanel>();
		
		remove(interior);
		
		JPanel newInterior = new JPanel();
		
		setupWindow(newInterior);

		add(newInterior);

		// Refresh size
		setSize(getPreferredSize());
		validate();
		
		getOwner().addConnectablePanels(connectables);
		
		interior = newInterior;
    	App.mainWindow.repaint();

	}
	
	// Responsible for setting up internal
	//public abstract void setupWindow(Container pane);
	public abstract void setupPanels(ConnectablePanel topPanel, ConnectablePanel middlePanel, ConnectablePanel bottomPanel);
	
	public void setupWindow(Container pane) {
		
		ConnectablePanel topPanel;
		ConnectablePanel bottomPanel;
		ConnectablePanel middlePanel;
		
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
		
		topPanel = new ConnectablePanel(new FlowLayout());
		middlePanel = new ConnectablePanel();
		bottomPanel = new ConnectablePanel(new FlowLayout());
		
		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		setupPanels(topPanel, middlePanel, bottomPanel);
	
		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);
		
		// Refresh size
		setSize(getPreferredSize());
		validate();
	}
	
	public void refresh() {
		setFocusable(true);

		DragListener listener = new DragListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);

		createKeyListeners();
	}

	public void select() {
		if (getOwner() != null) {
			getOwner().deselectAll();
			getOwner().moduleSelected(this);
		}

		requestFocusInWindow();
		selected = true;
//		setBorder(BorderFactory.createDashedBorder(Color.DARK_GRAY.brighter().brighter()));
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		refreshSize();

	}

	public void deselect() {
		selected = false;
		
//		setBorder(BorderFactory.createEmptyBorder());
		setBorder(createDefaultBorder());
		
		
		// Refresh size
		refreshSize();
	}
	
	public Border createDefaultBorder() {
		return BorderFactory.createLineBorder(Color.DARK_GRAY.brighter().brighter());
	}

	public boolean isSelected() {
		return selected;
	}

	public void removeSelf() {
		LineConnectPanel connectPanel = (LineConnectPanel) this.getParent();
		connectPanel.removeModule(this);
	}

	public void createKeyListeners() {
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE
						|| e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (selected) {
						removeSelf();
					}
				}
			}
		});
	}

	class DragListener extends MouseInputAdapter {
		Point location;
		MouseEvent pressed;

		public void mousePressed(MouseEvent e) {
			boolean pointHover = mouseOverride(e);
			// redispatch(e);

			if (!pointHover) {
				pressed = e;

				if (!selected) {
					select();
				}

				if (e.isPopupTrigger()) {
					doPop(e);
				}

			} else {
				redispatch(e);

			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			super.mouseReleased(e);

			if (e.isPopupTrigger()) {
				doPop(e);
			}

			redispatch(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);
			redispatch(e);
		}

		public void mouseDragged(MouseEvent e) {
			boolean pointHover = mouseOverride(e);
			if (!pointHover) {
				// MoveablePanel component = (MoveablePanel) e.getComponent();
				Component component = e.getComponent();
				location = component.getLocation(location);
				int x = location.x - pressed.getX() + e.getX();
				int y = location.y - pressed.getY() + e.getY();
				component.setLocation(x, y);

				repaint();
				getParent().repaint();
				
				JPanel parent = (JPanel) getParent();
				
				if (y > parent.getHeight()) {
					parent.setPreferredSize(new Dimension(parent.getWidth(), y + getHeight() * 2));
					parent.revalidate();
				}
				
				if (x > parent.getWidth()) {
					parent.setPreferredSize(new Dimension(x + getWidth() * 2, parent.getHeight()));
					parent.revalidate();
				}
				
				instance.setLocation(new Point(x, y));

			} else {
				redispatch(e);
			}

		}

		public boolean mouseOverride(MouseEvent e) {
			Component parent = e.getComponent();
			while (parent.getClass() != LineConnectPanel.class) {
				parent = parent.getParent();
			}

			if (parent.getClass() == LineConnectPanel.class) {
				LineConnectPanel lineConnect = (LineConnectPanel) parent;
				return lineConnect.isRequiringMouse();
			} else {
				return false;
			}

		}

		public boolean checkHover(MouseEvent e) {
			boolean hovered = false;

			Component parent = e.getComponent();
			while (parent.getClass() != LineConnectPanel.class) {
				parent = parent.getParent();
			}
			MouseEvent converted = SwingUtilities.convertMouseEvent(e
					.getComponent(), e, parent);

			for (ConnectablePanel connectablePanel : connectables) {
				hovered = hovered
						|| connectablePanel.checkPointHover(converted);
				// connectablePanel.checkPointHover(SwingUtilities.convertMouseEvent(
				// e.getComponent(), e, connectablePanel));
				//
			}
			return hovered;
		}

		public void redispatch(MouseEvent e) {
//			Component parent = e.getComponent();
//			while ((parent = parent.getParent()).getClass() != LineConnectPanel.class) {
//				System.out.println("Found line connect");
				// System.out.println(parent.getClass());
				// if (parent.getClass() == LineConnectPanel.class) {
				
			LineConnectPanel parent = ModulePanel.this.getOwner();
			MouseEvent converted = SwingUtilities.convertMouseEvent(e
						.getComponent(), e, parent);
				parent.dispatchEvent(converted);
			//}
		}

		public LineConnectPanel getLineConnectPanel(MouseEvent e) {
			Component parent = e.getComponent();
			while (parent.getClass() != LineConnectPanel.class) {
				parent = parent.getParent();
			}

			return (LineConnectPanel) parent;
		}

		private void doPop(MouseEvent e) {
			ModulePopup menu = new ModulePopup();
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public JPanel getInterior() {
		return interior;
	}
}
