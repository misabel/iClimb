package com.example.android.iClimb;

import java.util.ArrayList;
import java.util.HashMap;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

/**
 *
 * @author mariairizarry
 * Class that will hold all information regarding the climbing wall.
 */
public class Wall
{
	
	private static ArrayList<Route> routes = new ArrayList<Route>();
	private static ArrayList<String> routeNames = new ArrayList<String>();
	private static HashMap<String, Node> mappedNodes = new HashMap<String, Node>();
	private static ArrayList<Node> allNodes = new ArrayList<Node>();
	private static String name;
	public static int numNodes = 0;
	public static int numRoutes = 0;
	private static BluetoothDevice dataToSave;
	
	/**
	 * Get name of wall
	 * @return String
	 */
	public static String getName()
	{
		return name;
	}
	
	/**
	 * Set the name of the wall
	 * @param n
	 */
	public static void setWallName(String n)
	{
		name = n;
	}
	
	/**
	 * Save Route to wall
	 * @param p - Route to be saved
	 */
	public static void saveRoute(Route p)
	{
		routes.add(p);
		routeNames.add(p.getName());
	}
	
	/**
	 * Save all nodes created to wall
	 * @param nodes - List of nodes to be saved
	 */
	public static void saveNodes(ArrayList<Node> nodes)
	{
		
		allNodes = nodes;
	}
	
	/**
	 * Maps node to its corresponding address
	 * @param n - Node to be mapped
	 */
	public static void mapNode(Node n)
	{
        mappedNodes.put(n.getAddress(), n);
	}
	
	/**
	 * Gets all nodes on the wall
	 * @return List of nodes on the wall
	 */
	public static ArrayList<Node> getNodes()
	{
		return allNodes;
	}
	
	/**
	 * Gets node that belongs to the given address
	 * @param addr - address of node desired
	 * @return Node belonging to the provided address
	 */
	public static Node getMappedNode(String addr)
	{
		return mappedNodes.get(addr);
	}
	
	/**
	 * Gets all mapped nodes on the wall
	 * @return Mapped nodes
	 */
	public static HashMap<String, Node>getAllNodes()
	{
		return mappedNodes;
	}
	
	/**
	 * Get all routes that are currently saved on the wall
	 * @return All routes
	 */
	public static ArrayList<Route> getRoutes()
	{
		return routes;
	}
	
	/**
	 * Loads all routes that are currently saved on the wall
	 * @return All routes
	 */
	public static ArrayList<Route> loadPaths()
	{
		return routes;
	}
	
	/**
	 * Get names for all the routes saved on the wall
	 * @return List of name for all routes
	 */
	public static ArrayList<String> getRouteNames()
	{
		return routeNames;
	}
	
	/**
	 * Set the number of node capacity for the wall
	 * @param nNodes - capacity
	 */
	public static void setNumNodes(int nNodes)
	{
		numNodes = nNodes;
	}
	
	/**
	 * Get the capacity of the wall
	 * @return Capacity
	 */
	public static int getNumNodes ()
	{
		return numNodes;
	}
	
	/**
	 * Set number of routes that are saved on the wall
	 * @param nPaths - Number of routes
	 */
	public static void setNumRoutes(int nPaths)
	{
		numRoutes = nPaths;
	}
	
	/**
	 * Get the number of routes that are saved on the wall
	 * @return number of routes
	 */
	public static int getNumRoutes()
	{
		return numRoutes;
	}
	
	/**
	 * Get data
	 * @return data
	 */
    public static BluetoothDevice getData()
    {
    	return dataToSave;
    }
    
    /**
     * Set data to be saved
     * @param device - device to be save in data
     */
    public static void setData(BluetoothDevice device)
    {
    	dataToSave = device;
    }
}
