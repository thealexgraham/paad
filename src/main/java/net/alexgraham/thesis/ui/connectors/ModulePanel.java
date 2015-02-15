package net.alexgraham.thesis.ui.connectors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import net.alexgraham.thesis.AGHelper.TestEnum;

public class ModulePanel extends JPanel {

	private JPanel interior;
	private ArrayList<ConnectablePanel> connectables = new ArrayList<ConnectablePanel>();

	public ArrayList<ConnectablePanel> getConnectablePanels() {
		return connectables;
	}

	public void addConnectablePanel(ConnectablePanel panel) {
		connectables.add(panel);
	}

	private LineConnectPanel owner;

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
		}
	}

	private boolean selected = false;

	public ModulePanel(int width, int height) {
		setup();
		setPreferredSize(new Dimension(width, height));
	}

	public ModulePanel() {
		setup();
	}

	public void setOwner(LineConnectPanel owner) {
		this.owner = owner;
	}

	public void setup() {
		setFocusable(true);
		interior = new JPanel();
		setLayout(new GridLayout(1, 1));
		add(interior);

		DragListener listener = new DragListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);

		createKeyListeners();
	}

	public void select() {
		if (owner != null) {
			owner.moduleSelected(this);
		}

		requestFocusInWindow();
		selected = true;
		setBorder(BorderFactory.createLineBorder(Color.black));

	}

	public void deselect() {
		selected = false;
		setBorder(BorderFactory.createEmptyBorder());
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
				getParent().getParent().getParent().repaint();

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
			Component parent = e.getComponent();
			while ((parent = parent.getParent()).getClass() != LineConnectPanel.class) {
				// System.out.println(parent.getClass());
				// if (parent.getClass() == LineConnectPanel.class) {
				MouseEvent converted = SwingUtilities.convertMouseEvent(e
						.getComponent(), e, parent);
				parent.dispatchEvent(converted);
			}
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
