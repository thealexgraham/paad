package net.alexgraham.thesis.tests.demos.serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Synth;

public class Serializer {
	public static <T extends java.io.Serializable> void serialize(T[] array) {
		try {
			String outPath = "/tmp/employee.ser";
			
			// Create the output stream with the path 
			FileOutputStream fileOut = new FileOutputStream(outPath);
			
			// Create the object stram
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			for (T t : array) {
				out.writeObject(t);
				out.close();
			}
			
			//finally close
			fileOut.close();
			System.out.printf("Serialized data is saved in " + outPath);
			
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	
	public static void serialize(Employee e) {
		try {
			String outPath = "/tmp/employee.ser";
			
			// Create the output stream with the path 
			FileOutputStream fileOut = new FileOutputStream(outPath);
			
			// Create the object stram
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			// Write the object and close
			out.writeObject(e);
			out.close();
			
			//finally close
			fileOut.close();
			System.out.printf("Serialized data is saved in " + outPath);
			
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	public static void deserialize(Employee e) {
	    e = null;
	    try
	    {
	       FileInputStream fileIn = new FileInputStream("/tmp/employee.ser");
	       ObjectInputStream in = new ObjectInputStream(fileIn);
	       e = (Employee) in.readObject();
	       in.close();
	       fileIn.close();
	    }catch(IOException i)
	    {
	       i.printStackTrace();
	       return;
	    }catch(ClassNotFoundException c)
	    {
	       System.out.println("Employee class not found");
	       c.printStackTrace();
	       return;
	    }
	    System.out.println("Deserialized Employee...");
	    System.out.println("Name: " + e.name);
	    System.out.println("Address: " + e.address);
	    System.out.println("SSN: " + e.SSN);
	    System.out.println("Number: " + e.number);
	    System.out.println("Baggae string" + e.baggage.theString);
		
	}

	
	public static void main(String[] args) {
		Employee e = new Employee();
		e.name = "Reyan Ali";
		e.address = "Phokka Kuan, Ambehta Peer";
		e.SSN = 11122333;
		e.number = 101;
		serialize(e);
		deserialize(e);
	}

}
