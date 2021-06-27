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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * &emsp;						hello_observer	</br>
 * &emsp;&emsp;						hello_observer_child1												</br>
 * &emsp;&emsp;&emsp;					hello_observer_child2											</br>
 * &emsp;&emsp;&emsp;&emsp;					hello_observer_child3										</br>
 * 																										</br>
 *
 * @author laipl
 *
 */
class UT_Observer_toOperateDelete_asyn {
	
	
	String port2 = "coap://160.32.219.56:5656/hello_observer";		//有线连接树莓派, 路由给的地址是192.168.50.178
																	// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
	String port3 = "coap://160.32.219.56:5657/hello_observer";		//无线连接树莓派, 路由给的地址是192.168.50.179
																	// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
	
	String myuri1 		 = "coap://localhost:5656/hello_observer";
	String myuri1_c1 	 = "coap://localhost:5656/hello_observer/hello_observer_child1";
	String myuri1_c2 	 = "coap://localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2";
	String myuri1_c3	 = "coap://localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2/hello_observer_child3";
	
	
	static CoapServer server1 = null;
	static CoapClient client1 = null;
	//static CoapHandler myclientHandler1 = null;
	//Con_MyObserverResource_Con_Mwe myobResc1=null;
	//
	//---------------- data field ----------------
	String str_post_content="hi_i_am_string";
	// set data vo to test
	DtoFruit dtoFruit1= null;
	static ObjectMapper objectMapper = null;
	static String dtoFruit1AsString =null;
	//--------------------------------------------
	static CoapResponse resultFromServer1 = null;
	static CoapResponse resultFromServer2 = null;
	
	CoapObserveRelation coapObRelation1 =null;
	//
	MyObserverResource_Con_Mwe myobResc1 	= null;
	//
	MyObserverResource_Con_Mwe myobResc1_c1 = null;
	MyObserverResource_Con_Mwe myobResc1_c2 = null;
	MyObserverResource_Con_Mwe myobResc1_c3 = null;
	
	CoapResponse del_respone_tmp = null;
	//----------------------------------------------------------
	//
	//
	final Logger LOGGER = LoggerFactory.getLogger(UT_Observer_toOperateDelete_asyn.class);
	
	
	//----------------------------------------------------------
	//
	UT_Observer_toOperateDelete_asyn(){
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
		resultFromServer1 = null;
		resultFromServer2 = null;
		//
		del_respone_tmp = null;
		//
		// -----------configure server-----------------------
		// new server
		server1 = new CoapServer(5656);										// define port to be 5656 
		//
		// add resource
		myobResc1 	= new MyObserverResource_Con_Mwe("hello_observer");	// name "hello" is letter sensitive
		//
		myobResc1_c1 = new MyObserverResource_Con_Mwe("hello_observer_child1");
		myobResc1_c2 = new MyObserverResource_Con_Mwe("hello_observer_child2");
		 myobResc1_c3 = new MyObserverResource_Con_Mwe("hello_observer_child3");
		//
		myobResc1_c2.add(myobResc1_c3);
		myobResc1_c1.add(myobResc1_c2);
		myobResc1.add(myobResc1_c1);
		//
		server1.add(myobResc1);	
		//
		// -----------start server-----------------------
		System.out.println("starting server");
		server1.start();
		System.out.println("started server");
		//
		//----------------------------------------------------
		//--------------------- client1 ----------------------
		//
		// new client
		client1 = new CoapClient(myuri1_c1);
		//
		//
		// set observe handler
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
		
		// ----------- client1 observe -----------
		System.out.println("+++++ client1 start to observe +++++");
		coapObRelation1 = client1.observe(myObserveHandler1);
		System.out.println("++++++++ client1 observing ++++++++");
		//----------------------------------------

	}
	
	
	@AfterEach
	void aftersomething() {
		// 注意 我下面这个关闭流程 并不是特别标准, 但是 很能体现细节问题
		//
		//
		// ----------------------- server side 进行 destroy -----------------------------
		// 在我看来 如果实在demo 里面 其实 是不用这个 destroy方法
		// 因为 作为一个observe的关系
		// 
		server1.destroy();						//destory 只是 server side 释放了端口
		//
		// ----------------------- client side 取消 subscribe --------------------------
		//
        // 
        // 必须要取消observe， 不然它会影响后面的testcase, 
        // 当然你用proactiveCancel也行
        //
        // 注意
        // 如果你用reactiveCancel 最好, 等一段时间再shutdown
        // 因为 reactiveCancel 是等待 下一次过来的时候, 在发送RST 让server不再发送消息过来
        // 如果不等待的话, 直接shutdown, 会导致 对于server来说 还有这个 observe relation, 
        //
        // 如果 你不信 你可以试试删掉 sleep, 你会发现 除了 update task 之外 还有 handle get的输出, 然而此时 你的client1已经没了
		coapObRelation1.reactiveCancel();		//client side 还需要手动关掉 自己 observe 那个 server的 relation, 							
		LOGGER.info("###############################################server1.destroy");
		//
		// ----------------------- 利用 sleep main function 来发现问题  ---------------------
		//
		// server side 问题, 我发现单纯destroy是 不会停止 resource里的计时器的！！！！！！！！！！！
		MyThreadSleep.sleep10s();
		//
		// --------------------------------- 解决问题, 所以去停止 resource 的 timer ----------
		//
		// 所以这里还需要关掉 resource的timer
		myobResc1.stopMyResource();
		myobResc1_c1.stopMyResource();
		myobResc1_c2.stopMyResource();
		myobResc1_c3.stopMyResource();
		MyThreadSleep.sleep10s();
		LOGGER.info("###############################################server1.destroyed");
		//
		// ------------------- client side 取消完subscribe 还要 sutdown -------------------
		//
		client1.shutdown();
		LOGGER.info("###############################################client1.shutdown finishied");
		//
		// ---------------- 利用 sleep main function 尝试来发现问题 , 然后 发现没有问题---------------
		//
		MyThreadSleep.sleep10s();
		LOGGER.info("###############################################server and client close checked finished");
		//
	}
	
	/**
	 * 
	 * 
	 * <p>
	 * 							description:																</br>	
	 * &emsp;						hello_observer															</br>
	 * &emsp;&emsp;						hello_observer_child1(client1 to delete, then client2 to observe)	</br>
	 * &emsp;&emsp;&emsp;					hello_observer_child2											</br>
	 * &emsp;&emsp;&emsp;&emsp;					hello_observer_child3										</br>
	 * 																										</br>
	 *
	 * @author laipl
	 *
	 */
	
	@Test
	void testDelete_syn_then_observe_sameresc() {
		System.out.println("--------------------- testDelete_syn_then_observe_sameresc ----------------------------");
		//
		// sleep main function avoid ending the program 
		// to let the handler thread to get more notifications from server
		MyThreadSleep.sleep20s();				
		//
		//----------------- client1 deletes server resource ----------------------
		//
		// set delete handler
		CoapHandler myDeleteHandler = new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
            	System.out.println("---------------------------------------------------");
            	System.out.println("-------- delete handler onload start --------------");
            	System.out.println("result from server:" + response.isSuccess() );
				//
            	System.out.println("on load: " + response.getResponseText());
            	System.out.println("get code: " + response.getCode().name());
            	System.out.println("---------- delete handler onload end --------------");
            	System.out.println("---------------------------------------------------");
            	//
            	del_respone_tmp = response;
            }

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
		};
		
		//del_reponse = client1.delete();
		client1.delete(myDeleteHandler);
		//------------------------------------------------------------------------
		//
		// sleep main function for getting the last notification due to concurrency
		MyThreadSleep.sleep10s();
        //
		//
        assertEquals(false,resultFromServer1.isSuccess(),"if_success_client1");
        assertEquals("DELETED",del_respone_tmp.getCode().name(),"test_deleted_client1");
        assertEquals("NOT_FOUND",resultFromServer1.getCode().name(),"test_notfound_client1");
		//
		//------------------------------------------------------------------------
		//--------------------------- client2 ------------------------------------
		// new client
		CoapClient client2 = null;
		client2 = new CoapClient(myuri1_c1);
		//
		//
		// set handler
		CoapHandler myObserveHandler2 = new CoapHandler() { // e.g., anonymous inner class

			@Override
			public void onLoad(CoapResponse response) { // also error resp.
				System.out.println("---------------------------------------------------");
				System.out.println("--------- client side onload start ----------------");
				resultFromServer2 = response;
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
		CoapObserveRelation coapObRelation2 = client2.observe(myObserveHandler2);
		//
		//----------------------------------------
		//
		// sleep main function avoid ending the program 
		// to let the handler thread to get more notifications from server
		MyThreadSleep.sleep20s();				
		//
		assertEquals("NOT_FOUND",resultFromServer2.getCode().name(),"test_notfound_client2");
		//
		// if children1 is deleted, sub resource could not be observed
        assertEquals(false,resultFromServer2.isSuccess(),"if_success_client2");
        // --------------------------------------------------------------------
        // 必须要取消observe， 不然它会影响后面的testcase, 
        // 当然你用proactiveCancel也行
        //
        // 注意
        // 如果你用reactiveCancel 最好, 等一段时间再 让这个子程序结束
        // 因为 reactiveCancel 是等待 下一次notification过来的时候, 再发送RST 让server不再发送消息过来
        // 如果当下一次 notification 过来时, 这个子程序却 已经运行完了, 那么也就是说
        // 这个 子程序的  内部变量 coapObRelation2 来不及 发送RST 去server 取消订阅了
        //
        //
        coapObRelation2.reactiveCancel();
        //MyThreadSleep.sleep10s();
        //
        // shutdown need sometime to wait
        client2.shutdown();
        // 如果 你不信 你可以试试删掉 sleep, 你会发现 除了 update task 之外 还有 handle get的输出, 然而此时 你的client2已经没了
        // 在我看来 有点奇怪的是, 我明明 shutdown了 client2 可是在这里写sleep 也能避免RST无法发送的问题
        // 我猜测发送消息 可能是跟coapObRelation2有关, 
        // 因为他发送过去以后 不需要 server再发送消息过来了
        // 
        // 总而言之, 如果想要reactiveCancel 以后 还打算shutdown, 记得要稍微等待一段时间(最好超过 服务器每次发送消息的间隔)
        // 从而能够让 client2 等待 下一次信息过来 进而发送RST 给server, 从而达到取消订阅的目的
        MyThreadSleep.sleep10s();
        LOGGER.info("###############################################end");


	}
	
	
	/**
	 * 
	 * 
	 * <p>
	 * 							description:																</br>	
	 * &emsp;						hello_observer															</br>
	 * &emsp;&emsp;						hello_observer_child1			(client1 to delete)					</br>
	 * &emsp;&emsp;&emsp;					hello_observer_child2		(then client2 to observe)			</br>
	 * &emsp;&emsp;&emsp;&emsp;					hello_observer_child3										</br>
	 * 																										</br>
	 *
	 * @author laipl
	 *
	 */

	@Test
	void testDelete_syn_then_observe_subresc() {
		System.out.println("--------------------- testDelete_syn_then_observe_subresc ----------------------------");
		//
		// sleep main function avoid ending the program 
		// to let the handler thread to get more notifications from server
		MyThreadSleep.sleep20s();				
		//
		//----------------- client1 deletes server resource ----------------------
		// set delete handler
		CoapHandler myDeleteHandler = new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
            	System.out.println("---------------------------------------------------");
            	System.out.println("-------- delete handler onload start --------------");
            	System.out.println("result from server:" + response.isSuccess() );
				//
            	System.out.println("on load: " + response.getResponseText());
            	System.out.println("get code: " + response.getCode().name());
            	System.out.println("---------- delete handler onload end --------------");
            	System.out.println("---------------------------------------------------");
            	//
            	del_respone_tmp = response;
            }

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
		};
		
		//del_reponse = client1.delete();
		client1.delete(myDeleteHandler);
		//------------------------------------------------------------------------
		//
		// sleep main function for getting the last notification due to concurrency
		MyThreadSleep.sleep10s();
        //
		//
        assertEquals(false,resultFromServer1.isSuccess(),"if_success_client1");
        assertEquals("DELETED",del_respone_tmp.getCode().name(),"test_deleted_client1");
        assertEquals("NOT_FOUND",resultFromServer1.getCode().name(),"test_notfound_client1");
		//
		//------------------------------------------------------------------------
		//--------------------------- client2 ------------------------------------
		// new client
		CoapClient client2 = null;
		client2 = new CoapClient(myuri1_c2);
		//
		//
		// set handler
		CoapHandler myObserveHandler2 = new CoapHandler() { // e.g., anonymous inner class

			@Override
			public void onLoad(CoapResponse response) { // also error resp.
				System.out.println("---------------------------------------------------");
				System.out.println("--------- client side onload start ----------------");
				resultFromServer2 = response;
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
		CoapObserveRelation coapObRelation2 = client2.observe(myObserveHandler2);
		//
		//----------------------------------------
		//
		// sleep main function avoid ending the program 
		// to let the handler thread to get more notifications from server
		MyThreadSleep.sleep20s();				
		//
		assertEquals("NOT_FOUND",resultFromServer2.getCode().name(),"test_notfound_client2");
		//
		// if children1 is deleted, sub resource could not be observed
        assertEquals(false,resultFromServer2.isSuccess(),"if_success_client2");
        // --------------------------------------------------------------------
        // 必须要取消observe， 不然它会影响后面的testcase, 
        // 当然你用proactiveCancel也行
        //
        // 注意
        // 如果你用reactiveCancel 最好, 等一段时间再 让这个子程序结束
        // 因为 reactiveCancel 是等待 下一次notification过来的时候, 再发送RST 让server不再发送消息过来
        // 如果当下一次 notification 过来时, 这个子程序却 已经运行完了, 那么也就是说
        // 这个 子程序的  内部变量 coapObRelation2 来不及 发送RST 去server 取消订阅了
        //
        //
        coapObRelation2.reactiveCancel();
        //MyThreadSleep.sleep10s();
        //
        // shutdown need sometime to wait
        client2.shutdown();
        // 如果 你不信 你可以试试删掉 sleep, 你会发现 除了 update task 之外 还有 handle get的输出, 然而此时 你的client2已经没了
        // 在我看来 有点奇怪的是, 我明明 shutdown了 client2 可是在这里写sleep 也能避免RST无法发送的问题
        // 我猜测发送消息 可能是跟coapObRelation2有关, 
        // 因为他发送过去以后 不需要 server再发送消息过来了
        // 
        // 总而言之, 如果想要reactiveCancel 以后 还打算shutdown, 记得要稍微等待一段时间(最好超过 服务器每次发送消息的间隔)
        // 从而能够让 client2 等待 下一次信息过来 进而发送RST 给server, 从而达到取消订阅的目的
        MyThreadSleep.sleep10s();
        LOGGER.info("###############################################end");
	}

	
	/**
	 * 
	 * 
	 * <p>
	 * 							description:																</br>	
	 * &emsp;						hello_observer							(then client2 to observe)		</br>
	 * &emsp;&emsp;						hello_observer_child1			(client1 to delete)					</br>
	 * &emsp;&emsp;&emsp;					hello_observer_child2											</br>
	 * &emsp;&emsp;&emsp;&emsp;					hello_observer_child3										</br>
	 * 																										</br>
	 *
	 * @author laipl
	 *
	 */
	
	@Test
	void testDelete_syn_then_observe_parentresc() {
		System.out.println("--------------------- testDelete_syn_then_observe_parentresc ----------------------------");
		//
		// sleep main function avoid ending the program 
		// to let the handler thread to get more notifications from server
		MyThreadSleep.sleep20s();				
		//
		//----------------- client1 deletes server resource ----------------------
		// set delete handler
		CoapHandler myDeleteHandler = new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
            	System.out.println("---------------------------------------------------");
            	System.out.println("-------- delete handler onload start --------------");
            	System.out.println("result from server:" + response.isSuccess() );
				//
            	System.out.println("on load: " + response.getResponseText());
            	System.out.println("get code: " + response.getCode().name());
            	System.out.println("---------- delete handler onload end --------------");
            	System.out.println("---------------------------------------------------");;
            	//
            	del_respone_tmp = response;
            }

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
		};
		
		//del_reponse = client1.delete();
		client1.delete(myDeleteHandler);
		//------------------------------------------------------------------------
		//
		// sleep main function for getting the last notification due to concurrency
		MyThreadSleep.sleep10s();
        //
		//
        assertEquals(false,resultFromServer1.isSuccess(),"if_success_client1");
        assertEquals("DELETED",del_respone_tmp.getCode().name(),"test_deleted_client1");
        assertEquals("NOT_FOUND",resultFromServer1.getCode().name(),"test_notfound_client1");
		//
		//------------------------------------------------------------------------
		//--------------------------- client2 ------------------------------------
		// new client
		CoapClient client2 = null;
		client2 = new CoapClient(myuri1);
		//
		//
		// set handler
		CoapHandler myObserveHandler2 = new CoapHandler() { // e.g., anonymous inner class

			@Override
			public void onLoad(CoapResponse response) { // also error resp.
				System.out.println("---------------------------------------------------");
				System.out.println("--------- client side onload start ----------------");
				resultFromServer2 = response;
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
		CoapObserveRelation coapObRelation2 = client2.observe(myObserveHandler2);
		//
		//----------------------------------------
		//
		// sleep main function avoid ending the program 
		// to let the handler thread to get more notifications from server
		MyThreadSleep.sleep20s();				
		//
		// if children1 is deleted, parent resource could be observed
        assertEquals(true,resultFromServer2.isSuccess(),"if_success_client2");
        // --------------------------------------------------------------------
        // 必须要取消observe， 不然它会影响后面的testcase, 
        // 当然你用proactiveCancel也行
        //
        // 注意
        // 如果你用reactiveCancel 最好, 等一段时间再 让这个子程序结束
        // 因为 reactiveCancel 是等待 下一次notification过来的时候, 再发送RST 让server不再发送消息过来
        // 如果当下一次 notification 过来时, 这个子程序却 已经运行完了, 那么也就是说
        // 这个 子程序的  内部变量 coapObRelation2 来不及 发送RST 去server 取消订阅了
        //
        //
        coapObRelation2.reactiveCancel();
        //MyThreadSleep.sleep10s();
        //
        // shutdown need sometime to wait
        client2.shutdown();
        // 如果 你不信 你可以试试删掉 sleep, 你会发现 除了 update task 之外 还有 handle get的输出, 然而此时 你的client2已经没了
        // 在我看来 有点奇怪的是, 我明明 shutdown了 client2 可是在这里写sleep 也能避免RST无法发送的问题
        // 我猜测发送消息 可能是跟coapObRelation2有关, 
        // 因为他发送过去以后 不需要 server再发送消息过来了
        // 
        // 总而言之, 如果想要reactiveCancel 以后 还打算shutdown, 记得要稍微等待一段时间(最好超过 服务器每次发送消息的间隔)
        // 从而能够让 client2 等待 下一次信息过来 进而发送RST 给server, 从而达到取消订阅的目的
        MyThreadSleep.sleep10s();
        LOGGER.info("###############################################end");
	}
	
	
}
