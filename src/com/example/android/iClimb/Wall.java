package com.example.android.iClimb;

import java.util.ArrayList;
import java.util.HashMap;

public class Wall 
{
	
	private static ArrayList<Route> routes = new ArrayList<Route>();
	private static ArrayList<String> routeNames = new ArrayList<String>();
	private static HashMap<String, Node> allNodes = new HashMap<String, Node>();
	private static String name;
	public static int numNodes = 0;
	public static int numRoutes = 0;
	
	public String getName()
	{
		return name;
	}
	
	public static void setWallName(String n)
	{
		name = n;
	}
	
	public static void saveRoute(Route p)
	{
		routes.add(p);
		routeNames.add(p.getName());
	}
	
	public static void saveNodes(ArrayList<Node> nodes)
	{
		allNodes.clear();
		for(int i = 0; i<nodes.size(); i++)
		{
			allNodes.put(nodes.get(i).getAddress(), nodes.get(i));
		}
	}
	
	public static HashMap<String, Node> getNodes()
	{
		return allNodes;
	}
	
	public static ArrayList<Route> getRoutes()
	{
		return routes;
	}
	
	public static ArrayList<Route> loadPaths()
	{
		return routes;
	}
	
	public static ArrayList<String> getRouteNames()
	{
		return routeNames;
	}
	
	public static void setNumNodes(int nNodes)
	{
		numNodes = nNodes;
	}
	
	public static int getNumNodes ()
	{
		return numNodes;
	}
	
	public static void setNumRoutes(int nPaths)
	{
		numRoutes = nPaths;
	}
	
	public static int getNumRoutes()
	{
		return numRoutes;
	}
}
