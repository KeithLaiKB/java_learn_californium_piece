package com.learn.californium.server.impl;

import java.util.List;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.observe.ObserveRelationContainer;
import org.eclipse.californium.core.server.ServerMessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.core.server.resources.ResourceObserver;

import com.learn.californium.server.IMyCoapServer;

public class MyCoapServer implements IMyCoapServer {

	CoapServer server1 =null ;
	

	
	
	
	public MyCoapServer() {

	}

	public MyCoapServer(final int... ports) {
		this.server1 = new CoapServer(ports);

	}


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

	
	
	
}
