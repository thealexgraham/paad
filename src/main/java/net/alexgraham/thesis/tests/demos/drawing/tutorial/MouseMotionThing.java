package net.alexgraham.thesis.tests.demos.drawing.tutorial;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MouseMotionThing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame jFrame = new JFrame();
                jFrame.add(new MousePanel());
                jFrame.pack();
                jFrame.setSize(400, 400);
                jFrame.setVisible(true);
                jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }

    private static class MousePanel extends JPanel {

        Point p = new Point();

        public MousePanel() {
            setOpaque(true);
            addMouseMotionListener(new MouseHandler());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawLine(0, 0, p.x, p.y);
        }

        private class MouseHandler extends MouseAdapter {

            @Override
            public void mouseDragged(MouseEvent e) {
                update(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                update(e);
            }

            private void update(MouseEvent e) {
                System.out.println(e.paramString());
                MousePanel.this.p = e.getPoint();
                MousePanel.this.repaint();
            }
        }
    }
}
