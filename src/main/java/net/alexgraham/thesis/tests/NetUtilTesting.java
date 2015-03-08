package net.alexgraham.thesis.tests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import de.sciss.net.OSCBundle;
import de.sciss.net.OSCChannel;
import de.sciss.net.OSCClient;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;

public class NetUtilTesting {
	public static void main( String protocol )
	{
//		postln( "NetUtilTest.client( \"" + protocol + "\" )\n" );
//		postln( "talking to localhost port 57110" );
		
		final Object		sync = new Object();
		final OSCClient		c;
		OSCBundle			bndl1, bndl2;
		Integer				nodeID;
		
		try {
			c = OSCClient.newUsing( protocol );
			c.setTarget( new InetSocketAddress( InetAddress.getLocalHost(), 57110 ));
			c.start();
		}
		catch( IOException e1 ) {
			e1.printStackTrace();
			return;
		}
		
		c.addOSCListener( new OSCListener() {
			public void messageReceived( OSCMessage m, SocketAddress addr, long time )
			{
				if( m.getName().equals( "/n_end" )) {
					synchronized( sync ) {
						sync.notifyAll();
					}
				}
			}
		});
		c.dumpOSC( OSCChannel.kDumpBoth, System.err );
		try {
			c.send( new OSCMessage( "/notify", new Object[] { new Integer( 1 )}));
		}
		catch( IOException e3 ) {
			e3.printStackTrace();
		}
		for( int i = 0; i < 4; i++ ) {
			bndl1	= new OSCBundle( System.currentTimeMillis() + 50 );
			bndl2	= new OSCBundle( System.currentTimeMillis() + 1550 );
			nodeID	= new Integer( 1001 + i );
			bndl1.addPacket( new OSCMessage( "/s_new", new Object[] { "default", nodeID, new Integer( 1 ), new Integer( 0 )}));
			bndl1.addPacket( new OSCMessage( "/n_set", new Object[] { nodeID, "freq", new Float( Math.pow( 2, (float) i / 6 ) * 441 )}));
			bndl2.addPacket( new OSCMessage( "/n_set", new Object[] { nodeID, "gate", new Float( -3f )}));
			try {
				c.send( bndl1 );
				c.send( bndl2 );
			
				synchronized( sync ) {
					sync.wait();
				}
			}
			catch( InterruptedException e1 ) { /* ignored */}
			catch( IOException e2 ) {
				e2.printStackTrace();
			}

//			postln( "  stopListening()" );
//			try {
//				c.stop();
//			}
//			catch( IOException e1 ) {
//				e1.printStackTrace();
//			}
		}
		try {
			c.send( new OSCMessage( "/notify", new Object[] { new Integer( 0 )}));
		}
		catch( IOException e3 ) {
			e3.printStackTrace();
		}
		
		c.dispose();
	}
}
