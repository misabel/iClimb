package com.example.android.iClimb;

import java.util.ArrayList;
import java.util.Random;

import com.example.android.BluetoothChat.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class SetupActivity extends Activity {
	SetupActivity main = this;
	public static RelativeLayout setupRelativeLayout;
	public static RelativeLayout getSetupRelativeLayout() {
		return setupRelativeLayout;
	}
	RelativeLayout.LayoutParams relativeLayoutParameters;
	Node tb = null;
	MenuItem undoButton;
	
	public static ArrayList<Node> nodes = new ArrayList<Node>();
	int status;
    public static final int STOP_DRAGGING = 0;
    public static final int START_DRAGGING = 1;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupRelativeLayout = new RelativeLayout(this);
		relativeLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		
		setContentView(setupRelativeLayout, relativeLayoutParameters);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
					if(tb==null) undoButton.setEnabled(false);
				}
				
			break;
			
			case R.id.action_reset:
		
				Toast.makeText(this, "Reset Selected", Toast.LENGTH_SHORT).show();
				setupRelativeLayout.removeAllViews();
				tb = null;
				undoButton.setEnabled(false);
			break;
			
			case R.id.action_configure:
				
				while(tb!=null)
				{
					nodes.add(tb);
					tb = tb.getBefore();
				}
				
				Intent i=new Intent(SetupActivity.this, ClimbActivity.class);
				startActivity(i);
		        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		}
		
		return false;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			addToggleButton(event.getX(), event.getY());
		}
		return super.onTouchEvent(event);
	}
	
	Random random = new Random();
	int n; 
	private void addToggleButton(float x, float y)
	{
		if(!undoButton.isEnabled()) undoButton.setEnabled(true);
		
		Node temp = tb;
		tb = new Node(this, temp);
		tb.setX(x-75);
		tb.setY(y-200);
		
		n = random.nextInt(1000);
		tb.setAddress(Integer.toString(n));
		tb.setOnTouchListener(new View.OnTouchListener()
		 {

			@Override
			public boolean onTouch(View v, MotionEvent me) 
			{
				
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
						AddButtonLayout((Node)v, RelativeLayout.ALIGN_PARENT_LEFT, (int)me.getX(), (int)me.getY(), 0, 0);
						v.invalidate();
					}
				      
				}
				return false;
			}  
			
		  }); 
		setupRelativeLayout.addView(tb);
		Toast.makeText(this, tb.getAddress(), Toast.LENGTH_SHORT).show();
		
	}
	
	 /**
     * This method will place button on the Relative Layout of the app
     * @param button - Button to be placed on the view
     * @param centerInParent
     * @param marginLeft
     * @param marginTop
     * @param marginRight
     * @param marginBottom
     */
    public void AddButtonLayout(Button button, int centerInParent, int marginLeft, int marginTop, int marginRight, int marginBottom) 
    {
        RelativeLayout.LayoutParams buttonLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParameters.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        buttonLayoutParameters.addRule(centerInParent);
        
        button.setLayoutParams(buttonLayoutParameters);     
    }

}
