/*******************************************************************************
 * Copyright (c) 2019 Bosch Software Innovations GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Achim Kraus (Bosch Software Innovations GmbH) - initial implementation.
 ******************************************************************************/
package com.learn.californium.client_dtls.v3_2_0.tryssl;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.californium.elements.util.StringUtil;

import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;

/**
 * Test utility for openssl interoperability.
 * 
 * Requires external openssl installation, otherwise the tests are skipped. On
 * linux install just the openssl package (version 1.1.1). On windows you may
 * install git for windows,
 * <a href="https://git-scm.com/download/win" target="_blank">git</a> and add
 * the extra tools to your path ("Git/mingw64/bin", may also be done using a
 * installation option). Alternatively you may install openssl for windows on
 * it's own <a href=
 * "https://bintray.com/vszakats/generic/download_file?file_path=openssl-1.1.1c-win64-mingw.zip"
 * target="_blank">OpenSsl for Windows</a> and add that to your path.
 * 
 * Note: the windows version 1.1.1a to 1.1.1k of the openssl s_server seems to
 * be broken. It starts only to accept, when the first message is entered.
 * Therefore the test are skipped on windows.
 * 
 * Note: version 1.1.1l of the openssl s_server' PSK support is broken. See
 * {@link #assumePskServerVersion()} for more details.
 */
public class OpenSslProcessUtil {

	public enum AuthenticationMode {
		/**
		 * Use PSK.
		 */
		PSK,
		/**
		 * Send peer's certificate, trust all.
		 */
		CERTIFICATE,
		/**
		 * Send peer's certificate-chain, trust all.
		 */
		CHAIN,
		/**
		 * Send peer's certificate-chain, trust provided CAs.
		 */
		TRUST
	}

	public static final String DEFAULT_CURVES = "X25519:prime256v1";
	public static final String DEFAULT_SIGALGS = "ECDSA+SHA384:ECDSA+SHA256:RSA+SHA256";



	/**
	 * Create instance.
	 */
	public OpenSslProcessUtil() {
	}





}
