package com.learn.californium.server.minimalexample;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.minimalexample.mysrc.MyResource_Mwe;
import com.learn.californium.server.myresc.MyResource;

/**
 * 
 * MWE means minimal working example
 * 也就是最简化 的例子
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
		// name "hello" is letter sensitive
		server.add(new MyResource_Mwe("hello"));
		// start
		server.start(); // does all the magic

	}

}
