package net.alexgraham.thesis.tests.demos.serialize;
import java.io.*;

public class Employee implements java.io.Serializable
{
   public String name;
   public String address;
   public transient int SSN;
   public int number;
   public void mailCheck()
   {
      System.out.println("Mailing a check to " + name
                           + " " + address);
   }
   
   public Baggage baggage = new Baggage();
   
}


