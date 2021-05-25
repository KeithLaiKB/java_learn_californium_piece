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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.californium.client.datadto.DtoFruit;


/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						synchronous request																	</br>
 * &emsp;&emsp;							it needn't myCoapHandler1													</br>
 * &emsp;						try post with data																	</br>
 * &emsp;&emsp;							1.transform the data view object into json									</br>
 * &emsp;&emsp;							2.set the json as a parameter during sending the request					</br>
 * 																													</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						californium/api-demo/src/org/eclipse/californium/examples/CoAPObserveExample.java  	</br>
 * &emsp;						californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java  	</br>	
 *
 *
 * @author laipl
 *
 */
public class TestMain_Synchronous {

	
	public static void main(String[] args) {
		//
		// -------------------------preparasion-------------------------------------
		String port1 = "coap://localhost:5656/hello";
		String port2 = "coap://160.32.219.56:5656/hello";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.178
																// �Ұ�����192.168.50.178:5656 ӳ���160.32.219.56:5656
		String port3 = "coap://160.32.219.56:5657/hello";		//����������ݮ��, ·�ɸ��ĵ�ַ��192.168.50.179
																// �Ұ�����192.168.50.179:5656 ӳ���160.32.219.56:5657
		//
		//
		// set data vo to test
		DtoFruit dtoFruit1=new DtoFruit();
		dtoFruit1.setName("i am apple");
		dtoFruit1.setWeight(23.666);
		//
		//
		// transform the vo into json
		ObjectMapper objectMapper = new ObjectMapper();
		String dtoFruit1AsString = new String("");
		//
		try {
			dtoFruit1AsString = objectMapper.writeValueAsString(dtoFruit1);
			//resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_JSON);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//
		//
		// -------------------------start-------------------------------------
		// new client
		CoapClient client1 = new CoapClient(port1);
		//
		CoapResponse resp;
		//
		//
		//
		try {
			// http://www.iana.org/assignments/core-parameters/core-parameters.xhtml#content-formats
			//resp = client1.put("payload", MediaTypeRegistry.TEXT_PLAIN);
			//
			//resp = client1.post("i am payload", MediaTypeRegistry.TEXT_PLAIN);
			//
			resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_JSON);
			//resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_OCTET_STREAM);
			//resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_VND_OMA_LWM2M_JSON);
			//
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
		//
		//
		client1.shutdown();
        System.exit(0);
	}
}
