package com.example.android.iClimb;

import java.util.ArrayList;

import com.example.android.BluetoothChat.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SetupActivity extends Activity
{
	SetupActivity main = this;
	public static RelativeLayout setupRelativeLayout;
	
	public static RelativeLayout getSetupRelativeLayout()
	{
		return setupRelativeLayout;
	}
	
	RelativeLayout.LayoutParams relativeLayoutParameters;
	Node tb = null;
	MenuItem undoButton;
	int MAX_NODES = 10;
	int numNodes; 
	
	public static ArrayList<Node> nodes = new ArrayList<Node>();
	int status;
    public static final int STOP_DRAGGING = 0;
    public static final int START_DRAGGING = 1;
    
    Dialog wallNameDialog;
    EditText wallNameField;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		numNodes = 0;
		setupRelativeLayout = new RelativeLayout(this);
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.background);
		drawable.setAlpha(125);
		setupRelativeLayout.setBackgroundDrawable(drawable);
		relativeLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		
		setContentView(setupRelativeLayout, relativeLayoutParameters);
		
		wallNameDialog = new Dialog(this);
    	wallNameDialog.setContentView(R.layout.route_name_window);
    	wallNameDialog.setTitle("Name Your Wall");
    	Button okButton = (Button)wallNameDialog.findViewById(R.id.ok_button);
        wallNameField = (EditText)wallNameDialog.findViewById(R.id.route_name_tf);

    	okButton.setOnClickListener(new OnClickListener() 
    	{
			
			@Override
			public void onClick(View arg0) 
			{
				
				Wall.setWallName(wallNameField.getText().toString());
		        wallNameDialog.hide();
	        	
			}
		});
    	
    	wallNameDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
		undoButton = menu.findItem(R.id.undo);
		undoButton.setEnabled(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.undo:
				
				if(tb!=null)
				{
					Node toRemove = tb;
					tb = toRemove.getBefore();
					setupRelativeLayout.removeView(toRemove);
					numNodes--;
					if(tb==null) undoButton.setEnabled(false);
				}
				
			break;
			
			case R.id.action_reset:
				Toast.makeText(this, "Reset Selected", Toast.LENGTH_SHORT).show();
				setupRelativeLayout.removeAllViews();
				tb = null;
				numNodes = 0;
				undoButton.setEnabled(false);
			break;
			
			case R.id.action_configure:
				
				while(tb!=null)
				{
					nodes.add(tb);
					tb = tb.getBefore();
				}

				
				Intent i=new Intent(SetupActivity.this, ConfigurationActivity.class);
				Wall.saveNodes(nodes);
				startActivity(i);
		        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		        finish();
		        
			break;
		}
		
		return false;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if(numNodes < Wall.getNumNodes())
			{
				addToggleButton(event.getX(), event.getY());
				Toast.makeText(this, "X: " + event.getX() + "Y: " + event.getY() , Toast.LENGTH_SHORT).show();
				numNodes++;
			}
			else 
			{
				AlertDialog.Builder warning =  new AlertDialog.Builder(main);
            	warning.setTitle("The maximum amount of holds have been reached.");
            	warning.setIcon(R.drawable.ic_no_routes);
            	warning.setPositiveButton("OK", null);
            	warning.show();
			}
		
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
		return super.onTouchEvent(event);
	}
	

	private void addToggleButton(float x, float y)
	{
		if(!undoButton.isEnabled()) undoButton.setEnabled(true);
		
		Node temp = tb;
		tb = new Node(this, temp);
		tb.setX(x-75);
		tb.setY(y-200);
		
		tb.setAddress(null);
		tb.setOnTouchListener(new View.OnTouchListener()
		 {

			@Override
			public boolean onTouch(View v, MotionEvent me) 
			{
				float x,y=0.0f;
				
				if(me.getAction() == MotionEvent.ACTION_DOWN)
				{
					status = START_DRAGGING;
				}
				
				if(me.getAction() == MotionEvent.ACTION_UP)
				{
					status = STOP_DRAGGING;
				}
		
				else if(me.getAction() == MotionEvent.ACTION_MOVE)
				{
					if(status == START_DRAGGING)
					{
						x = me.getRawX()-v.getWidth()/2;
						y = me.getRawY()-v.getHeight()*3/2-125;
						v.setX(x);
						v.setY(y);
					}
				      
				}
				return false;
			}  
			
		  }); 
		tb.setIcon(R.drawable.gray_hold);
		setupRelativeLayout.addView(tb);
		Toast.makeText(this, tb.getAddress(), Toast.LENGTH_SHORT).show();
		
	}
}
