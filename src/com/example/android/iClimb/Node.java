package com.example.android.iClimb;


import com.example.android.BluetoothChat.R;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class Node extends ToggleButton {
	private Node nodeBefore;
	private String address;
	private boolean checked;
	private String color;
	private int icon;
	Node(Context c, Node n)
	{
		super(c);
		nodeBefore = n;
		configuration();
	}
	
	Node(Context c, Node n, String addr, float x, float y)
	{
		super(c);
		nodeBefore = n;
		this.setX(x);
		this.setY(y);
		address = addr;
		this.setBackgroundResource(R.drawable.gray_hold);
		configuration();
	}
	
	private void configuration()
	{
		checked = false;
		this.setChecked(checked);
		this.setText(null);
		this.setTextOn(null);
		this.setTextOff(null);

		this.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public void setIcon(int i)
	{
		icon = i;
		this.setBackgroundResource(icon);
	}
	public void setColor(String c)
	{
		color = c;
	}
	
	public  String getColor()
	{
		return color;
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
