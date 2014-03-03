package com.example.android.BluetoothChat;

import android.content.Context;
import android.widget.ToggleButton;

public class CustomButton extends ToggleButton {
	CustomButton b;
	CustomButton(Context c)
	{
		super(c);
		
	}
	
	public CustomButton getBefore()
	{
		return b;
	}
	
	public void setBefore(CustomButton tb)
	{
		b = tb;
	}
}
