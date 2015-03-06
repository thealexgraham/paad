package com.illposed.osc;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;

import sun.rmi.server.Dispatcher;

public class OSCPortInRemove extends OSCPortIn {

	public OSCPortInRemove(DatagramSocket socket) {
		super(socket);
		// TODO Auto-generated constructor stub
	}

	public OSCPortInRemove(int port) throws SocketException {
		super(port);
		// TODO Auto-generated constructor stub
	}

	public OSCPortInRemove(int port, Charset charset) throws SocketException {
		super(port, charset);
		// TODO Auto-generated constructor stub
	}


}
