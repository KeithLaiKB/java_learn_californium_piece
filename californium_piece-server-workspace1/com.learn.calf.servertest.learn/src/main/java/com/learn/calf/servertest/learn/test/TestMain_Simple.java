package com.learn.calf.servertest.learn.test;


import org.eclipse.californium.core.CoapServer;



public class TestMain_Simple {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 如果不填参数，则默认端口是5683
		// 这里我尝试自己定义一个端口5656
		CoapServer server = new CoapServer();
		// 注意 这里的 hello 大小写是敏感的
		// 因为 client那边 是根据 coap://localhost:5656/hello 来发送请求的
		server.add(new MyResource("hello"));

		server.start(); // does all the magic

	}

}
