package com.learn.californium.server_dtls;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;

public class TestMainDTLSCoapServer_Try {
	/*
	public static void main(String[] args) {
		CoapEndpoint serverEndpoint = createEndpoint("server", TEST_EXCHANGE_LIFETIME, TEST_ACK_TIMEOUT,
				TEST_DTLS_TIMEOUT, TEST_DTLS_PSK_DELAY);
		CoapServer server = new CoapServer(serverEndpoint.getConfig());
		server.addEndpoint(serverEndpoint);
		server.start();
		URI uri = serverEndpoint.getUri();
		List<CoapEndpoint> clientEndpoints = new ArrayList<>();
		int clients = TestScope.enableIntensiveTests() ? TEST_CLIENTS : 10;
		for (int i = 0; i < clients; ++i) {
			CoapEndpoint clientEndpoint = createEndpoint("client-" + i, TEST_EXCHANGE_LIFETIME, TEST_ACK_TIMEOUT,
					TEST_DTLS_FAST_TIMEOUT, 0);
			clientEndpoint.start();
			clientEndpoints.add(clientEndpoint);
		}
		List<Request> requests = new ArrayList<>();
		for (CoapEndpoint clientEndpoint : clientEndpoints) {
			Request request = Request.newGet();
			request.setURI(uri);
			clientEndpoint.sendRequest(request);
			requests.add(request);
		}
		List<Integer> pending = new ArrayList<>();
		List<Integer> errors = new ArrayList<>();
		for (int index = 0; index < requests.size(); ++index) {
			Request request = requests.get(index);
			Response response = request.waitForResponse(TEST_EXCHANGE_LIFETIME);
			if (response == null) {
				if (request.getSendError() != null) {
					errors.add(index);
				} else {
					pending.add(index);
				}
			}
		}
		for (CoapEndpoint clientEndpoint : clientEndpoints) {
			try {
				clientEndpoint.destroy();
			} catch (Exception ex) {

			}
		}
		try {
			server.destroy();
		} catch (Exception ex) {

		}
		if (!pending.isEmpty() || !errors.isEmpty()) {
			StringBuilder message = new StringBuilder("loop: ");
			message.append(loop).append(" - ");
			if (!errors.isEmpty()) {
				message.append(errors.size()).append(" requests failed, ");
				int max = Math.min(5, errors.size());
				for (int index = 0; index < max; ++index) {
					message.append(errors.get(index)).append(' ');
				}
				message.append(", ");
			}
			if (!errors.isEmpty()) {
				message.append(pending.size()).append(" requests pending, ");
				int max = Math.min(5, pending.size());
				for (int index = 0; index < max; ++index) {
					message.append(pending.get(index)).append(' ');
				}
			}
			fail(message.toString());
		}
		for (AsyncAdvancedPskStore pskStore : pskStores) {
			pskStore.shutdown();
		}
		pskStores.clear();
		System.gc();
		Thread.sleep(200);
		
		
	}
	*/
}
