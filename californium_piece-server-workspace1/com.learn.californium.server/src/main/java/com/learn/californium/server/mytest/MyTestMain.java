package com.learn.californium.server.mytest;

public class MyTestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyPeople myPeople1= new MyPeople();
		System.out.println(myPeople1.getCls_attribute());
		
		MyPeople myPeople_Student1= new MyStudent();
		System.out.println(myPeople_Student1.getCls_attribute());
		
		MyPeople myStudent1= new MyStudent();
		System.out.println(myStudent1.getCls_attribute());
		
		System.out.println(myStudent1.outputMoneyAmount());
	}

}
