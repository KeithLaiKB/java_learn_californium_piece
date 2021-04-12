package com.learn.californium.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandomSpi;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.UdpMatcher;
import org.eclipse.californium.core.network.stack.AbstractLayer.LogOnlyLayer;
import org.eclipse.californium.core.network.stack.CoapStack;
import org.eclipse.californium.core.network.stack.CoapUdpStack;
import org.eclipse.californium.core.network.stack.ExchangeCleanupLayer;
import org.eclipse.californium.core.network.stack.ReliabilityLayer;
import org.eclipse.californium.elements.exception.ConnectorException;



public class TestMain_Synchronous {

	
	public static void main(String[] args) {
		String port1 = "coap://localhost:5656/hello";
		String port2 = "coap://160.32.219.56:5656/hello";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.178
																// �Ұ�����192.168.50.178:5656 ӳ���160.32.219.56:5656
		String port3 = "coap://160.32.219.56:5657/hello";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.179
																// �Ұ�����192.168.50.179:5656 ӳ���160.32.219.56:5657
		
		CoapClient client2 = new CoapClient(port1);
		//
		CoapResponse resp;
		//
		try {
			// http://www.iana.org/assignments/core-parameters/core-parameters.xhtml#content-formats
			//resp = client2.put("payload", MediaTypeRegistry.TEXT_PLAIN);
			resp = client2.put("payload", MediaTypeRegistry.TEXT_PLAIN);
			System.out.println( resp.isSuccess() );
			System.out.println( resp.getOptions() );
			System.out.println( resp.getResponseText() );

			//client2.useNONs();  // use autocomplete to see more methods
			//client2.delete();
			//client2.useCONs().useEarlyNegotiation(32).get(); // it is a fluent API
			
			CoapEndpoint A;
			CoapUdpStack c;
			CoapStack g;
			SecureRandomSpi t;
			UdpMatcher aa;
			LogOnlyLayer gg;
			ExchangeCleanupLayer a;
			//ObserveLayer
			//SecureRandom tt;
			//org.eclipse.californium.core.network.stack.BlockwiseLayer
			ReliabilityLayer rl;

		} catch (ConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // for response details

		
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
		
		
	}
}