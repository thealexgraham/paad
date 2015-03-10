package net.alexgraham.thesis.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;

public class ColorIcon implements Icon {
	Dimension size = new Dimension(5, 5);
	Color color = Color.red;
	
	public ColorIcon(Color color) {
		this.color = color;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(x, y, size.width, size.height);
	}

	@Override
	public int getIconWidth() {
		return size.width;
	}

	@Override
	public int getIconHeight() {
		return size.height;
	}
	
}