package AlexGraham.TestMaven.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import AlexGraham.TestMaven.supercollider.SCLang;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

public class CommandLine {
	
	static int RECEIVE_PORT = 1291;
	static int SEND_PORT = 27320;

	public static void main(String[] args) throws IOException {
		String command = "cd";
		
		SCLang sc = new SCLang(SEND_PORT, RECEIVE_PORT);
		sc.startSCLang();
		
    	System.in.read();
		System.out.println("Done.");
    	Runtime.getRuntime().exit(1);

	}

}
