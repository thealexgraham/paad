package net.alexgraham.thesis.tests;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class IconTest {
	  public static void main(String[] argv) throws IOException {

		    JFrame frame = new JFrame();
		    
		    JPanel panel = new JPanel();
		    
		    JLabel field = new JLabel("This is my text field");
		    
		    URL imageResource = IconTest.class.getResource("/images/testalpha.png");
		    BufferedImage image = ImageIO.read(imageResource);
		    BufferedImage reverse = invertImage2(image);
		    
		    Image newImage = reverse.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		    ImageIcon icon = new ImageIcon(newImage);
		    
	        field.setHorizontalTextPosition(SwingConstants.LEADING);
	        field.setAlignmentX(SwingConstants.RIGHT);
		    
		    field.setIcon(icon);
		    panel.add(field);
		    
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.getContentPane().add(panel);
		    frame.setSize(400, 500);
		    frame.setVisible(true);

	  }

	  private static BufferedImage invertImage2 (BufferedImage source) {
		  BufferedImage reverse = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		  RescaleOp op = new RescaleOp(-1.0f, 255f, null);	
		  op.filter(source, reverse);
		  return reverse;
	  }
	  
		private static void invertImage(BufferedImage inputFile) {
						
			 for (int x = 0; x < inputFile.getWidth(); x++) {
		            for (int y = 0; y < inputFile.getHeight(); y++) {
		                int rgba = inputFile.getRGB(x, y);
		                Color col = new Color(rgba, true);
		                col = new Color(255 - col.getRed(),
		                                255 - col.getGreen(),
		                                255 - col.getBlue(), 
		                                col.getAlpha());
		                
		                inputFile.setRGB(x, y, col.getRGB());
		            }
		        }

		}
}
