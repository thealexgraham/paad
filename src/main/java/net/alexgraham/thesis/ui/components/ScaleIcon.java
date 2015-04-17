package net.alexgraham.thesis.ui.components;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ScaleIcon extends ImageIcon {
	public ScaleIcon(String path, int width, int height) {
		try {
			
		    URL imageResource = getClass().getResource(path);
		    BufferedImage image;
			
			image = ImageIO.read(imageResource);
		    Image newImage = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		    super(newImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
