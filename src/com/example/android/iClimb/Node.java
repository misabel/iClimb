package com.example.android.iClimb;

import android.content.Context;
import android.widget.ToggleButton;

public class Node extends ToggleButton {
	Node b;
	Node(Context c)
	{
		super(c);
	}
	
	public Node getBefore()
	{
		return b;
	}
	
	
	public void setBefore(Node tb)
	{
		b = tb;
	}
	
	
}
