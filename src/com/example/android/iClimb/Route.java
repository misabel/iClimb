package com.example.android.iClimb;

import java.util.ArrayList;

/**
 *
 * @author mariairizarry
 * This class creates a route that will contain objects of type Node.
 */
public class Route
{
	private ArrayList<Node> nodes;
	private String n;
	private String id;
	
	/**
	 * Constructor for creating a new route.
	 * @param name - Names the current route being created.
	 */
	Route(String name)
	{
		super();
		n = name;
		nodes = new ArrayList<Node>();
	}
	
	/**
	 * Get method for name of route.
	 * @return Name of route.
	 */
	public String getName()
	{
		return n;
	}
	
	/**
	 * Get all nodes on the route in form of a list.
	 * @return All nodes.
	 */
	public ArrayList<Node> getNodes()
	{
		return nodes;
	}
	
	/**
	 * Adds node to a route.
	 * @param n - Node to be added.
	 */
	public void addNode(Node n)
	{
		nodes.add(n);
	}
    
	/**
	 * Set the ID for route.
	 * @param routeID - String value of ID.
	 */
	public void setID(String routeID)
	{
		id = routeID;
	}
    
	/**
	 * Get method for ID.
	 * @return ID of route.
	 */
	public String getID()
	{
		return id;
	}
	
}
