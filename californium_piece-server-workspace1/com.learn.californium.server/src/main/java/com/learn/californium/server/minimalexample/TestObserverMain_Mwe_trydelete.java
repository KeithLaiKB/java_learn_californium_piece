package com.learn.californium.server.minimalexample;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.minimalexample.myresc.MyObserverResource_Con_Mwe;


/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE 意思就是  简化的例子														</br>
 * &emsp;						for testing the observer												</br>
 * 	
 *
 *
 * @author laipl
 *
 */
public class TestObserverMain_Mwe_trydelete  {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
		// 如果不填参数，则默认端口是5683
		// 这里我尝试自己定义一个端口5656
		//MyTestCoapServer server = new MyTestCoapServer(5683);
		//MyTestCoapServer server = new MyTestCoapServer(5656);
		CoapServer server = new CoapServer(5656);
		//
		//
		//
		//------------------------resource settings-------------------------------------
		// 注意 这里的 hello 大小写是敏感的
		// 因为 client那边 是根据 coap://localhost:5656/hello 来发送请求的
		/*
		MyObserverResource myobResc1 = new MyObserverResource("hello_observer");
		myobResc1.setMyCoapServer(server);
		*/
		MyObserverResource_Con_Mwe myobResc1 = new MyObserverResource_Con_Mwe("hello_observer");
		//
		//
		//
		MyObserverResource_Con_Mwe myobResc1_c1 = new MyObserverResource_Con_Mwe("hello_observer_child1");
		MyObserverResource_Con_Mwe myobResc1_c2 = new MyObserverResource_Con_Mwe("hello_observer_child2");
		MyObserverResource_Con_Mwe myobResc1_c3 = new MyObserverResource_Con_Mwe("hello_observer_child3");
		//
		myobResc1_c2.add(myobResc1_c3);
		myobResc1_c1.add(myobResc1_c2);
		myobResc1.add(myobResc1_c1);
		//------------------------operate server-------------------------------------
		//
		server.add(myobResc1);
		//
		server.start(); // does all the magic
		
	}

	
	


}
