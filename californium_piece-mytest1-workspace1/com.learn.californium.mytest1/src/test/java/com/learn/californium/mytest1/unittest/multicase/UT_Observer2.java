package com.learn.californium.mytest1.unittest.multicase;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.californium.mytest1.MyThreadSleep;
import com.learn.californium.server.minimalexample.datadto.DtoFruit;
import com.learn.californium.server.minimalexample.myresc.MyObserverResource_Con_Mwe;
import com.learn.californium.server.minimalexample.myresc.concise.Con_MyObserverResource_Con_Mwe;
import com.learn.californium.server.minimalexample.myresc.concise.Con_MyResource_Mwe;





/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						before using this junit, please maven install other projects, then update this project	</br>
 * &emsp;&emsp;						debug ->maven clean-> maven install	(other projects)				</br>
 * &emsp;&emsp;						maven ->update project	(this projects)								</br>
 * 																										</br>
 * 	
 *
 *
 * @author laipl
 *
 */
class UT_Observer2 {
	
	String port1 = "coap://localhost:5656/hello_observer";
	String port2 = "coap://160.32.219.56:5656/hello_observer";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.178
																	// �Ұ�����192.168.50.178:5656 ӳ���160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello_observer";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.179
																	// �Ұ�����192.168.50.179:5656 ӳ���160.32.219.56:5657
	static CoapServer server1 = null;
	static CoapClient client1 = null;
	//static CoapHandler myclientHandler1 = null;
	MyObserverResource_Con_Mwe myobResc1=null;
	//
	//---------------- data field ----------------
	String str_post_content="hi_i_am_string";
	// set data vo to test
	DtoFruit dtoFruit1= null;
	static ObjectMapper objectMapper = null;
	static String dtoFruit1AsString =null;
	//--------------------------------------------
	static CoapResponse resultFromServer1=null;
	
	
	CoapObserveRelation coapObRelation1 = null;
	//----------------------------------------------------------
	//
	UT_Observer2(){
		System.out.println("constructor");
	}
	
	
	static void datapreparation() {
		// set data vo to test
		DtoFruit dtoFruit1 = new DtoFruit();
		dtoFruit1.setName("i am apple");
		dtoFruit1.setWeight(23.666);
		//
		//
		// transform the vo into json
		objectMapper = new ObjectMapper();
		dtoFruit1AsString = new String("");
		//
		try {
			dtoFruit1AsString = objectMapper.writeValueAsString(dtoFruit1);
			// resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_JSON);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//
		//
	}
	//
	//
	//----------------------------------------------------------
	//---------------------start test---------------------------
	

	@BeforeAll
	static void preparation() {
		datapreparation();
	}

	
	@BeforeEach
	void beforesomething() {
		System.out.println("---------------------------------------------------------");
		//
		// -----------configure server-----------------------
		// new server
		server1 = new CoapServer(5656);										// define port to be 5656 
		//
		// add resource
		myobResc1 = new MyObserverResource_Con_Mwe("hello_observer");	// name "hello" is letter sensitive
		server1.add(myobResc1);	
		//
		// -----------start server-----------------------
		System.out.println("starting server");
		server1.start();
		System.out.println("started server");
		// -----------prepare client-----------------------
		//
		// new client
		client1 = new CoapClient(port1);
		
		//

	}
	
	
	@AfterEach
	void aftersomething() {
		// --------------------------- client side ----------------------------------
		// ����� ����Ϊ��΢��׼�ķ�ʽ, ������test��Ĳ��� �Ĺر����� ��Ϊ�˿������ϸ�ڶ���
		// ���µĹ��� ����Ϊ�����һ��
		//
		// client side ȡ������
        // 
        //
        // ע��
        // �������reactiveCancel ���, ��һ��ʱ���� ������ӳ������
        // ��Ϊ reactiveCancel �ǵȴ� ��һ��notification������ʱ��, �ٷ���RST ��server���ٷ�����Ϣ����
        // �������һ�� notification ����ʱ, ����ӳ���ȴ �Ѿ���������, ��ôҲ����˵
        // ��� �ӳ����  �ڲ����� coapObRelation1 ������ ����RST ȥserver ȡ��������
		//
		// �Ҹ��˶���, ��Ȼ���ﲻ�����ڲ�����, 
		// ������ϣ������ �ȴ�һ��ʱ��, �Ӷ��ܹ��� client2 �ȴ� ��һ����Ϣ���� ��������RST ��server, �Ӷ��ﵽȡ�����ĵ�Ŀ��
		coapObRelation1.reactiveCancel();		//client side ����Ҫ�ֶ��ص� �Լ� observe �Ǹ� server�� relation, 	
		MyThreadSleep.sleep10s();
		//
		// ���ʹ�� proactiveCancel ����Ҫ�ڵȴ���
		//coapObRelation1.proactiveCancel();
		System.out.println("client1 canceled subscribe");
		// client side �ر�
		client1.shutdown();
		System.out.println("client1 shutdown");
		//
		// --------------------------- server side ----------------------------------
		// ���sleep��Ϊ�˿� �����Ƿ���ȡ��, ��Ȼû�����Ҳ����
		MyThreadSleep.sleep20s();
		//
		// server side �ر���Դ
		myobResc1.stopMyResource();
		System.out.println("server stopped resource");
		// server side �ر�
		server1.destroy();
		System.out.println("server destroyed");

		MyThreadSleep.sleep20s();
	}
	
	@Test
	void testObserve() {
		//
		// set handler
		CoapHandler myObserveHandler1 = new CoapHandler() { // e.g., anonymous inner class

			@Override
			public void onLoad(CoapResponse response) { // also error resp.
				System.out.println("---------------------------------------------------");
				System.out.println("--------- client side onload start ----------------");
				resultFromServer1 = response;
				System.out.println("result from server:" + response.isSuccess() );
				System.out.println("on load: " + response.getResponseText());
				System.out.println("response code name: " + response.getCode().name());
				System.out.println("---------- client side onload end -----------------");
				System.out.println("---------------------------------------------------");
			}

			@Override
			public void onError() { // I/O errors and timeouts
				System.err.println("Failed");
			}
		};
		//----------------------------------------
		System.out.println("+++++ client1 start to observe +++++");
		coapObRelation1 = client1.observe(myObserveHandler1);
		System.out.println("++++++++ client1 observing ++++++++");
		//----------------------------------------
		//
		// method 1 to wait notification from server
		/*
		// ������sleep ������Ӱ�� handler�Ǹ��̵߳�����
		// �����Ҳ��, ֻ������ ��Щ����Ϥ���� ��֪��
		try {
			System.out.println("go to sleep");
			Thread.sleep(30*1000);
			System.out.println("wake up");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//
		//
		// method 2 to wait notification from server
		// 1s  =         1,000 ms
		// 1ms =     1,000,000 ns
		// 1s  = 1,000,000,000 ns
		long startObserveTime=System.nanoTime();   //��ȡ��ʼʱ��  
		//
		//
		boolean judge_timeout = false;
		while (judge_timeout==false) {
			long nowTime_tmp=System.nanoTime();
			long timelimit_tmp=20*1000000000L;
			//System.out.println("h"+nowTime_tmp);
			//System.out.println("hh2:"+(nowTime_tmp-startObserveTime));
			//System.out.println("hh3:"+(nowTime_tmp-startObserveTime-timelimit_tmp));
			if(nowTime_tmp-startObserveTime>timelimit_tmp) {
				judge_timeout=true;
			}
		}
        //
        assertEquals(true,resultFromServer1.isSuccess(),"if_success");
	}
	
	
	
	
	
}
