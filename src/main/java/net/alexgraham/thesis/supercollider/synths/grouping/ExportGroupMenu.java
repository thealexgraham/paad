package net.alexgraham.thesis.supercollider.synths.grouping;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.ColorIcon;

public class ExportGroupMenu extends JMenu {
		
    JMenuItem anItem;
    public ExportGroupMenu(ParamModel paramModel){

    	setText("Select Export Group");
    	
    	JMenuItem menuItem;
        menuItem = new JMenuItem("Create Group...");
        menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
                String s = (String)JOptionPane.showInputDialog(
                        null,
                        "Enter name for new group",
                        "New Group",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "Export group");
                ParamGroup group = App.paramGroupModel.newExportGroup(s);
                paramModel.setExportGroup(group);
                group.addParamModel(paramModel);
                App.mainWindow.repaint();
//
//				while ((parent = parent.getParent()) != null) {
//					System.out.println("Parent " + parent.getClass().getSimpleName() + " repaiting");
//					parent.repaint();
//				}
                
			}
                
		});
        add(menuItem);

        addSeparator();
        
        ParamGroup currentGroup = paramModel.getExportGroup();
        List<ParamGroup> paramGroupList = App.paramGroupModel.getExportGroups();

        ButtonGroup groupSelections = new ButtonGroup();
        
        boolean selected = currentGroup == null;
        menuItem = new JRadioButtonMenuItem("No Group", selected);
        groupSelections.add(menuItem);
        add(menuItem);
        
        menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentGroup != null)
					currentGroup.removeParamModel(paramModel);
				paramModel.setExportGroup(null);
                App.mainWindow.repaint();
			}
		});
        
        add(menuItem);
        
        for (ParamGroup paramGroup : paramGroupList) {
			selected = currentGroup == paramGroup;
			menuItem = new JRadioButtonMenuItem(paramGroup.getName(), new ColorIcon(paramGroup.getGroupColor()), selected);
			
	        menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Remove this model from its previous group
					if (currentGroup != null)
						currentGroup.removeParamModel(paramModel);
					
					// Set the group and add it to the export
					paramModel.setExportGroup(paramGroup);
					paramGroup.addParamModel(paramModel);
	                App.mainWindow.repaint();
				}
			});
			
			groupSelections.add(menuItem);
			add(menuItem);
		}
    }
    
    private String getSelectedString(ParamGroup ownerGroup, ParamGroup checkGroup) {
    	if (ownerGroup == checkGroup) {
    		return " X";
    	} else {
    		return "";
    	}
    }
}