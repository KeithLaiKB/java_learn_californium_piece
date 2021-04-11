package com.learn.californium.server.mytest;

public class MyPeople {
	private String cls_attribute="this is MyPeople";
	
	private int money_amount = 0;

	public String getCls_attribute() {
		return cls_attribute;
	}

	public String outputMoneyAmount() {
		return "people:"+this.money_amount;
	}
	
	
	
	private String jobType="doctor";
}
