package com.learn.californium.server.minimalexample;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.minimalexample.myresc.MyResource_Mwe;





/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE ��˼����  �򻯵�����														</br>
 * 																										</br>
 * 
 * </p>
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
		CoapServer server = new CoapServer(5683);
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
