package com.example.android.iClimb;

import java.util.ArrayList;

public class Route
{
	private ArrayList<Node> nodes;
	private String n;
	private String id;
	
	Route(String name)
	{
		super();
		n = name;
		nodes = new ArrayList<Node>();
	}
	
	public String getName()
	{
		return n;
	}
	
	public ArrayList<Node> getNodes()
	{
		return nodes;
	}
	
	public void addNode(Node n)
	{
		nodes.add(n);
	}

	public void setID(String routeID) 
	{
		id = routeID;
	}

	public String getID()
	{
		return id;
	}
	
}
