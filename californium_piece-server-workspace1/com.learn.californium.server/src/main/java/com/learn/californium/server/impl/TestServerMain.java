package com.learn.californium.server.impl;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.IMyCoapServer;
import com.learn.californium.server.myresc.MyObserverResource;

public class TestServerMain  {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("fkkkk");
		// ��������������Ĭ�϶˿���5683
		// �����ҳ����Լ�����һ���˿�5656
		MyCoapServer server = new MyCoapServer(5656);
		// ע�� ����� hello ��Сд�����е�
		// ��Ϊ client�Ǳ� �Ǹ��� coap://localhost:5656/hello �����������
		MyObserverResource myobResc1 = new MyObserverResource("hello_observer");
		myobResc1.setMyCoapServer(server);
		
		server.add(myobResc1);

		server.start(); // does all the magic
		
	}

	
	


}
