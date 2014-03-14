package com.example.android.iClimb;

import java.util.ArrayList;

public class Wall 
{
	private static ArrayList<Route> routes = new ArrayList<Route>();
	private static ArrayList<String> routeNames = new ArrayList<String>();
	
	public static void saveRoute(Route p)
	{
		routes.add(p);
		routeNames.add(p.getName());
	}
	
	
	public static ArrayList<Route> getRoutes()
	{
		return routes;
	}
	
	public static ArrayList<Route> loadPaths()
	{
		return routes;
	}
	
	public static ArrayList<String> getRoutNames()
	{
		return routeNames;
	}
}
