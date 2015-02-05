package net.alexgraham.thesis.tests.demos;

import java.awt.*;
import javax.swing.*;

public class AbsoluteLayoutExample
{
    private void displayGUI()
    {
        JFrame frame = new JFrame("Absolute Layout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setOpaque(true);
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);

        JLabel label = new JLabel(
            "This JPanel uses Absolute Positioning"
                                    , JLabel.CENTER);
        label.setSize(300, 30);
        label.setLocation(5, 5);

        JButton button = new JButton("USELESS");
        button.setSize(100, 30);
        button.setLocation(95, 45);

        contentPane.add(label);
        contentPane.add(button);

        frame.setContentPane(contentPane);
        frame.setSize(310, 125);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String... args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new AbsoluteLayoutExample().displayGUI();
            }
        });
    }
}