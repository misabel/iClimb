package com.example.android.iClimb;
import com.example.android.BluetoothChat.R;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

/**
 *
 * @author mariairizarry
 *This class represents each hold on the interface
 */
public class Node extends ToggleButton
{
	private Node nodeBefore;
	private String address;
	private boolean checked;
	private String color;
	private int icon;
	
	/**
	 *
	 * @param c - Context of page that Node is being created in.
	 * @param n - Node that comes before this current one.
	 */
	Node(Context c, Node n)
	{
		super(c);
		nodeBefore = n;
		configuration();
		address = null;
	}
	
	/**
	 *
	 * @param c - Context of page that Node is being created in.
	 * @param n - Node that comes before this current one.
	 * @param addr - Address of this node.
	 * @param x - Horizontal coordinate of Node.
	 * @param y - Vertical coordinate of Node.
	 */
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
	
	/**
	 * Private method that configures each Node created to not have any text, and modifies its layout.
	 */
	private void configuration()
	{
		checked = false;
		this.setChecked(checked);
		this.setText(null);
		this.setTextOn(null);
		this.setTextOff(null);
        
		this.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
	}
	
	/**
	 * Get method for icon
	 * @return Current icon set to Node.
	 */
	public int getIcon()
	{
		return icon;
	}
	
	/**
	 * Sets icon
	 * @param i - Value of desired icon.
	 */
	public void setIcon(int i)
	{
		icon = i;
		this.setBackgroundResource(icon);
	}
	
	/**
	 * Sets color
	 * @param c - RGB value for desired color.
	 */
	public void setColor(String c)
	{
		color = c;
	}
	
	/**
	 * Get method for color.
	 * @return
	 */
	public  String getColor()
	{
		return color;
	}
	
	/**
	 * Turns Node on.
	 */
	public void turnOn()
	{
		this.setChecked(true);
		
	}
	
	/**
	 * Turns Node off.
	 */
	public void turnOff()
	{
		this.setChecked(false);
	}
	
	/**
	 * Checks whether Node is on or off.
	 * @return Boolean value of whether it is on or off.
	 */
	public boolean isOn()
	{
		return this.isChecked();
	}
	
	/**
	 * Set address to Node.
	 * @param addr - Desired address.
	 */
	public void setAddress(String addr)
	{
		address = addr;
	}
	
	/**
	 * Get method for address.
	 * @return Address string.
	 */
	public String getAddress()
	{
		return address;
	}
	
	/**
	 * Get method for Node that was before this one.
	 * @return Node
	 */
	public Node getBefore()
	{
		return nodeBefore;
	}
	
	/**
	 * Sets the given Node to be the one before the newly created one.
	 * @param tb - Node before.
	 */
	public void setBefore(Node tb)
	{
		nodeBefore = tb;
	}
    
}
