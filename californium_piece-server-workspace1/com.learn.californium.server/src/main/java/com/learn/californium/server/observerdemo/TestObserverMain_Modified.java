package com.learn.californium.server.observerdemo;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.IMyCoapServer;
import com.learn.californium.server.impl.MyTestCoapServer;
import com.learn.californium.server.observerdemo.myresc.MyObserverResource;
import com.learn.californium.server.observerdemo.myresc.MyObserverResourceTest1;

public class TestObserverMain_Modified  {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("fkkkk");
		// ��������������Ĭ�϶˿���5683
		// �����ҳ����Լ�����һ���˿�5656
		MyTestCoapServer server = new MyTestCoapServer(5656);
		// ע�� ����� hello ��Сд�����е�
		// ��Ϊ client�Ǳ� �Ǹ��� coap://localhost:5656/hello �����������
		//MyObserverResource myobResc1 = new MyObserverResource("hello_observer");
		MyObserverResourceTest1 myobResc1 = new MyObserverResourceTest1("hello_observer");
		myobResc1.setMyCoapServer(server);
		
		server.add(myobResc1);

		server.start(); // does all the magic
		
	}

	
	


}