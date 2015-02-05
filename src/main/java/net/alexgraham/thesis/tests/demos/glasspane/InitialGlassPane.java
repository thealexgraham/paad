package net.alexgraham.thesis.tests.demos.glasspane;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * GlassPane tutorial
 * "A well-behaved GlassPane"
 * http://weblogs.java.net/blog/alexfromsun/
 * <p/>
 * This the initial version of GlassPane mostly copied from   
 * http://java.sun.com/docs/books/tutorial/uiswing/components/rootpane.html
 * 
 * @author Alexander Potochkin
 */ 
public class InitialGlassPane extends JPanel {
    private Point point;

    public InitialGlassPane(JFrame frame) {
        super(null);
        setOpaque(false);
        MouseEventRedispatcher listener = new MouseEventRedispatcher(this, frame.getLayeredPane());
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setColor(Color.RED);
        int d = 22; 
        g2.fillRect(getWidth() - d, 0, d, d);
        if (point != null) {            
            g2.fillOval(point.x + d, point.y + d, d, d);
        }
        g2.dispose();
    }

    class MouseEventRedispatcher extends MouseInputAdapter {
        private final InitialGlassPane glassPane;
        private final Container contentPane;


        public MouseEventRedispatcher(InitialGlassPane glassPane, Container contentPane) {
            this.glassPane = glassPane;
            this.contentPane = contentPane;
        }

        public void mouseMoved(MouseEvent e) {
            redispatchMouseEvent(e, true);
        }

        public void mouseDragged(MouseEvent e) {
            redispatchMouseEvent(e, true);
        }

        public void mouseClicked(MouseEvent e) {
            redispatchMouseEvent(e, true);
        }

        public void mouseEntered(MouseEvent e) {
            redispatchMouseEvent(e, true);
        }

        public void mouseExited(MouseEvent e) {
            redispatchMouseEvent(e, true);
        }

        public void mousePressed(MouseEvent e) {
            redispatchMouseEvent(e, true);
        }

        public void mouseReleased(MouseEvent e) {
            redispatchMouseEvent(e, true);
        }

        //A more finished version of this method would
        //handle mouse-dragged events specially.
        private void redispatchMouseEvent(MouseEvent e,
                                          boolean repaint) {
            Point glassPanePoint = e.getPoint();
            Container container = contentPane;
            Point containerPoint = SwingUtilities.convertPoint(glassPane,
                    glassPanePoint,
                    contentPane);
            //The mouse event is probably over the content pane.
            //Find out exactly which component it's over.  
            Component component =
                    SwingUtilities.getDeepestComponentAt(container,
                            containerPoint.x,
                            containerPoint.y);

            if (component != null) {
                //Forward events
                Point componentPoint = SwingUtilities.convertPoint(glassPane,
                        glassPanePoint,
                        component);
                component.dispatchEvent(new MouseEvent(component,
                        e.getID(),
                        e.getWhen(),
                        e.getModifiers(),
                        componentPoint.x,
                        componentPoint.y,
                        e.getClickCount(),
                        e.isPopupTrigger()));
            } else {
                glassPanePoint = null;
            }
        
            //Update the glass pane if requested.
            if (repaint) {
                glassPane.setPoint(glassPanePoint);
                glassPane.repaint();
            }
        }
    }
}
