package com.learn.californium.server.mydemo.impl;

import java.util.List;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.Endpoint;

import org.eclipse.californium.core.server.ServerMessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;

import com.learn.californium.server.mydemo.IMyCoapServer;
/**
 * 
 * 
 * <p>
 * 							description:																					</br>	
 * &emsp;						implements 	IMyCoapServer, so that the resource can access parts info of server				</br>
 * 	

 *
 * @author laipl
 *
 */
public class MyTestCoapServer implements IMyCoapServer {

	CoapServer server1 =null ;
	

	
	
	
	public MyTestCoapServer() {

	}

	public MyTestCoapServer(final int... ports) {
		this.server1 = new CoapServer(ports);

	}

	@Override
	public List<Endpoint> getMyEndPoints() {
		// TODO Auto-generated method stub
		System.out.println("now there are:" + server1.getEndpoints().size());
		System.out.println("now there are uri:" + server1.getEndpoints().get(0).getUri());
		System.out.println("now there are content:" + (ServerMessageDeliverer)(server1.getMessageDeliverer()));
		return server1.getEndpoints();
	}


	public int add(Resource... resources) {
		this.server1.add(resources);
		return 1;
	}
	
	public void start() {
		this.server1.start();
	}

	
	
	@Override
	public boolean remove(Resource resource) {
		// TODO Auto-generated method stub
		return this.server1.remove(resource);
	}

	
	
	
}
