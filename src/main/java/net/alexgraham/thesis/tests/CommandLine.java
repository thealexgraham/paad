package net.alexgraham.thesis.tests;

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
import java.util.function.Function;

import net.alexgraham.thesis.supercollider.SCLang;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

public class CommandLine {
	
	static int RECEIVE_PORT = 1291;
	static int SEND_PORT = 27320;

	public static void main(String[] args) throws IOException {
//		String command = "cd";
//		
//		SCLang sc = new SCLang(SEND_PORT, RECEIVE_PORT);
//		sc.startSCLang();
		
		Function<String, String> myFun = new Function<String, String>() {

			@Override
			public String apply(String t) {
				
				return (t + " it worked");
			}
		};
		
		System.out.println(myFun.apply("Whatever"));
		
		
    	System.in.read();
		System.out.println("Done.");
    	Runtime.getRuntime().exit(1);

	}

}
