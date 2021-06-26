package com.learn.californium.server.minimalexample;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.minimalexample.myresc.MyObserverResource_Con_Mwe;


/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE ��˼����  �򻯵�����														</br>
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
		// ��������������Ĭ�϶˿���5683
		// �����ҳ����Լ�����һ���˿�5656
		//MyTestCoapServer server = new MyTestCoapServer(5683);
		//MyTestCoapServer server = new MyTestCoapServer(5656);
		CoapServer server = new CoapServer(5656);
		//
		//
		//
		//------------------------resource settings-------------------------------------
		// ע�� ����� hello ��Сд�����е�
		// ��Ϊ client�Ǳ� �Ǹ��� coap://localhost:5656/hello �����������
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
		
		System.out.println("try to destroy server!!!!!!!!!!!!!!!!!!!!!!");
		
		
		
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println("destroying server!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		server.destroy(); // does all the magic
		// ����, �ҷ��ֵ���destroy�� ����ֹͣ resource��ļ�ʱ����
		// �����ҽ�����������Ҫ ���� �ǲ���Ӧ��destroy ��ʱ��Ѽ�ʱ��Ҳ�ص�
		// californium/californium-tests/californium-integration-tests/src/test/java/org/eclipse/californium/integration/test/SecureObserveTest.java 
		// ���ǹٷ��ĵ��Ļ�, ������Main �ø�forѭ�� ������ֵ, ���һ��changed(String)����  ,   Ȼ���ֶ� �� resouce ȥchange
		/*
		for (int i = 0; i < REPEATS; ++i) {
			resource.changed("client");
			Thread.sleep(50);
		}
		
		��ʵ�����������ô����, ����main ����ȥ����, 
		�൱�ڰ� ���resource���ҵ���߼� 	
				��resource ���Ƶ��� server
		 * 
		 */
		myobResc1.stopMyResource();
		myobResc1_c1.stopMyResource();
		myobResc1_c2.stopMyResource();
		myobResc1_c3.stopMyResource();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//myobResc1_c1 = null;
		System.out.println("finished!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

	}

	
	


}
