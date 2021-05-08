package com.learn.californium.server.minimalexample;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.impl.MyTestCoapServer;
import com.learn.californium.server.minimalexample.myresc.MyObserverResource_Mwe;
import com.learn.californium.server.mydemo.IMyCoapServer;
import com.learn.californium.server.mydemo.observerdemo.myresc.MyObserverResource;
import com.learn.californium.server.mydemo.observerdemo.myresc.MyObserverResourceTest1;

public class TestObserverMain_Mwe  {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
		// 如果不填参数，则默认端口是5683
		// 这里我尝试自己定义一个端口5656
		//MyTestCoapServer server = new MyTestCoapServer(5683);
		MyTestCoapServer server = new MyTestCoapServer(5656);
		
		// 注意 这里的 hello 大小写是敏感的
		// 因为 client那边 是根据 coap://localhost:5656/hello 来发送请求的
		//MyObserverResource myobResc1 = new MyObserverResource("hello_observer");
		MyObserverResource_Mwe myobResc1 = new MyObserverResource_Mwe("hello_observer");
		myobResc1.setMyCoapServer(server);
		//
		server.add(myobResc1);
		//
		server.start(); // does all the magic
		
	}

	
	


}
