package com.learn.californium.server.mydemo;

import java.util.List;

import org.eclipse.californium.core.network.Endpoint;

public interface IMyCoapServer {
	public abstract List<Endpoint> getMyEndPoints();
}
