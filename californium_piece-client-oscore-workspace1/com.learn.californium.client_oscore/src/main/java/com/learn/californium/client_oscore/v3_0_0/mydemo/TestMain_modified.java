package com.learn.californium.client_oscore.v3_0_0.mydemo;

import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.cose.AlgorithmID;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.californium.oscore.HashMapCtxDB;
import org.eclipse.californium.oscore.OSCoreCoapStackFactory;
import org.eclipse.californium.oscore.OSCoreCtx;
import org.eclipse.californium.oscore.OSCoreResource;
import org.eclipse.californium.oscore.OSException;

public class TestMain_modified {

	private final static HashMapCtxDB db = new HashMapCtxDB();
	//
	//

	//
	//private final static String uriLocal = "coap://localhost";
	private static String uri_addr1 = "127.0.0.1";
	private static String uri_addr2 = "135.0.237.84";			//因为你的树莓派已经端口映射到它的公共IP上了, 用这个就可以了
	private static String uri_addr3 = "192.168.239.137";		
	private static String uri_addr4 = "192.168.50.178";			//因为你是访问者, 你不需要知道树莓派在它的局域网中的内部IP,所以这个不需要
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
	private final static String hello1 = "/hello/1";
	private final static AlgorithmID alg = AlgorithmID.AES_CCM_16_64_128;
	private final static AlgorithmID kdf = AlgorithmID.HKDF_HMAC_SHA_256;

	// test vector OSCORE draft Appendix C.1.1
	private final static byte[] master_secret = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
			0x0C, 0x0D, 0x0E, 0x0F, 0x10 };
	private final static byte[] master_secret2 = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
			0x0C, 0x0D, 0x0E, 0x0F, 0x11 };
	private final static byte[] master_salt = { (byte) 0x9e, (byte) 0x7c, (byte) 0xa9, (byte) 0x22, (byte) 0x23,
			(byte) 0x78, (byte) 0x63, (byte) 0x40 };
	private final static byte[] master_salt2 = { (byte) 0x9e, (byte) 0x7c, (byte) 0xa9, (byte) 0x22, (byte) 0x23,
			(byte) 0x78, (byte) 0x63, (byte) 0x41 };
	
	private final static byte[] sid = new byte[0];
	//private final static byte[] sid = new byte[1];
	// 写上 server 的id 作为  Recipient ID
	private final static byte[] rid = new byte[] { 0x01 };

	public static void main(String[] args) throws OSException, ConnectorException, IOException {
		OSCoreCtx ctx = new OSCoreCtx(master_secret, true, alg, sid, rid, kdf, 32, master_salt, null);
		// context好像可以不一样
		db.addContext(uriLocal2, ctx);

		OSCoreCoapStackFactory.useAsDefault(db);
		CoapClient c = new CoapClient(uriLocal2 +":5656" + hello1);

		Request r = new Request(Code.GET);
		CoapResponse resp = c.advanced(r);
		printResponse(resp);

		System.out.println("kkkkkkkkkkkkkkkkk");
		
		r = new Request(Code.GET);
		// 在getOptions()的时候,  options = new OptionSet();
		r.getOptions().setOscore(new byte[0]);
		//r.getOptions();
		resp = c.advanced(r);
		printResponse(resp);
		c.shutdown();
	}

	private static void printResponse(CoapResponse resp) {
		if (resp != null) {
			System.out.println("RESPONSE CODE: " + resp.getCode().name() + " " + resp.getCode());
			if (resp.getPayload() != null) {
				System.out.print("RESPONSE PAYLOAD: ");
				for (byte b : resp.getPayload()) {
					System.out.print(Integer.toHexString(b & 0xff) + " ");
				}
				System.out.println();
			}
			System.out.println("RESPONSE TEXT: " + resp.getResponseText());
		} else {
			System.out.println("RESPONSE IS NULL");
		}
	}
}