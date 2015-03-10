package net.alexgraham.thesis.supercollider.synths.grouping;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;

public class ParamMenuAdapter extends MouseAdapter {
	
	ParamModel paramModel;
	
	public ParamMenuAdapter(ParamModel model) {
		// TODO Auto-generated constructor stub
		this.paramModel = model;
	}
	
	class BlankMenu extends JPopupMenu {
	    JMenuItem anItem;
	    public BlankMenu(){
	        anItem = new JMenuItem("Menu");
	        anItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("It was done");
				}
			});
	        add(anItem);
	    }
	}
	
    public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
    	JPopupMenu menu = new JPopupMenu();
        ExportGroupMenu exportGroupMenu = new ExportGroupMenu(paramModel);
        menu.add(exportGroupMenu);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

}
