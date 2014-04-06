package com.example.android.iClimb;

import java.util.ArrayList;

public class Wall 
{
	
	private static ArrayList<Route> routes = new ArrayList<Route>();
	private static ArrayList<String> routeNames = new ArrayList<String>();
	private static ArrayList<Node> allNodes = new ArrayList<Node>();
	private static String name;
	public static int numNodes = 0;
	public static int numPaths = 0;
	
	public String getName()
	{
		return name;
	}
	
	public void setWallName(String n)
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
		allNodes = nodes;
	}
	public static ArrayList<Node> getNodes()
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
	public static void setNumNodes(int nNodes){
		numNodes = nNodes;
	}
	public static int getNumNodes (){
		return numNodes;
	}
	public static void setNumPaths(int nPaths){
		numPaths = nPaths;
	}
	public static int getNumPaths(){
		return numPaths;
	}
}
