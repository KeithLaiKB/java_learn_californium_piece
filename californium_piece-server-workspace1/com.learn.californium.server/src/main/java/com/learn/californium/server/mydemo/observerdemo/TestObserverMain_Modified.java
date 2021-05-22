package com.learn.californium.server.mydemo.observerdemo;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.mydemo.IMyCoapServer;
import com.learn.californium.server.mydemo.impl.MyTestCoapServer;
import com.learn.californium.server.mydemo.observerdemo.myresc.MyObserverResource;
import com.learn.californium.server.mydemo.observerdemo.myresc.MyObserverResource_Modified;
import com.learn.californium.server.mydemo.observerdemo.myresc.useless.MyObserverResource_Modified_Cp1;
import com.learn.californium.server.mydemo.observerdemo.myresc.useless.MyObserverResource_Modified_Cp2;
import com.learn.californium.server.mydemo.observerdemo.myresc.useless.MyObserverResource_Modified_Cp3;

public class TestObserverMain_Modified  {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("fkkkk");
		// 如果不填参数，则默认端口是5683
		// 这里我尝试自己定义一个端口5656
		
		// 注意 这里的 hello 大小写是敏感的
		// 因为 client那边 是根据 coap://localhost:5656/hello 来发送请求的
		//MyObserverResource myobResc1 = new MyObserverResource("hello_observer");
		MyTestCoapServer server = new MyTestCoapServer(5656);
		MyObserverResource_Modified myobResc1 = new MyObserverResource_Modified("hello_observer");
		MyObserverResource_Modified_Cp1 myobResc1_c1 = new MyObserverResource_Modified_Cp1("hello_observer_child1");
		MyObserverResource_Modified_Cp2 myobResc1_c2 = new MyObserverResource_Modified_Cp2("hello_observer_child2");
		MyObserverResource_Modified_Cp3 myobResc1_c3 = new MyObserverResource_Modified_Cp3("hello_observer_child3");
		//
		myobResc1_c2.add(myobResc1_c3);
		myobResc1_c1.add(myobResc1_c2);
		myobResc1.add(myobResc1_c1);
		
		myobResc1.setMyCoapServer(server);
		myobResc1_c1.setMyCoapServer(server);
		myobResc1_c2.setMyCoapServer(server);
		myobResc1_c3.setMyCoapServer(server);
		
		server.add(myobResc1);

		server.start(); // does all the magic
		
	}

	
	


}
