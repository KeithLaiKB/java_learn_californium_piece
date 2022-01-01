package com.learn.californium.server.minimalexample;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.minimalexample.myresc.MyObserverResource_Con_Mwe;



/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE 意思就是  简化的例子														</br>
 * &emsp;						for testing the observer												</br>
 * 
 * 该服务器设置了
 * 资源	hello_observer
 * 并且	hello_observer 			设置了一个子资源 	hello_observer_child1 
 * 并且	hello_observer_child1 	设置了一个子资源 	hello_observer_child2 
 * 并且	hello_observer_child2 	设置了一个子资源 	hello_observer_child3 
 * 
 * 用来测试, client 那边去 delete server这边的resource
 * 
 * @author laipl
 *
 */
public class TestObserverMain_Mwe_trydelete  {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
		// 如果不填参数，则默认端口是5683
		// 这里我尝试自己定义一个端口5656
		//MyTestCoapServer server = new MyTestCoapServer(5683);
		//MyTestCoapServer server = new MyTestCoapServer(5656);
		CoapServer server = new CoapServer(5656);
		//
		//
		//
		//------------------------resource settings-------------------------------------
		// 注意 这里的 hello 大小写是敏感的
		// 因为 client那边 是根据 coap://localhost:5656/hello 来发送请求的
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
		// 问题, 我发现单纯destroy是 不会停止 resource里的计时器的
		// 所以我接下来就是需要 想想 是不是应该destroy 的时候把计时器也关掉
		// californium/californium-tests/californium-integration-tests/src/test/java/org/eclipse/californium/integration/test/SecureObserveTest.java 
		// 像是官方文档的话, 他是在Main 用个for循环 来更新值, 添加一个changed(String)方法  ,   然后手动 让 resouce 去change
		/*
		for (int i = 0; i < REPEATS; ++i) {
			resource.changed("client");
			Thread.sleep(50);
		}
		
		但实际情况不能这么做吧, 你在main 函数去更新, 
		相当于把 这个resource里的业务逻辑 	
				从resource 上移到了 server
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