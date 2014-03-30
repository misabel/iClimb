package com.example.android.iClimb;

import java.util.ArrayList;

public class Wall 
{
	
	private static ArrayList<Route> routes = new ArrayList<Route>();
	private static ArrayList<String> routeNames = new ArrayList<String>();
	private static ArrayList<Node> allNodes = new ArrayList<Node>();
	
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
}
