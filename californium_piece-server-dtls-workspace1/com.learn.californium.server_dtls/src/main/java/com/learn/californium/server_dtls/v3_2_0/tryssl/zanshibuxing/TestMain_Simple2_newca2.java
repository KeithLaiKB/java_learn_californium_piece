package com.learn.californium.server_dtls.v3_2_0.tryssl.zanshibuxing;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.elements.util.ClockUtil;

import org.eclipse.californium.elements.util.SslContextUtil.Credentials;
import org.eclipse.californium.elements.util.StringUtil;
import org.eclipse.californium.scandium.DTLSConnector;

import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;



import com.learn.californium.server_dtls.v3_2_0.tryssl.OpenSslProcessUtil.AuthenticationMode;

/**
 * ref:californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslServerInteroperabilityTest.java 
 * 
 * 用的是不同的加密套件 		cipherSuite = CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256;
 * 
 * 暂时运行不了
 * 
 * @author laipl
 *
 */
public class TestMain_Simple2_newca2 {

	//public TestNameLoggerRule name = new TestNameLoggerRule();

	//private static final InetSocketAddress BIND = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
	//private static final InetSocketAddress DESTINATION = new InetSocketAddress(InetAddress.getLoopbackAddress(),ScandiumUtil.PORT);
	
	private static final int myDefaultPort = 5684;
	//private static final String ACCEPT = "127.0.0.1:" + myDefaultPort;
	//private static final String ACCEPT = "172.26.64.1:" + myDefaultPort;
	private static final String ACCEPT = "192.168.239.137:" + myDefaultPort;

	//private static OpenSslProcessUtil processUtil;
	//private static ScandiumUtil scandiumUtil;
	
	
	
	//ref:californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ConnectorUtil.java
	private static final String SERVER_NAME = "server";
	
	public static final String TRUST_CA = "ca";
	public static final String TRUST_ROOT = "root";
	/**
	 * DTLS connector.
	 */
	private DTLSConnector connector;
	/**
	 * Credentials for ECDSA base cipher suites.
	 */
	private Credentials serverCredentials;
	
	/**
	 * Specific credentials for ECDSA base cipher suites to be used by the next test.
	 */
	private Credentials nextCredentials;
	private Certificate[] trustedCertificates;
	private Certificate[] trustRoot;
	
	
	
	private static final String KEY_STORE_LOCATION = "mycerts/my_own/mykeystore.jks";
	//private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
	private static final char[] KEY_STORE_PASSWORD = "SksOneAdmin".toCharArray();
	private static final String TRUST_STORE_LOCATION = "mycerts/my_own/mykeystore_truststore.jks";
	//private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
	private static final char[] TRUST_STORE_PASSWORD = "StsOneAdmin".toCharArray();
	
	
	public static final String OPENSSL_PSK_IDENTITY = "Client_identity";
	public static final byte[] OPENSSL_PSK_SECRET = "secretPSK".getBytes();

	
	//ref:californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslClientAuthenticationInteroperabilityTest.java
	private static CipherSuite cipherSuite;
	
	//public String serverPemCertificate="mykeystorepem.pem";
	public String serverPemCertificate="ktserverksone.pem";
	//public String serverPemCertificate="ktserveronepem.pem";
	public String serverTrustStorePemCertificate="mykeystore_truststorepem.pem";
	//public String serverPemCertificate_dir				="/mycerts/my_own";
	public String serverPemCertificate_dir				="/mycerts/new/my_own/backup2";
	public String serverTrustStorePemCertificate_dir	="/mycerts/my_own";
	
	private static String serverPemCertificate_loc = null;
	private static String serverTrustStorePemCertificate_loc = null;
	
	
	public String caCertificate							="ktserverksone.crt";
	public String caCertificate_dir						="/mycerts/new/my_own/backup2";
	private static String caCertificate_loc				=null;
	
	//ref: californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslProcessUtil.java
	public void add(List<String> args, OpenSslProcessUtil.AuthenticationMode authMode, String chain)
			throws IOException, InterruptedException {
		switch (authMode) {
		case PSK:
			break;
		case CERTIFICATE:
			args.add("-no-CAfile");
			break;
		case CHAIN:
			args.add("-no-CAfile");
			args.add("-cert_chain");
			args.add(chain);
			break;
		case TRUST:
			args.add("-CAfile");
			args.add(caCertificate_loc);
			args.add("-build_chain");
			break;
		}
	}
	//ref: californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslProcessUtil.java
	public void add(List<String> args, String curves, String sigAlgs) throws IOException, InterruptedException {
		if (curves != null) {
			args.add("-curves");
			args.add(curves);
		}
		if (sigAlgs != null) {
			args.add("-sigalgs");
			args.add(sigAlgs);
		}
	}
	
	//ref: californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ProcessUtil.java
	private void startReadingOutput(final Process process, final String tag) {
		//setResult(null);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					boolean stopped = false;
					long time = System.nanoTime();
					Reader reader = null;
					StringBuilder console = new StringBuilder();
					try {
						reader = new InputStreamReader(process.getInputStream());
						char[] buffer = new char[2048];						// 创建一个 指定长度的数组
						int read;
						// 每次 读取 之前数组的长度的内容 放到之前的数组当中,例如 整个串 有 4098 
						// 第一次读取 2048 的内容放到 之前的数组当中
						// 第二次读取 2048 的内容放到 之前的数组当中
						// 第三次读取剩余2的内容 放到 之前的数组当中
						while ((read = reader.read(buffer)) >= 0) {			
							//stopped = isStopped();
							//if (stopped) {
							//	break;
							//}
							String out = new String(buffer, 0, read);		// 将每一次 数组中存放的内容 从0 到 内容中的长度 的内容  创建string
							//if (verbose) {
	 						//	System.out.println("> (" + out.length() + " bytes)");
							//}
 							System.out.print(out);
							System.out.flush();
							console.append(out);
							//setConsole(console.toString());

						}
					} catch (IOException e) {
						//stopped = isStopped();
						//if (!stopped) {
							//e.printStackTrace();
						//}
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException e) {
							}
						}
					}
					//int rc = process.waitFor();
					//time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - time);
					//StringBuilder message = new StringBuilder("> exit: ");
					//message.append(process.exitValue());
					//message.append(" (").append(time).append("ms");
					//if (stopped) {
						//message.append(", stopped");
					//}
					//message.append(tag).append(").");
					//System.out.println(message);
					//setResult(new ProcessResult(rc, console.toString()));
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	
	public String showString(String str) {
		System.out.println(str);
		return str;
	}
	
	
	public static void main(String[] args) {
		new TestMain_Simple2_newca2().my_configureToPrepare();
	}
	
	public void my_configureToPrepare() {
		String myusr_path = System.getProperty("user.dir");
		serverPemCertificate_loc 			=	myusr_path+ serverPemCertificate_dir				+"/"	+serverPemCertificate;
		serverTrustStorePemCertificate_loc 	= 	myusr_path+ serverTrustStorePemCertificate_dir		+"/"	+serverTrustStorePemCertificate;

		
		caCertificate_loc 					= 	myusr_path+ caCertificate_dir						+"/" 	+caCertificate;
		///////////////ref: 相关的test方法    from  californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslServerAuthenticationInteroperabilityTest.java /////////////////////////////////
		

		//cipherSuite = CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8;
		cipherSuite = CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256;
		
		//-------------------ref: startupServer(String accept, OpenSslProcessUtil.AuthenticationMode authMode, CipherSuite... ciphers)
		//------------------------ from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslProcessUtil.java  ------------------
	
		//OpenSslProcessUtil.AuthenticationMode authMode = AuthenticationMode.CERTIFICATE;
		OpenSslProcessUtil.AuthenticationMode authMode = AuthenticationMode.TRUST;
		
		
		String curves=null;
		String sigAlgs=null;	//代表signature algorithm?
		// 和上面有一些些 不同, 参数 的数量不一样
		//-------------------ref: startupServer(String accept, OpenSslProcessUtil.AuthenticationMode authMode, String serverCertificate,String curves, String sigAlgs, CipherSuite... ciphers)  ------------------
		//------------------------ from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/openssl/OpenSslProcessUtil.java  ------------------
		List<CipherSuite> suites = Arrays.asList(cipherSuite);
		List<String> args = new ArrayList<String>();
		String openSslCiphers = OpenSslUtil.getOpenSslCipherSuites(cipherSuite);
		args.addAll(Arrays.asList("openssl", "s_server", "-4", "-dtls1_2", "-accept", ACCEPT, "-listen", "-verify", "5",
				"-cipher", openSslCiphers));
		if (CipherSuite.containsPskBasedCipherSuite(suites)) {
			//assumePskServerVersion();
			args.add("-psk_identity");
			args.add(OPENSSL_PSK_IDENTITY);
			args.add("-psk");
			args.add(StringUtil.byteArray2Hex(OPENSSL_PSK_SECRET));
		}
		if (CipherSuite.containsCipherSuiteRequiringCertExchange(suites)) {
			args.add("-cert");
			args.add(serverPemCertificate_loc);							//.key
			//String chain = CA_CERTIFICATES;
			String chain = caCertificate_loc;									//暂时不知道这个干什么的
			//if (SERVER_CA_RSA_CERTIFICATE.equals(serverCertificate)) {
				//chain = CA_RSA_CERTIFICATES;
			//}
			try {
				add(args, authMode, chain);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			add(args, curves, sigAlgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//print(args);
		//execute(args);
		
		///////////////ref: execute(List<String> args) from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ProcessUtil.java 
		String console = "";
		long lastConsoleUpdate= ClockUtil.nanoRealtime();
		boolean stopped= false;
		
		ProcessBuilder builder = new ProcessBuilder(args);
		builder.redirectErrorStream(true);
		Process process = null;
		try {
			process = builder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//setProcess(process);
		
		String tag = "";		//ref: constructor in ProcessUtil
		startReadingOutput(process, tag);
		
		
		// ensure, server is ready to ACCEPT messages
		//assumeTrue(waitConsole("ACCEPT", TIMEOUT_MILLIS));
		//return "(" + openSslCiphers.replace(":", "|") + ")";
			
		
		
		String message = "Hello Scandium! hellloooo";
		if (process != null) {
			System.out.println("< " + message);
			try {
				process.getOutputStream().write(message.getBytes());
				process.getOutputStream().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new IllegalStateException("process not running");
		}
		

		
		//----------------
		//---------------- to stop
		for (String arg : args) {
			System.out.print(arg);
			System.out.print(" ");
		}
		System.out.println();
		
		
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ref: stop() from californium/californium-tests/californium-interoperability-tests/src/test/java/org/eclipse/californium/interoperability/test/ProcessUtil.java
		if (process != null) {
			try {
				process.exitValue();
				System.out.println("process stopped" + tag);
			} catch (IllegalThreadStateException ex) {
				//setStopped(true);
				process.destroy();
				System.out.println("process forced stopped" + tag);
			}
		}
	}

}