package com.learn.calf.servertest.learn.test;


import org.eclipse.californium.core.CoapServer;



public class TestMain_Simple {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// ��������������Ĭ�϶˿���5683
		// �����ҳ����Լ�����һ���˿�5656
		CoapServer server = new CoapServer();
		// ע�� ����� hello ��Сд�����е�
		// ��Ϊ client�Ǳ� �Ǹ��� coap://localhost:5656/hello �����������
		server.add(new MyResource("hello"));

		server.start(); // does all the magic

	}

}
