package net.alexgraham.thesis.tests.demos.simplemvc;
//Controller.java
//(C) Joseph Mack 2011, jmack (at) wm7d (dot) net, released under GPL v3 (or any later version)

//inspired by Joseph Bergin's MVC gui at http://csis.pace.edu/~bergin/mvc/mvcgui.html

//Controller is a Listener

class Controller implements java.awt.event.ActionListener {

	//Joe: Controller has Model and View hardwired in
	Model model;
	View view;

	Controller() {	
		System.out.println ("Controller()");
	} //Controller()

	//invoked when a button is pressed
	public void actionPerformed(java.awt.event.ActionEvent e){
		//uncomment to see what action happened at view
		/*
		System.out.println ("Controller: The " + e.getActionCommand() 
			+ " button is clicked at " + new java.util.Date(e.getWhen())
			+ " with e.paramString " + e.paramString() );
		*/
		System.out.println("Controller: acting on Model");
		model.incrementValue();
	} //actionPerformed()

	//Joe I should be able to add any model/view with the correct API
	//but here I can only add Model/View
	public void addModel(Model m){
		System.out.println("Controller: adding model");
		this.model = m;
	} //addModel()

	public void addView(View v){
		System.out.println("Controller: adding view");
		this.view = v;
	} //addView()

	public void initModel(int x){
		model.setValue(x);
	} //initModel()

} //Controller
