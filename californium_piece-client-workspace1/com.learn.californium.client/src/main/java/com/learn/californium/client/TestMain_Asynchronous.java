package com.learn.californium.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.californium.client.datadto.DtoFruit;

public class TestMain_Asynchronous {

	
	public static void main(String[] args) {
		String port1 = "coap://localhost:5656/hello?my_var1=i_am_var";
		String port2 = "coap://160.32.219.56:5656/hello";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.178
																// �Ұ�����192.168.50.178:5656 ӳ���160.32.219.56:5656
		String port3 = "coap://160.32.219.56:5657/hello";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.179
																// �Ұ�����192.168.50.179:5656 ӳ���160.32.219.56:5657
		
		CoapClient client1 = new CoapClient(port1);
		//
		CoapResponse resp;
		//
		//Request req1 = new Request(null);
		//req1.setToken(token);
		
		//client2.advanced(request)
		
		/*
		client2.get(new CoapHandler() { // e.g., anonymous inner class
	
			@Override public void onLoad(CoapResponse response) { // also error resp.
				System.out.println( "hi:" + response.getResponseText() );
				System.out.println( "hi:" + response.advanced().getTokenString());
			}
			 
			@Override public void onError() { // I/O errors and timeouts
				System.err.println("Failed");
			}
		});
		*/
		
		
		CoapHandler myCoapHandler1 = new CoapHandler() { // e.g., anonymous inner class
			
			@Override public void onLoad(CoapResponse response) { // also error resp.
				System.out.println( "hi:" + response.getResponseText() );
				System.out.println( "hi:" + response.advanced().getTokenString());
			}
			 
			@Override public void onError() { // I/O errors and timeouts
				System.err.println("Failed");
			}
		};
		
		
		
		DtoFruit dtoFruit1=new DtoFruit();
		dtoFruit1.setName("i am apple");
		dtoFruit1.setWeight(23.666);
		//
		ObjectMapper objectMapper = new ObjectMapper();
		String dtoFruit1AsString = new String("");
		try {
			dtoFruit1AsString = objectMapper.writeValueAsString(dtoFruit1);
			//resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_JSON);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//
		//resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_OCTET_STREAM);
		//resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_VND_OMA_LWM2M_JSON);
		//
		
		//client1.get(myCoapHandler1);
		client1.post(myCoapHandler1,dtoFruit1AsString, MediaTypeRegistry.APPLICATION_JSON);
		
		//---------------------------------------------
		// ��Ϊ �첽����Ҫ�ȴ��ش��ģ��ȴ�����Ҫʱ��ģ�
		// ���� �Ҳ����ó�����ô�����
		// ���� ����������س��ٽ�����Ҳ����˵ �㲻����س�����ô�����main����û����
		// �Ӷ� ��ʱ�� ��client�ȵ� �������� ����
		// ��Ȼ�Ļ� �ڵȴ��Ĺ����У��ܺ����Ѿ���������, �����������Щ������ �̰߳� Ҳ�п���û���ˣ�
        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
				br.readLine(); 
		} 
		catch (IOException e) { }
		System.out.println("CANCELLATIONING");
		//resp.proactiveCancel();
		System.out.println("CANCELLATION FINISHED");
		
		client1.shutdown();
        System.exit(0);
	}
}
