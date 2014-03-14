package com.example.android.iClimb;


import android.content.Context;
import android.widget.ToggleButton;

public class Node extends ToggleButton {
	private Node nodeBefore;
	private String address;
	private boolean checked;
	Node(Context c, Node n)
	{
		super(c);
		nodeBefore = n;
		checked = false;
		this.setChecked(checked);
	}
	
	Node(Context c, Node n, String addr, float x, float y)
	{
		super(c);
		nodeBefore = n;
		this.setX(x);
		this.setY(y);
		address = addr;
		
	}
	
	public void turnOn()
	{
		this.setChecked(true);
	}
	
	public void turnOff()
	{
		this.setChecked(false);
	}
	
	public boolean isOn()
	{
		return this.isChecked();
	}
	
	public void setAddress(String addr)
	{
		address = addr;
	}
	
	public String getAddress()
	{
		return address;
	}
	public Node getBefore()
	{
		return nodeBefore;
	}
	
	
	public void setBefore(Node tb)
	{
		nodeBefore = tb;
	}
		

	
	
}
