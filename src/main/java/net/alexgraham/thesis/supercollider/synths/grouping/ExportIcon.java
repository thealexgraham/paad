package net.alexgraham.thesis.supercollider.synths.grouping;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;

import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;

public class ExportIcon implements Icon, Serializable {
	Dimension size = new Dimension(5, 5);
	ParamModel model = null;
	
	public ExportIcon(ParamModel model) {
		this.model = model;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (model.getExportGroup() != null) {
			g.setColor(model.getExportGroup().getGroupColor());
			g.fillRect(x, y, size.width, size.height);
		}
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
