package com.learn.californium.server.mydemo;

import java.util.List;

import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.server.resources.Resource;

public interface IMyCoapServer {
	public abstract List<Endpoint> getMyEndPoints();
	
	public abstract boolean remove(Resource resource);
}
