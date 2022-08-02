package com.learn.californium.server_dtls.v3_2_0.tryssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 尝试用Java 来调用系统的命令台 来 ping 网络
 * @author laipl
 *
 */
public class TryProcessBuilder {

    public static void main(String[] args) {

        ProcessBuilder processBuilder = new ProcessBuilder();

        // Run this on Windows, cmd, /c = terminate after this run
        // 相当于在cmd中使用          cmd.exe /c ping -n 3 google.com  
        // 它是可以使用的
        processBuilder.command("cmd.exe", "/c", "ping -n 3 google.com");

	    try {
	
	        Process process = processBuilder.start();
	
			// blocked :(
	        BufferedReader reader =
	                new BufferedReader(new InputStreamReader(process.getInputStream()));
	
	        String line;
	        while ((line = reader.readLine()) != null) {
	            System.out.println(line);
	        }
	
	        int exitCode = process.waitFor();
	        System.out.println("\nExited with error code : " + exitCode);
	
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }

}

}