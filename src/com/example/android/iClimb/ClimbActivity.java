/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.iClimb;


import java.util.ArrayList;

import com.example.android.BluetoothChat.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the Climbing Activity that displays the virtual climbing wall, 
 * and is the main user interface through which the user will interact with 
 * the wall, create their routes, save/delete routes, and illuminate the
 * holds they wish to climb. The class communicates with the hub via 
 * bluetooth, and thus contains a handler that relays messages between
 * this class and the bluetoothConnection class.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({ "InlinedApi", "HandlerLeak" })
public class ClimbActivity extends Activity {
	
	// Debugging
    //private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    
    private int currColor = 0;
    private String[] diffColors = { "White", "Red", "Yellow", "Green", "Cyan", "Blue", "Violet" };
    private int[] holdIcons = { R.drawable.white_hold, R.drawable.red_hold, R.drawable.yellow_hold, R.drawable.green_hold, R.drawable.cyan_hold, R.drawable.blue_hold, R.drawable.violet_hold};
	private String[] rgbEquiv = { "255 255 255", "255 0 0 ", "255 255 0", "0 255 0", "0 255 255", "0 0 255", "255 0 255" };
    private MenuItem drawButton;
    private int[] colorIcons = {R.drawable.ic_action_edit,        R.drawable.ic_action_edit_red, 
    					 R.drawable.ic_action_edit_yellow, R.drawable.ic_action_edit_green, 
    					 R.drawable.ic_action_edit_cyan,   R.drawable.ic_action_edit_blue,
    					 R.drawable.ic_action_edit_purple};
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int STOP_DRAGGING = 0;
    public static final int START_DRAGGING = 1;
    
    //Bluetooth commands
    public static final String ILLUMINATE_NODE = "illNode";
    public static final String ILLUMINATE_ROUTE = "illRoute";
    public static final String SAVE_ROUTE = "saveRoute";
    public static final String DELETE_ROUTE = "deleteRoute";

    
    //store incoming bluetooth message
    public String readMessage;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    //private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothConnection mChatService = null;
    private String origMessage;
    
    RelativeLayout mainRelativeLayout;
	RelativeLayout.LayoutParams relativeLayoutParameters;
	Node tb = null;
	int status;
	ClimbActivity cmain = this;
	StringBuilder sBuilder;
	//DragEventListener dragListen = new DragEventListener();
	private static final String TAG = "Climb Activity";

	private ArrayList<Node> nodes = new ArrayList<Node>();
	private Node node;
	private ArrayList<Node> routeToDisplay;
	private boolean routeLoaded;
	
    @Override
    /**
     * When page is first loaded, this method is called
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	mainRelativeLayout = new RelativeLayout(this);
		
        node = new Node(this, null);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        
        this.setTitle("Climbing " + Wall.getName());
        relativeLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        
        setContentView(mainRelativeLayout, relativeLayoutParameters);
        Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.background);
		//drawable.setAlpha(125);
		mainRelativeLayout.setBackgroundDrawable(drawable);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish(); 
            return;
        }
        
        routeLoaded = false;
        
       
        ArrayList<Node> refNodes = new ArrayList<Node>(Wall.getAllNodes().values());
        Log.d(TAG, "REF NODES LENGTH IS: " + refNodes.size());
        for(int i = 0 ; i < refNodes.size() ; i++)
        {
        	Node reference = refNodes.get(i);
        	node = new Node(this, node.getBefore(), reference.getAddress(), reference.getX(), reference.getY());
        	node.setIcon(R.drawable.gray_hold);
    		Log.d(TAG, "CURRENT NODE IS: " + node.getAddress() + "with x y location of : " +node.getX()+  " " +node.getY());

        	node.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton button, boolean isChecked) {
					
						Node n = (Node)button;
						if(isChecked)
						{
							n.setColor(rgbEquiv[currColor]);
							n.setIcon(holdIcons[currColor]);
						}
						
						else
						{
							n.setColor("0 0 0");
							n.setIcon(R.drawable.gray_hold);
							
						}
							
						if(!routeLoaded)
						{
							illuminateNode(n);
						}

				}
			});
        	mainRelativeLayout.addView(node);
        	nodes.add(node);
        	
        	
        }
        Wall.saveNodes(nodes);
        
        Log.d(TAG, "Attempting connection");
        //BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("00:06:66:67:57:D7");
        if (mChatService == null) setupChat();
        mChatService.connect(Wall.getData(), true);
        
    }
    

    
    @Override
    /*
     * Called when Action Bar is created
     */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        drawButton = menu.findItem(R.id.color_select);
        
        return true;
    }

    EditText routeNameField;
    Route route;
    Dialog routeNameDialog, routeListDialog;
    
  
    RouteListAdapter adapter;
    @Override
    /*
     * This method is called when one of the options on the Action Bar is selected
     */
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        Intent serverIntent = null;
        switch (item.getItemId())
        {
	        case R.id.secure_connect_scan:
	            // Launch the DeviceListActivity to see devices and do scan
	            serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
	            return true;
	        case R.id.color_select: // Button that goes through all the colors that user can select
				currColor++;
				if( currColor > 6 ) 
				{
					currColor = 0;
				} // end IF
				drawButton.setIcon(colorIcons[currColor]);
				Toast.makeText(this, diffColors[currColor] + " selected!", Toast.LENGTH_SHORT).show();
				
				break;
				
	        case R.id.action_save:
	        	int count = 0;
	        	
	        	if(count>0)
	        	{
	        		AlertDialog.Builder warning =  new AlertDialog.Builder(cmain);
	            	warning.setTitle("No holds are selected to be saved.");
	            	warning.setIcon(R.drawable.ic_no_routes);
	            	warning.setPositiveButton("OK", null);
	            	warning.show();
	        	}
	        	
	        	else
		        {
			        	routeNameDialog = new Dialog(this);
			        	routeNameDialog.setContentView(R.layout.route_name_window);
			        	routeNameDialog.setTitle("Name Your Route");
			        	Button okButton = (Button)routeNameDialog.findViewById(R.id.ok_button);
			            routeNameField = (EditText)routeNameDialog.findViewById(R.id.route_name_tf);
			
			        	okButton.setOnClickListener(new OnClickListener()
			        	{
							
							@Override
							public void onClick(View arg0)
							{
								
								route = new Route(routeNameField.getText ().toString());
					        	for(int i = 0 ; i < nodes.size() ; i++)
					        	{
					        		if(nodes.get(i).isChecked())
						        	{ 
						        		Node currNode = nodes.get(i);
						        		currNode.setIcon(nodes.get(i).getIcon());
						        		route.addNode(currNode);
					        		}
					        	}
					        
						        	//Wall.saveRoute(route);
						        	sendMessage("saveRoute\n" +route.getName());
						        	routeNameDialog.hide();
					        	
							}
						});
			        	
			        	routeNameDialog.show();
	        	}
	        	break;
	        	
	        case R.id.action_load:
	        	
	        	if(Wall.getRoutes().size()==0)
	        	{
	
	            	AlertDialog.Builder warning =  new AlertDialog.Builder(this);
	            	warning.setTitle("No routes are currently saved.");
	            	warning.setIcon(R.drawable.ic_no_routes);
	            	warning.setPositiveButton("OK", null);
	            	warning.show();
	        	}
	        	
	        	else
		        {
		        	routeListDialog = new Dialog(this);
		        	
		        	routeListDialog.setContentView(R.layout.route_list);
		        	adapter = new RouteListAdapter(this, R.layout.route_item, Wall.getRoutes());
		        	ListView routeList = (ListView)routeListDialog.findViewById(R.id.route_list);
		        	routeList.setAdapter(adapter);
		        	
		        	routeListDialog.setTitle("Saved Routes");
		        	routeListDialog.show();
	        	}
	        break;
	        
	        case R.id.turn_off:
	        	for(int i = 0; i< nodes.size(); i++)
	        	{
	        		nodes.get(i).turnOff();
	        	}
	        	Node s = new Node(getApplicationContext(), null);
	        	s.setAddress("0");
	        	s.setColor("0 0 0");
	        	illuminateNode(s);
	        	SystemClock.sleep(100);
	        	illuminateNode(s);
	        	break;
        }
        
        return false;
    }
    Route routeToRemove;
    public void deleteRoute(View v)
    {
    	routeToRemove = (Route)v.getTag();
    	
    	AlertDialog.Builder warning =  new AlertDialog.Builder(this);
    	warning.setTitle("Are you sure you wish to delete \"" + routeToRemove.getName() + "\"?");
    	warning.setIcon(R.drawable.ic_delete_route);
    	warning.setPositiveButton("OK", new DialogInterface.OnClickListener() 
    	{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				adapter.remove(routeToRemove);
				Wall.getNodes().remove(routeToRemove);
				deleteRoute(routeToRemove);
			}
		});
    	warning.setNegativeButton("Cancel", null);
    	warning.show();
    	
    	
    }
    
    public void loadRoute(View v)
    {
    	Route routeToLoad = (Route)v.getTag();
		routeLoaded = true;

		for(int i = 0; i<nodes.size(); i++)
		{
			for(int j = 0; j< routeToLoad.getNodes().size(); j++)
			{
				
				if( nodes.get(i).getAddress().compareTo(routeToLoad.getNodes().get(j).getAddress())==0)
				{	
					nodes.get(i).turnOn();
					nodes.get(i).setIcon(holdIcons[currColor]);
					
					
					break;
				}
				
				else 
				{
					nodes.get(i).turnOff();
				}
			}
		
		}
		illuminateRoute(routeToLoad);
		
		routeListDialog.hide();
    }
	
    @Override
    public void onStart() 
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) 
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } 
        
        else
        {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume()
    {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) 
        {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothConnection.STATE_NONE)
            {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() 
    {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        //mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothConnection(this, mHandler);


        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause()
    {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() 
    {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
	private void ensureDiscoverable()
    {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) 
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    public void sendMessage(String message)
    {
    	SystemClock.sleep(100);
    	readMessage = null;
    	origMessage = null;
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothConnection.STATE_CONNECTED)
        {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0)
        {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
           // mOutEditText.setText(mOutStringBuffer);
        }
    }

 

    private final void setStatus(int resId)
    {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle)
    {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
        
        if(((String) actionBar.getSubtitle()).contains("not connected")){
        	mChatService.connect(Wall.getData(), true);
        }
    }

    /**
     * The Handler that gets information back from the BluetoothCommunication class 
     * Handles the bluetooth communication between this class and bluetoothConnection class
     */
    private final Handler mHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) 
        {
            switch (msg.what) 
            {
	            case MESSAGE_STATE_CHANGE:
	                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                switch (msg.arg1) 
	                {
		                case BluetoothConnection.STATE_CONNECTED:
		                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
		                    break;
		                    
		                case BluetoothConnection.STATE_CONNECTING:
		                    setStatus(R.string.title_connecting);
		                    break;
		                    
		                case BluetoothConnection.STATE_LISTEN:
		                	SystemClock.sleep(500);
		                	mChatService.connect(Wall.getData(), true);
		                    setStatus(R.string.title_not_connected);
		                    break;
		                    
		                case BluetoothConnection.STATE_NONE:
		                    break;
	                }
	                break;
	                
	            case MESSAGE_WRITE:
	                byte[] writeBuf = (byte[]) msg.obj;
	                // construct a string from the buffer
	                String writeMessage = new String(writeBuf);
	                Log.d(TAG, "Writting: " + writeMessage);

	                break;
	                
	            case MESSAGE_READ:
	                byte[] readBuf = (byte[]) msg.obj;
	                // construct a string from the valid bytes in the buffer
	                readMessage = new String(readBuf, 0, msg.arg1);
	           	    if (readMessage == null){
	           	    	readMessage = "";
	           	    }
	           	    readMessage = readMessage + origMessage;
	           	    if (readMessage.contains("<")){
	           	    	Log.d(TAG, "Received: " + readMessage);
		                
		                if(readMessage.contains("saveRoute"))
		                {
		                	saveRoute(route);
		                }
		                else if(readMessage.contains("illuminated")){
		                	routeLoaded = false;
		                }
	           	    }
	                break;
	                
	            case MESSAGE_DEVICE_NAME:
	                // save the connected device's name
	                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
	                Toast.makeText(getApplicationContext(), "Connected to "
	                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	                break;
	                
	            case MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
	                               Toast.LENGTH_SHORT).show();
	                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode)
        {
	        case REQUEST_CONNECT_DEVICE_SECURE:
	            // When DeviceListActivity returns with a device to connect
	            if (resultCode == Activity.RESULT_OK) 
	            {
	                connectDevice(data, true);
	            }
	            break;
	        case REQUEST_ENABLE_BT:
	            // When the request to enable Bluetooth returns
	            if (resultCode == Activity.RESULT_OK)
	            {
	                // Bluetooth is now enabled, so set up a chat session
	                setupChat();
	            } 
	            
	            else
	            {
	                // User did not enable Bluetooth or an error occurred
	                Log.d(TAG, "BT not enabled");
	                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
	                finish();
	            }
        }
    }

    private void connectDevice(Intent data, boolean secure) 
    {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
    
    /**
     * BLuetooth command to illuminateNode sends a message to hub with
     * node address, and the user's selected color to light the appropriate
     * hold
    */
    private void illuminateNode(Node n)
    {
    	sendMessage (ILLUMINATE_NODE + "\n" + n.getAddress()+ " " + n.getColor());
    }

    /**
     * BLuetooth command to illuminateRoute sends a message to hub with
     * user's selected route id, and the user's selected in which to illuminate
     * the route
    */
    private void illuminateRoute(Route r)
    {
    	sendMessage (ILLUMINATE_ROUTE + "\n" + r.getID()+ " " + rgbEquiv[currColor] );
    }
    
    /**
     * BLuetooth command to DeleteRoute sends a message to hub with
     * user's selected route to delete
    */
    private void deleteRoute (Route r)
    {
    	sendMessage(DELETE_ROUTE + "\n" + r.getID());
    }
    
    /**
     * BLuetooth command to Save a Route sends a message to hub with
     * all node addresses saved in the user's currently selected route
     * for the hub to save.
    */
    private void saveRoute (Route r){
    	if (readMessage.contains("yes"))
    	{
    		//r.setID(InteaerWall.getRoutes().size());
    		String [] parts = readMessage.split("\n");
    		r.setID(parts[2]);
    		Wall.saveRoute(r);
    		
        	Toast.makeText(cmain, "Path has been saved", Toast.LENGTH_SHORT).show();
        	ArrayList<Node> route = r.getNodes();
        	String addresses = "";
        	for (int i = 0; i<route.size(); i++ ){
        		addresses+= route.get(i).getAddress() + " ";
        		
        	}
        	sendMessage(addresses);
    	}
    	else if (readMessage.contains("no"))
    	{
        	Toast.makeText(cmain, "Unable to save Path", Toast.LENGTH_SHORT).show();
    		
    	}
    }
    

}
