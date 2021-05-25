package com.learn.californium.mytest1.unittest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.learn.californium.server.minimalexample.datadto.DtoFruit;
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
class UT_Asynchronous_Synchronous {
	
	String port1 = "coap://localhost:5656/hello";
	String port2 = "coap://160.32.219.56:5656/hello";		//有线连接树莓派, 路由给的地址是192.168.50.178
															// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello";		//无线连接树莓派, 路由给的地址是192.168.50.179
															// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
	static CoapServer server1 = null;
	static CoapClient client1 = null;
	//---------------- data field ----------------
	String str_post_content="hi_i_am_string";
	// set data vo to test
	DtoFruit dtoFruit1= null;
	static ObjectMapper objectMapper = null;
	static String dtoFruit1AsString =null;
	
	
	
	//----------------------------------------------------------
	//
	UT_Asynchronous_Synchronous(){
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
		// -----------prepare server-----------------------
		System.out.println("---------------------------------------------------------");
		System.out.println("starting server");
		//
		server1 = new CoapServer(5656);		// define port to be 5656 
		server1.add(new Con_MyResource_Mwe("hello"));		// name "hello" is letter sensitive
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
		server1.destroy();
	}
	
	@Test
	void testTextPlain() {
		CoapResponse resp = null;
		try {
			resp = client1.post(str_post_content, MediaTypeRegistry.TEXT_PLAIN);
		} catch (ConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("result from server:" + resp.isSuccess() );
		System.out.println("result option from server:" + resp.getOptions() );
		System.out.println("result text from server:" + resp.getResponseText() );
		assertEquals(true,resp.isSuccess(),"if_success");
		//fail("Not yet implemented");
	}
	
	
	@Test
	void testJson() {
		CoapResponse resp = null;
		try {
			resp = client1.post(dtoFruit1AsString, MediaTypeRegistry.APPLICATION_JSON);
		} catch (ConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("result from server:" + resp.isSuccess() );
		System.out.println("result option from server:" + resp.getOptions() );
		System.out.println("result text from server:" + resp.getResponseText() );
		assertEquals(true,resp.isSuccess(),"if_success");
		//fail("Not yet implemented");
	}

}
