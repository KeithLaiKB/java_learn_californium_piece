package com.learn.californium.server_oscore.v3_0_0.mydemo.observerdemo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.cose.AlgorithmID;
import org.eclipse.californium.elements.util.Bytes;
import org.eclipse.californium.oscore.HashMapCtxDB;
import org.eclipse.californium.oscore.OSCoreCoapStackFactory;
import org.eclipse.californium.oscore.OSCoreCtx;
import org.eclipse.californium.oscore.OSCoreResource;
import org.eclipse.californium.oscore.OSException;

public class TestObserverMain {

	private final static HashMapCtxDB db = new HashMapCtxDB();
	//
	//
	//
	private static String uri_addr1 = "127.0.0.1";
	private static String uri_addr2 = "135.0.237.84";			//如果你的树莓派 上方没有路由器, 而是公共IP, 则你用这个
	private static String uri_addr3 = "192.168.239.137";		
	private static String uri_addr4 = "192.168.50.178";			//因为你放在树莓派这个服务器上, 并且你的树莓派上有路由器, 这个是树莓派在那个路由器下的地址
	//
	//private final static String uriLocal 			= "coap://localhost";
	private final static String uriLocal1 			= "coap://"+uri_addr1;
	private final static String uriLocal2 			= "coap://"+uri_addr2;
	private final static String uriLocal3 			= "coap://"+uri_addr3;
	private final static String uriLocal4 			= "coap://"+uri_addr4;
	private final static String uriLocal9 			= "myranduri";
	//
	//
	//
	//OSCORE context information shared between server and client
	private final static AlgorithmID alg = AlgorithmID.AES_CCM_16_64_128;
	private final static AlgorithmID kdf = AlgorithmID.HKDF_HMAC_SHA_256;
	private final static byte[] master_secret = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
			0x0C, 0x0D, 0x0E, 0x0F, 0x10 };
	private final static byte[] master_salt = { (byte) 0x9e, (byte) 0x7c, (byte) 0xa9, (byte) 0x22, (byte) 0x23,
			(byte) 0x78, (byte) 0x63, (byte) 0x40 };

	
	//--------------------------------------
	private static Endpoint serverEndpoint;
	//public static final InetSocketAddress LOCALHOST_EPHEMERAL = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
	//public static final InetSocketAddress LOCALHOST_EPHEMERAL = new InetSocketAddress(InetAddress.getLoopbackAddress(), 5656);
	public static final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress(uri_addr1,5656);
	public static final InetSocketAddress LOCALHOST_EPHEMERAL2 = new InetSocketAddress(uri_addr2,5656);
	public static final InetSocketAddress LOCALHOST_EPHEMERAL3 = new InetSocketAddress(uri_addr3,5656);
	public static final InetSocketAddress LOCALHOST_EPHEMERAL4 = new InetSocketAddress(uri_addr4,5656);
	private static Timer timer;
	
	
	
	public static void main(String[] args) throws OSException {
		/*
		OSCoreCtx ctx = new OSCoreCtx(master_secret, false, alg, sid, rid, kdf, 32, master_salt, null);
		db.addContext(uriLocal, ctx);
		OSCoreCoapStackFactory.useAsDefault(db);

		final CoapServer server = new CoapServer(5683);

		OSCoreResource hello = new OSCoreResource("hello", true) {

			@Override
			public void handleGET(CoapExchange exchange) {
				System.out.println("Accessing hello resource");
				Response r = new Response(ResponseCode.CONTENT);
				r.setPayload("Hello Resource");
				exchange.respond(r);
			}
		};

		OSCoreResource hello1 = new OSCoreResource("1", true) {

			@Override
			public void handleGET(CoapExchange exchange) {
				System.out.println("Accessing hello/1 resource");
				Response r = new Response(ResponseCode.CONTENT);
				r.setPayload("Hello World!");
				exchange.respond(r);
				server.destroy();
			}
		};

		server.add(hello.add(hello1));
		server.start();
		
		
		*/
		//Set up OSCORE context information for response (server)
		byte[] sid = new byte[] { 0x01 };
		byte[] rid = Bytes.EMPTY;
		
		System.out.println(InetAddress.getLoopbackAddress());
		
		
		EndpointManager.clear();
		OSCoreCoapStackFactory.useAsDefault(db);
		
		
		byte[] myContextId1 = { 0x74, 0x65, 0x73, 0x74, 0x74, 0x65, 0x73, 0x74 };
		byte[] myContextId2 = { 0x74, 0x65, 0x73, 0x74, 0x74, 0x65, 0x73, 0x75 };
		byte[] myContextId3 = { 0x74, 0x65, 0x73, 0x74, 0x74, 0x65, 0x73, 0x76 };
		try {
			OSCoreCtx ctx_B = new OSCoreCtx(master_secret, false, alg, sid, rid, kdf, 32, master_salt, null);
			//db.addContext(uriLocal, ctx_B);
			db.addContext(uriLocal3, ctx_B);
		}
		catch (OSException e) {
			System.err.println("Failed to set server OSCORE Context information!");
		}
		
		
		
		//Create server
		CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
		builder.setCustomCoapStackArgument(db);
		builder.setInetSocketAddress(LOCALHOST_EPHEMERAL3);
		serverEndpoint = builder.build();
		CoapServer server = new CoapServer();
		server.addEndpoint(serverEndpoint);

		/** --- Resources for Observe tests follow --- **/
		
		//Base resource for OSCORE Observe test resources
		OSCoreResource oscore = new OSCoreResource("oscore", true);
		
		//Second level base resource for OSCORE Observe test resources
		OSCoreResource oscore_hello = new OSCoreResource("hello", true);

		/**
		 * The resource for testing Observe support 
		 * 
		 * Responds with "one" for the first request and "two" for later updates.
		 *
		 */
		class ObserveResource extends CoapResource {
			
			public String value = "one";
			private boolean firstRequestReceived = false;

			public ObserveResource(String name, boolean visible) {
				super(name, visible);
				
				this.setObservable(true); 
				this.setObserveType(Type.NON);
				this.getAttributes().setObservable();
				
				timer.schedule(new UpdateTask(), 0, 750);
			}

			@Override
			public void handleGET(CoapExchange exchange) {
				firstRequestReceived  = true;

				exchange.respond(value);
			}
			
			//Update the resource value when timer triggers (if 1st request is received)
			class UpdateTask extends TimerTask {
				@Override
				public void run() {
					if(firstRequestReceived) {
						value = "two";
						changed(); // notify all observers
					}
				}
			}
		}
		//
		timer = new Timer();
		//observe2 resource for OSCORE Observe tests
		ObserveResource oscore_observe2 = new ObserveResource("observe2", true);

		//Creating resource hierarchy	
		oscore.add(oscore_hello);
		oscore.add(oscore_observe2);

		server.add(oscore);

		/** --- End of resources for Observe tests **/

		//Start server
		server.start();
		//cleanup.add(server);
	}
}
