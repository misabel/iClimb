package com.example.android.iClimb;

import java.util.ArrayList;

import com.example.android.BluetoothChat.R;
import com.example.android.BluetoothChat.R.layout;
import com.example.android.BluetoothChat.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.RelativeLayout;

public class ConfigurationActivity extends Activity {

	RelativeLayout configRelativeLayout;
	ArrayList<Node> nodes = new ArrayList<Node>();//array list that will hold the nodes present on this activity
	Node node = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		
		configRelativeLayout = new RelativeLayout(this);
			
		
		//This section will transfer the nodes that were placed on the first page so that they can be displayed on the configurations page
		  ArrayList<Node> refNodes = SetupActivity.nodes;
	      for(int i = 0 ; i < refNodes.size() ; i++)
	      {
	    	  Node reference = refNodes.get(i);
	    	  node = new Node(this, node.getBefore(), reference.getAddress(), reference.getX(), reference.getY());
	    	  configRelativeLayout.addView(node);
	    	  nodes.add(node);
	      }
	      
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configuration, menu);
		return true;
	}

}
