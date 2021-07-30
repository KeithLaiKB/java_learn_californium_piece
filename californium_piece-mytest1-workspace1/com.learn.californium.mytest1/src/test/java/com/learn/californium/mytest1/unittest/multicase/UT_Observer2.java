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
	String port2 = "coap://160.32.219.56:5656/hello_observer";		//有线连接树莓派, 路由给的地址是192.168.50.178
																	// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello_observer";		//无线连接树莓派, 路由给的地址是192.168.50.179
																	// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
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
		// 这个是 我认为稍微标准的方式, 其他的test里的测试 的关闭流程 是为了看更多的细节而已
		// 以下的过程 我认为会更好一点
		//
		// client side 取消订阅
        // 
        //
        // 注意
        // 如果你用reactiveCancel 最好, 等一段时间再 让这个子程序结束
        // 因为 reactiveCancel 是等待 下一次notification过来的时候, 再发送RST 让server不再发送消息过来
        // 如果当下一次 notification 过来时, 这个子程序却 已经运行完了, 那么也就是说
        // 这个 子程序的  内部变量 coapObRelation1 来不及 发送RST 去server 取消订阅了
		//
		// 我个人而言, 虽然这里不算是内部变量, 
		// 但还是希望他能 等待一段时间, 从而能够让 client2 等待 下一次信息过来 进而发送RST 给server, 从而达到取消订阅的目的
		coapObRelation1.reactiveCancel();		//client side 还需要手动关掉 自己 observe 那个 server的 relation, 	
		MyThreadSleep.sleep10s();
		//
		// 如果使用 proactiveCancel 则不需要在等待了
		//coapObRelation1.proactiveCancel();
		System.out.println("client1 canceled subscribe");
		// client side 关闭
		client1.shutdown();
		System.out.println("client1 shutdown");
		//
		// --------------------------- server side ----------------------------------
		// 这个sleep是为了看 订阅是否有取消, 当然没了这句也可以
		MyThreadSleep.sleep20s();
		//
		// server side 关闭资源
		myobResc1.stopMyResource();
		System.out.println("server stopped resource");
		// server side 关闭
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
		// 这里用sleep 并不会影响 handler那个线程的运行
		// 用这个也行, 只不过怕 有些不熟悉的人 不知道
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
		long startObserveTime=System.nanoTime();   //获取开始时间  
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
