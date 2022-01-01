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
/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						Asynchronous request																</br>
 * &emsp;&emsp;							it needs myCoapHandler1														</br>
 * &emsp;						try post with data																	</br>
 * &emsp;&emsp;							1.transform the data view object into json									</br>
 * &emsp;&emsp;							2.set the json as a parameter during sending the request					</br>
 * 																													</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						californium/api-demo/src/org/eclipse/californium/examples/CoAPObserveExample.java  	</br>
 * &emsp;						californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java	
 *
 *
 * @author laipl
 *
 */
public class TestMain_Asynchronous {

	
	public static void main(String[] args) {
		//
		// -------------------------preparasion-------------------------------------
		String port1 = "coap://localhost:5656/hello?my_var1=i_am_var";
		String port2 = "coap://135.0.237.84:5656/hello";		//有线连接树莓派, 路由给的地址是192.168.50.178
																// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
		String port3 = "coap://160.32.219.56:5657/hello";		//无线连接树莓派, 路由给的地址是192.168.50.179
																// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
		//
		//
		// set data vo to test
		DtoFruit dtoFruit1=new DtoFruit();
		dtoFruit1.setName("i am apple");
		dtoFruit1.setWeight(23.666);
		//
		// transform the vo into json
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
		//
		// -------------------------start-------------------------------------
		// new client
		CoapClient client1 = new CoapClient(port2);
		//
		//
		// set handler
		CoapHandler myCoapHandler1 = new CoapHandler() { // e.g., anonymous inner class
			
			@Override public void onLoad(CoapResponse response) { // also error resp.
				System.out.println( "hi:" + response.getResponseText() );
				System.out.println( "hi:" + response.advanced().getTokenString());
			}
			 
			@Override public void onError() { // I/O errors and timeouts
				System.err.println("Failed");
			}
		};
		//
		//
		// action
		//
		//client1.get(myCoapHandler1);
		try {
			CoapResponse resp1= client1.put(dtoFruit1AsString,  MediaTypeRegistry.APPLICATION_JSON);
			System.out.println(resp1.getResponseText());
		} catch (ConnectorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//client1.post(myCoapHandler1,dtoFruit1AsString, MediaTypeRegistry.APPLICATION_JSON);
		//resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_OCTET_STREAM);
		//resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_VND_OMA_LWM2M_JSON);
		//
		//
		//
		//---------------------------------------------
		// 因为 异步，是要等待回传的，等待是需要时间的，
		// 所以 我不能让程序那么快结束
		// 所以 我让你输入回车再结束，也就是说 你不输入回车，那么这个总main函数没走完
		// 从而 有时间 让client等到 传回来的 数据
		// 不然的话 在等待的过程中，总函数已经运行完了, 所以里面的这些变量啊 线程啊 也有可能没有了？
        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
				br.readLine(); 
		} 
		catch (IOException e) { }
		System.out.println("CANCELLATIONING");
		//resp.proactiveCancel();
		System.out.println("CANCELLATION FINISHED");
		//---------------------------------------------
		//
		client1.shutdown();
        System.exit(0);
	}
}