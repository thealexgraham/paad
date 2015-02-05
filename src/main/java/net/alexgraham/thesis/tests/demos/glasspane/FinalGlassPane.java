package net.alexgraham.thesis.tests.demos.glasspane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * GlassPane tutorial
 * "A well-behaved GlassPane"
 * http://weblogs.java.net/blog/alexfromsun/
 * <p/>
 * This is the final version of the GlassPane
 * it is transparent for MouseEvents,
 * and respects underneath component's cursors by default,
 * it is also friedly for other users,
 * if someone adds a mouseListener to this GlassPane
 * or set a new cursor it will respect them
 *
 * @author Alexander Potochkin
 */
public class FinalGlassPane extends JPanel implements AWTEventListener {
    private final JFrame frame;
    private Point point = new Point();

    public FinalGlassPane(JFrame frame) {
        super(null);
        this.frame = frame;
        setOpaque(false);
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.GREEN.darker());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        int d = 22;
        g2.fillRect(getWidth() - d, 0, d, d);
        if (point != null) {
            g2.fillOval(point.x + d, point.y + d, d, d);
        }
        g2.dispose();
    }

    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) event;
            if (!SwingUtilities.isDescendingFrom(me.getComponent(), frame)) {
                return;
            }
            if (me.getID() == MouseEvent.MOUSE_EXITED && me.getComponent() == frame) {
                point = null;
            } else {
                MouseEvent converted = SwingUtilities.convertMouseEvent(me.getComponent(), me, frame.getGlassPane());
                point = converted.getPoint();
            }
            repaint();
        }
    }

    /**
     * If someone adds a mouseListener to the GlassPane or set a new cursor
     * we expect that he knows what he is doing
     * and return the super.contains(x, y)
     * otherwise we return false to respect the cursors
     * for the underneath components
     */
    public boolean contains(int x, int y) {
        if (getMouseListeners().length == 0 && getMouseMotionListeners().length == 0
                && getMouseWheelListeners().length == 0
                && getCursor() == Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)) {
            return false;
        }
        return super.contains(x, y);
    }
}

