package com.learn.californium.server.minimalexample;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.minimalexample.mysrc.MyResource_Mwe;
import com.learn.californium.server.myresc.MyResource;

/**
 * 
 * MWE means minimal working example
 * Ҳ������� ������
 * 
 * @author laipl
 *
 */
public class TestMain_Simple_Mwe {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// ��������������Ĭ�϶˿���5683
		// �����ҳ����Լ�����һ���˿�5656
		CoapServer server = new CoapServer(5656);
		// ע�� ����� hello ��Сд�����е�
		// ��Ϊ client�Ǳ� �Ǹ��� coap://localhost:5656/hello �����������
		server.add(new MyResource_Mwe("hello"));
		//
		server.start(); // does all the magic

	}

}
