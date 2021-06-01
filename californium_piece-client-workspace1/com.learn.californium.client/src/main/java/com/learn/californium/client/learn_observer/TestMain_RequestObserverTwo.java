package com.learn.californium.client.learn_observer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;

/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						client to observe																	</br>
 * 																													</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						 californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java  	</br>	
 * 																													</br>
 *
 * @author laipl
 *
 */
public class TestMain_RequestObserverTwo {
    public static void main(String[] args) {
    	/**
    	 * 假设 服务器设置了
    	 * 资源	hello_observer
    	 * 并且	hello_observer 			设置了一个子资源 	hello_observer_child1 
    	 * 并且	hello_observer_child1 	设置了一个子资源 	hello_observer_child2 
    	 * 并且	hello_observer_child2 	设置了一个子资源 	hello_observer_child3 
    	 * 
    	 */
    	String myuri1 	     = "coap://localhost:5656/hello_observer";
    	String myuri1_wrong1 = "coap://localhost:5656/hello_observer_child1";				//根据 上面的假设, 这样是访问不到 hello_observer 的子资源		hello_observer_child1 
    	String myuri1_wrong2 = "coap://localhost:5656/hello_observer_child2";				//根据 上面的假设, 这样是访问不到 hello_observer 的子资源		hello_observer_child1 	的子资源	 hello_observer_child2 
    	String myuri1_c1 	 = "coap://localhost:5656/hello_observer/hello_observer_child1";
    	String myuri1_c2 	 = "coap://localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2";
    	String myuri1_c3	 = "coap://localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2/hello_observer_child3";
    	
        //
    	//-----------------------------------------------------------------------
    	// 对 uri 的解释
    	//
    	// 情况一:
    	// 当运行 localhost:5656/hello_observer/hello_observer_child1
    	// 使用了 delete
    	// 停止后
    	// 运行     localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2
    	// 是NOT_FOUND的
    	// +++++++++++++++++++++
    	//
    	// 情况二:
    	// 当运行 localhost:5656/hello_observer/hello_observer_child1
    	// 使用了 delete
    	// 停止后
    	// 运行     localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2/hello_observer_child3
    	// 是NOT_FOUND的
    	// +++++++++++++++++++++
    	//
    	// 情况三:
    	// 当运行 localhost:5656/hello_observer/hello_observer_child1
    	// 使用了 delete
    	// 停止后
    	// 运行     localhost:5656/hello_observer/hello_observer_child1/hello_observer_child2
    	// 是可以访问的到的
    	//
    	// 也就是说 如果服务器删除一个resource, 它的子resource也会被删除
    	//-----------------------------------------------------------------------
    	//
    	//
    	//CoapClient client = new CoapClient("coap://localhost:5683/hello_observer");
    	CoapClient client = new CoapClient(myuri1);
        //
        CoapObserveRelation coapObRelation1;
		//
        try {
			
			//response = client.get();
	        //Request rq1=new Request(Code.POST);
	        //rq1.setType(Type.CON);
	        //
        	// set handler for observer method, because observe method needs asynchronous operation
			CoapHandler myObserveHandler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
	            	System.out.println("---------------------------------------");
	            	System.out.println("-------- observe handler onload start --------------");
					System.out.println("result from server:" + response.isSuccess() );
	            	System.out.println("on load: " + response.getResponseText());
	            	System.out.println("get code: " + response.getCode().name());
	            	System.out.println("---------- observe handler onload end --------------");
	            	
	            }

	            @Override
	            public void onError() {
	            }
	        };

	        // set handler for delete method, you can choose use or not to use
	        // if you use this handler in delete(handler), it means you want asynchronous operation
			CoapHandler myDeleteHandler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	                //log.info("Command Response Ack: {}, {}", response.getCode(), response.getResponseText());
	            	System.out.println("---------------------------------------");
	            	System.out.println("-------- delete handler onload start --------------");
	            	System.out.println("result from server:" + response.isSuccess() );
					//
	            	System.out.println("on load: " + response.getResponseText());
	            	System.out.println("get code: " + response.getCode().name());
	            	System.out.println("---------- delete handler onload end --------------");
	            	
	            }

	            @Override
	            public void onError() {
	            }
	        };
	        //
	        //
	        //
			// 假设server中的resource 设定的type是NON 那么 效果就像下面这样
			// 53144	-> 	5656 	CON		GET ....
			// 5656		->	53144	ACK		1st_num
	        // 5656		->	53144	NON		2nd_num
	        // 5656		->	53144	NON		3rd_num
	        // 5656		->	53144	NON		4th_num
	        coapObRelation1 = client.observe(myObserveHandler);
			//response = client.observeAndWait(handler);
	        //response = client.observe(rq1,handler);
	        //
	        // 如果用observe的时候
	        // 区别在于,
	        // 假设先启动client, 没启动server, 
	        // 那么此时他	会	显示wow_hello,
	        //
	        // 如果用observeAndWait的时候
	        // 假设先启动client, 没启动server, 
	        // 那么此时他	不会	显示wow_hello,
	        //
	        // 所以就像是一个线程阻塞了的感觉
	        System.out.println("wow_hello");
	        //
	        if (coapObRelation1!=null) {
	        
	        	System.out.println( "response getCode:");
	        	System.out.println( "response getOptions:");
	        	//System.out.println( "response text:" + response.toString() );
	        	//System.out.println( "payload:" + response.getCurrent().getResponseText() );
	        	//response.getCurrentResponse();
	        	//System.out.println( "payload:" + new String(response.getCurrent().getPayload()) );
	        	
	        	//System.out.println(xml);
	        } else {
	        	
	        	System.out.println("Request failed");
	        	
	        }	
	        //
	        //
	        //
	        Scanner in =new Scanner(System.in) ;
            int int_choice = 0;
            while(int_choice!=-1) {
            	System.out.println("here is the choice:");
            	System.out.println("-1: to exit");
            	System.out.println("1: to delete");
            	System.out.println("2: to reactiveCancel");
            	System.out.println("3: to proactiveCancel");
            	System.out.println("4: to observe again");
            	System.out.println("enter the choice:");
            	// input
            	int_choice = in.nextInt();
            	if(int_choice==-1) {
            		//System.exit(0);
            		break;
            	}
            	else if(int_choice==1) {
            		//
            		System.out.println("deleteing record");
            		//System.out.println("deleting resources");
            		//
            		//
            		// 我认为 delete 挺重要的 所以我这选择的是同步
            		//client.delete();				// 用的是 同步, 对面没回应, 就不能继续往下走
            		client.delete(myDeleteHandler); // 用的是 异步
            		//
            		// 注意 这个delete 
            		// 可以是让 	服务器删除 这个资源
            		// 也可以是让	服务器删除 某个记录(比如server那边 连了个数据库)
            		// 这取决于 server 那边的 handleDelete 里的操作
            		//
            		// 如果 是让服务器删除 这个资源
            		// 以后 client 不会再收到这个resource的内容, 但是 server还是在运行 
            		// 所以server那边需要 把timer关掉, 此外还有可能要 remove(Resource resource)
            		//
            		//
            		//System.out.println("deleted resources");
            		//System.out.println("deleted record");
            	}
            	else if(int_choice==2) {
            		
            		// ref https://datatracker.ietf.org/doc/html/rfc7641#section-3.6
            		// When the server then
            		//  sends the next notification, the client will not recognize the token
            		//  in the message and thus will return a Reset message
            		//
            		// in order words it means
            		// send a RST when next notification arrives
            		// 
            		// 也就是 说 等到下一次 server发送 Notification过来的时候 
            		// 这个subscriber 才发送 RST给server 来取消观察这个消息
            		coapObRelation1.reactiveCancel();				//取消观察状态
            		System.out.println("reactiveCancel");
            		
            	}
            	else if(int_choice==3) {

            		// ref https://datatracker.ietf.org/doc/html/rfc7641#section-3.6
            		// In some circumstances, it may be desirable to cancel an observation
            		//   and release the resources allocated by the server to it more eagerly.
            		//   In this case, a client MAY explicitly deregister by issuing a GET
            		//   request that has the Token field set to the token of the observation
            		//   to be cancelled and includes an Observe Option with the value set to
            		//   1 (deregister)
            		//
            		// in order words it means
            		// send another cancellation request, with an Observe Option set to 1 (deregister)
            		// 
            		// 也就是 说  	不必 		等到下一次 server发送 Notification过来的时候,  这个subscriber 才发送 RST给server 来取消观察这个消息
            		// 而是直接	发送 一个 get请求	给server 来取消观察这个消息 
            		coapObRelation1.proactiveCancel();				//取消观察状态
            		System.out.println("proactiveCancel");
            	}
            	else if(int_choice==4) {
            		coapObRelation1 = client.observe(myObserveHandler); //取消观察状态后 还是可以继续observe的
            		System.out.println("observe again");
            	}
            }
	        //
            //---------------------------------------------
            in.close();
            
			//String xml = client.get(MediaTypeRegistry.APPLICATION_XML).getResponseText();
			//response.reactiveCancel();
            /*
	        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try { br.readLine(); } catch (IOException e) { }
			System.out.println("CANCELLATIONING");
			response.proactiveCancel();
			System.out.println("CANCELLATION FINISHED");
			*/
            //
            client.shutdown();
            System.exit(0);
		//		
		//	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
