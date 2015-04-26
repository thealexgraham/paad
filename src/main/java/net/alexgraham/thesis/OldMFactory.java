package net.alexgraham.thesis;

public class OldMFactory {
//	
//	public static JPanel createIntParamPanel(ModulePanel module, IntParamModel model) {
//
//		// Set up the panels
//		JPanel togetherPanel = new JPanel();
//		//togetherPanel.setLayout(new BoxLayout(togetherPanel, BoxLayout.X_AXIS));
//		togetherPanel.setLayout(new SpringLayout());
//		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
//		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
//		
//
//		// Setup components
//		// -----------------------
//		JLabel paramNameLabel = new JLabel(model.getName());
//		JSpinner paramSpinner = new JSpinner(model);
//		
//	    JComponent field = ((JSpinner.DefaultEditor) paramSpinner.getEditor());
//	    Dimension prefSize = field.getPreferredSize();
//	    prefSize = new Dimension(30, prefSize.height);
//	    field.setPreferredSize(prefSize);
//	      
//		
//		// Add to panels
//		// -----------------------
//		leftPanel.add(paramNameLabel);
//		rightPanel.add(paramSpinner);
//		
//		togetherPanel.add(leftPanel);
//		togetherPanel.add(Box.createHorizontalStrut(20));
//		togetherPanel.add(Box.createHorizontalGlue());
//		togetherPanel.add(rightPanel);
//		
//		togetherPanel.setBackground(Color.red);
//		
//
//		return togetherPanel; //ModuleFactory.createSideConnectPanel(module, model.getConnector(ConnectorType.PARAM_CHANGE_IN), togetherPanel); 
//			
//	}
	
	
		public static JPanel createDoubleParamPanel(ModulePanel module, DoubleParamModel model) {
		
//		 Create Panels
//		// ------------------------
		JPanel togetherPanel = new JPanel();
		togetherPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0 ,0));
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		

		// Create connectables
		// -----------------------
		ConnectablePanel leftConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.LEFT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);

		ConnectablePanel rightConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);
		
		// Create Components
		// ----------------------
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(String.format("%.2f", model.getDoubleValue()));
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				paramValueLabel.setText(String.format("%.2f", model.getDoubleValue()));
			}
		});
		
		// Dial Components
		DialD dial = new DialD(model);
		dial.setForcedSize(new Dimension(15, 15));
		dial.setDrawText(false);

		//Add to panels
		// -----------------------
		
		
		// Left Panel
		leftPanel.add(leftConnectable);
		leftPanel.add(new JLabel(new ExportIcon(model)));
		leftPanel.add(Box.createHorizontalStrut(2));
		leftPanel.add(paramNameLabel);
		leftPanel.add(Box.createHorizontalStrut(7));

		// Right Panel
		rightPanel.add(paramValueLabel);
		rightPanel.add(Box.createHorizontalStrut(3));
		rightPanel.add(dial);
		rightPanel.add(rightConnectable);
		
		// Put it together
		// -----------------------
		togetherPanel.add(leftPanel);
		togetherPanel.add(rightPanel);
		
		dial.addMouseListener(new ParamMenuAdapter((ParamModel) model));
		togetherPanel.addMouseListener(new ParamMenuAdapter((ParamModel)model));
		togetherPanel.setBackground(Color.red);
		togetherPanel.setOpaque(true);
		    	togetherPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		return togetherPanel;
	}
	
	
//	public static JPanel createModelPanel(ModulePanel module, ParamModel baseModel) {
//		JPanel panel = 
//	}
	
}
