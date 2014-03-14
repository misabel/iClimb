package com.example.android.iClimb;

import java.util.ArrayList;

public class Route
{
	private ArrayList<String> addresses;
	private String n;
	Route(String name)
	{
		super();
		n = name;
		addresses = new ArrayList<String>();
	}
	
	public String getName()
	{
		return n;
	}
	public ArrayList<String> getNodeAddresses()
	{
		return addresses;
	}
	public void addNode(String n)
	{
		addresses.add(n);
	}
	
}
