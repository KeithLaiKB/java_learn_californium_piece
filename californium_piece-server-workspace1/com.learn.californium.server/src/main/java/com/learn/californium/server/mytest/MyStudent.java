package com.learn.californium.server.mytest;

public class MyStudent extends MyPeople {
	private String cls_attribute="this is MyStudent";
	public MyStudent() {
		super();
	}
	
	@Override
	public String outputMoneyAmount() {
		String str_temp = super.outputMoneyAmount();
		return "student:"+str_temp;
	}
}
