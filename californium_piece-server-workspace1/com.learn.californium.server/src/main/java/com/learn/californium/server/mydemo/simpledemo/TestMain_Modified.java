package com.learn.californium.server.mydemo.simpledemo;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.mydemo.myresc.MyResource;

public class TestMain_Modified {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// ��������������Ĭ�϶˿���5683
		// �����ҳ����Լ�����һ���˿�5656
		CoapServer server = new CoapServer(5656);
		// ע�� ����� hello ��Сд�����е�
		// ��Ϊ client�Ǳ� �Ǹ��� coap://localhost:5656/hello �����������
		server.add(new MyResource("hello"));
		//
		server.start(); // does all the magic

	}

}
