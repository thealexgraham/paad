package net.alexgraham.thesis.tests.demos.drawing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.alexgraham.thesis.ui.components.TriangleShape;

public class TriangleTest {

    public static void main(String[] args) {
        new TriangleTest();
    }

    public TriangleTest() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
//                frame.pack();
                frame.setSize(1000, 750);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        private TriangleShape triangleShape;
        private TriangleShape baseTriangleShape;
        private Polygon poly;

        public TestPane() {
//            triangleShape = new TriangleShape(TriangleShape.APEX, new Point(100, 100), 0, 20, 25);
//            baseTriangleShape = new TriangleShape(new Point(100, 75), TriangleShape.BASE, 0, 20, 25);

            poly = new Polygon(
                    new int[]{50, 100, 0},
                    new int[]{0, 100, 100},
                    3);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
         
            g.setColor(Color.GREEN);
            triangleShape.draw(g2);
            
            g2.setColor(Color.RED);
            baseTriangleShape.draw(g2);
           
            g2.setColor(Color.black);
            g2.fillOval(0, 0, 5, 5);
        }
   }

}