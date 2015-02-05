package net.alexgraham.thesis.tests.demos.glasspane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

/**
 * GlassPane tutorial
 * "A well-behaved GlassPane"
 * http://weblogs.java.net/blog/alexfromsun/
 * 
 * @author Alexander Potochkin
 */ 
public class TestApp extends JFrame {
    private JCheckBoxMenuItem switcher = new JCheckBoxMenuItem("GlassPane is visible");
    
    public TestApp() {
        super("GlassPane test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        JMenu menu = new JMenu("Options");
        menu.setMnemonic('o');
        bar.add(menu);        
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem first =
                new JRadioButtonMenuItem(new ChangeGlassPaneAction("First glassPane", new InitialGlassPane(this)));
        first.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_MASK));
        first.doClick();
        group.add(first);
        menu.add(first);
        JRadioButtonMenuItem second =
                new JRadioButtonMenuItem(new ChangeGlassPaneAction("Better glassPane", new BetterGlassPane(this)));
        second.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_MASK));
        group.add(second);
        menu.add(second);
        JRadioButtonMenuItem third =
                new JRadioButtonMenuItem(new ChangeGlassPaneAction("Final glassPane", new FinalGlassPane(this)));
        third.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_MASK));
        group.add(third);
        menu.add(third);
        menu.addSeparator();
        switcher.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.ALT_MASK));
        switcher.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TestApp.this.getGlassPane().setVisible(switcher.isSelected());
            }
        });
        menu.add(switcher);

        JDesktopPane pane = new JDesktopPane();
        JInternalFrame firstFrame = getFirstFrame();
        pane.add(firstFrame);
        JInternalFrame secondFrame = getSecondFrame();
        pane.add(secondFrame);
        add(pane);
        setSize(650, 350);
        setLocationRelativeTo(null);
    }


    private JInternalFrame getFirstFrame() {
        final JInternalFrame frame = new JInternalFrame("First frame");
        JTextField tf = new JTextField("JTextField", 15);
        JComponent panel = createVerticalPanel(
                new JCheckBox("JCheckBox"),
                new JRadioButton("JRadioButton"),
                tf);
        JPopupMenu popup = new JPopupMenu("Menu");
        popup.add(new JMenuItem("MenuItem"));
        popup.add(new JMenuItem("MenuItem"));
        popup.add(new JMenuItem("MenuItem"));
        tf.setComponentPopupMenu(popup);

        frame.add(panel);

        frame.setResizable(true);
        frame.addMouseListener(new FrameActivator(frame));
        frame.setBounds(15, 10, 300, 250);
        frame.setVisible(true);
        return frame;
    }

    private JInternalFrame getSecondFrame() {
        final JInternalFrame frame = new JInternalFrame("Second frame - tooltips and rollover effect");
        final JLabel label = new JLabel("I have a tooltip");
        label.setToolTipText("Hello from JLabel !");
        label.setOpaque(true);
        label.addMouseListener(new BackgroundColorer(label));
        final JButton button = new JButton("JButton");
        button.setToolTipText("Hello from JButton !");
        button.addMouseListener(new BackgroundColorer(button));
        JSlider slider = new JSlider(0, 100);
        slider.setToolTipText("Hello from slider !");
        slider.addMouseListener(new BackgroundColorer(slider));
        JComponent panel = createVerticalPanel(
                label,
                button,
                slider);

        frame.add(panel);

        frame.setResizable(true);
        frame.addMouseListener(new FrameActivator(frame));
        frame.setBounds(330, 10, 300, 250);
        frame.setVisible(true);
        return frame;
    }

    class BackgroundColorer extends MouseAdapter {
        private final Component c;
        private Color oldColor;

        public BackgroundColorer(Component c) {
            this.c = c;
            oldColor = c.getBackground();
        }

        public void mouseEntered(MouseEvent e) {
            c.setBackground(Color.GREEN);
        }

        public void mouseExited(MouseEvent e) {
            c.setBackground(oldColor);
        }
    }
    
    private JComponent createVerticalPanel(Component... c) {
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        for (Component component : c) {
            JPanel panel = new JPanel();
            panel.add(component);
            box.add(panel);
            box.add(Box.createVerticalGlue());
        }
        Box temp = Box.createHorizontalBox();
        temp.add(Box.createHorizontalGlue());
        temp.add(box);
        temp.add(Box.createHorizontalGlue());
        return temp;
    }

    
    class FrameActivator extends MouseAdapter {
        private final JInternalFrame frame;

        public FrameActivator(JInternalFrame frame) {
            this.frame = frame;
        }

        public void mouseEntered(MouseEvent e) {
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e1) {
                e1.printStackTrace();
            }
        }
    }

    class ChangeGlassPaneAction extends AbstractAction {
        private final Component glassPane;

        public ChangeGlassPaneAction(String name, final Component glassPane) {
            super(name);
            this.glassPane = glassPane;
            
            glassPane.addComponentListener(new ComponentAdapter() {
                public void componentHidden(ComponentEvent e) {
                    if (getGlassPane() == glassPane) {
                        switcher.setSelected(false);
                    }
                }

                public void componentShown(ComponentEvent e) {
                    if (getGlassPane() == glassPane) {
                        switcher.setSelected(true);
                    }
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            Component oldGlassPane = TestApp.this.getGlassPane();
            boolean isVisible = oldGlassPane.isVisible();
            oldGlassPane.setVisible(false);
            if (oldGlassPane instanceof AWTEventListener) {
                AWTEventListener al = (AWTEventListener) oldGlassPane;
                Toolkit.getDefaultToolkit().removeAWTEventListener(al);
            }
            TestApp.this.setGlassPane(glassPane);
            if (glassPane instanceof AWTEventListener) {
                AWTEventListener al = (AWTEventListener) glassPane;
                Toolkit.getDefaultToolkit().addAWTEventListener(al,
                        AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
            }
            glassPane.setVisible(isVisible);
            glassPane.repaint();
        }
    }


    public static void main(String[] args) throws Exception {
        TestApp testApp = new TestApp();
        testApp.setVisible(true);
    }
}
