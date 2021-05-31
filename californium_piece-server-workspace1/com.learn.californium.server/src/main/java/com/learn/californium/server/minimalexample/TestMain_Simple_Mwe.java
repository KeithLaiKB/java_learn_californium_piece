package com.learn.californium.server.minimalexample;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.minimalexample.myresc.MyObserverResource_Con_Mwe;
import com.learn.californium.server.minimalexample.myresc.MyResource_Mwe;
import com.learn.californium.server.mydemo.myresc.MyResource;


/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE 意思就是  简化的例子														</br>
 * 	
 *
 *
 * @author laipl
 *
 */
public class TestMain_Simple_Mwe {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
		// define port to be 5656 
		CoapServer server = new CoapServer(5656);
		//
		//
		//------------------------resource settings-------------------------------------
		//
		// name "hello" is letter sensitive
		MyResource_Mwe myResc1 = new MyResource_Mwe("hello");
		//
		//
		//------------------------operate server-------------------------------------
		server.add(myResc1);
		// start
		server.start(); // does all the magic

	}

}
