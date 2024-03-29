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
import java.util.Stack;

import com.example.android.BluetoothChat.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View.OnTouchListener;


/**
 * This class holds a conversation with the hub during which it connects
 * the x.y locations of all holds placed by the user during the setup process
 * with the appropriate node addresses associated with the nodes on the hub.
 * The class communicates with the hub via bluetooth, and thus contains a 
 * handler that relays messages between this class and the bluetoothConnection class.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({ "InlinedApi", "HandlerLeak" })
public class ConfigurationActivity extends Activity {
	
	// Debugging
    //private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int STOP_DRAGGING = 0;
    public static final int START_DRAGGING = 1;
    public static final int NUM_NODES = Wall.getNumNodes();
    
    //store incoming bluetooth message
    public String readMessage;
    public String origMessage;
    public String messageToSend;
    
    //counter of configured nodes
    public int nodesConfigured = 0;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothConnection mChatService = null;
    
    RelativeLayout configRelativeLayout;
	RelativeLayout.LayoutParams relativeLayoutParameters;

	private static final String TAG = "Configuration Activity";

	private ArrayList<Node> nodes = new ArrayList<Node>();
	private Node node;
	private String addressToAssign = null;
	
	//menu buttons
	MenuItem configButton;
	MenuItem undoButton;
	MenuItem climbButton;

	//Stack of assigned addresses
	private Stack<Node> assignedNodes = new Stack<Node>();
	
    @SuppressWarnings("deprecation")
	@Override
    /**
     * When page is first loaded, this method is called
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	configRelativeLayout = new RelativeLayout(this);
		
        node = new Node(this, null);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        relativeLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        
        setContentView(configRelativeLayout, relativeLayoutParameters);
        Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.background);
		//drawable.setAlpha(125);
		configRelativeLayout.setBackgroundDrawable(drawable);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }        
        
        setnodes();
        
        
        Log.d(TAG, "Attempting connection");
        //BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("00:06:66:67:57:D7");
        if (mChatService == null) setupChat();
        mChatService.connect(Wall.getData(), true);
       
    }
    
	/**
     * This Method will call all saved nodes and will add an ontouch listener to each node that will respond to each touch by saving
     * the current address to the touched node and sending a message to the hub requesting the next address be sent.
     * */
    
    Node currNode;
    private void setnodes()
    {
    	
    	ArrayList<Node> refNodes = Wall.getNodes();
        //ArrayList<Node> refNodes = new ArrayList<Node>(Wall.getAllNodes().values());
        for(int i = 0 ; i < refNodes.size() ; i++)
        {
        	Node reference = refNodes.get(i);
        	node = new Node(this, node.getBefore(), null, reference.getX(), reference.getY());
        	
        	node.setOnTouchListener(new OnTouchListener() 
        	{

                public boolean onTouch(View v, MotionEvent event)
                {
                    int action = event.getAction();
                    currNode = (Node) v;
                    currNode.setIcon(R.drawable.red_hold);
                    //if node selected
                    if (action == MotionEvent.ACTION_DOWN ) 
                    {
                    	//if button pushed do nothing, wait for release
                    }
                    
                    //else if node is released
                    else if (action == MotionEvent.ACTION_UP )
                    {
                    	if (addressToAssign!= null){
                    		if (currNode.getAddress() == null)
                    		{
                    			//currNode.setIcon(R.drawable.gray_hold);
                    			currNode.setAddress(addressToAssign);
        			        	Log.d(TAG, "SETTING ADDRESS TO: " + currNode.getAddress());
                    			Wall.mapNode(currNode);
        			        	undoButton.setEnabled(true);
        			        	readMessage = null;
        			        	addressToAssign = null;
        			        	sendMessage("setXY\n" + currNode.getX() +" "+ currNode.getY());

                    		}
                    		else
                    		{
                    			//Double check!!!!!!!!!!!!!!!!!!!!!!!
                    			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfigurationActivity.this);
                    	 
                    				// set title
                    				alertDialogBuilder.setTitle("Hold already Assigned");
                    	 
                    				// set dialog message
                    				alertDialogBuilder
                    					.setMessage("This hold has already been assigned, would you like to overwrite old assignment?")
                    					.setCancelable(false)
                    					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    						public void onClick(DialogInterface dialog,int id) {
                    							//Yes Button Clicked
                    							
                                    			currNode.setAddress(addressToAssign);
                        			        	Log.d(TAG, "SETTING ADDRESS TO: " + currNode.getAddress());
                                    			Wall.mapNode(currNode);
                        			        	undoButton.setEnabled(true);
                        			        	readMessage = null;
                        			        	addressToAssign = null;
                        			        	sendMessage("setXY\n" + currNode.getX() +" "+ currNode.getY());
                    						}
                    					  })
                    					.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    						public void onClick(DialogInterface dialog,int id) {
                    							// if this button is clicked, just close
                    							// the dialog box and do nothing
                    							dialog.cancel();
                    						}
                    					});
                    	 
                    					// create alert dialog
                    					AlertDialog alertDialog = alertDialogBuilder.create();
                    	 
                    					// show it
                    					alertDialog.show();
                    		}//end else
                    	}//end if message pending
                    }

                    return false;
                }

            });
        	
        	//add nodes to layout
	    	configRelativeLayout.addView(node);
	    	//save nodes in node class
        	nodes.add(node);
        }
        //save all added nodes to wall class
        Wall.saveNodes(nodes);
    }
    
    @Override

    /**
     * Retrieves menu options to create menu bar 
     * */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.configuration, menu);
		configButton = menu.findItem(R.id.action_start_config);
		configButton.setEnabled(false);
		
		undoButton = menu.findItem(R.id.action_undo);
		undoButton.setEnabled(false);
		
		climbButton = menu.findItem(R.id.action_climb);
		climbButton.setEnabled(false);
		
        return true;
    }
    
    @Override

    /**
     * handles selections made from the menu bar
     * */
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        Intent serverIntent = null;
        switch (item.getItemId()) {
	        case R.id.secure_connect_scan:
	            // Launch the DeviceListActivity to see devices and do scan
	            serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
	            return true;
	        case R.id.action_undo:
	        	climbButton.setEnabled(false);
	        	Wall.getMappedNode(currNode.getAddress()).setIcon(R.drawable.red_hold);
	        	Wall.removeNode(Wall.getMappedNode(currNode.getAddress()));
	        	currNode.setAddress(null);
	        	nodesConfigured--;
	        	assignedNodes.pop();
	        	//currNode = assignedNodes.peek();
	        	if(assignedNodes.isEmpty() ){
	        		undoButton.setEnabled(false);
	        	}else{
		        	currNode = assignedNodes.peek();
	        	}
	        	sendMessage("undo");
			break;
			case R.id.action_climb:
				sendMessage("startClimb");
				
				Intent switchView = new Intent(this, ClimbActivity.class);
				startActivity(switchView);
		        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		        finish();
			break;
			case R.id.action_start_config:
				sendMessage("startConfig");
				configButton.setEnabled(false);
			break;
        }
        return false;
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
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothConnection(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
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
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) 
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
    	readMessage = null;
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

    /**
     * Sets bluetooth connection device.
     * @param resID id of connected device.
     */
    private final void setStatus(int resId) 
    {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    /**
     * Sets bluetooth connection status.
     * @param subTitle the status of connection message.
     */
    private final void setStatus(CharSequence subTitle)
    {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
        
        if(((String) actionBar.getSubtitle()).contains("not connected")){
        	mChatService.connect(Wall.getData(), true);
        }
        
        if (subTitle.equals(getString(R.string.title_connected_to, mConnectedDeviceName))){
        	sendMessage("setWallName\n " + Wall.getName());
            Log.d(TAG, "sent setWallName");
            //Toast.makeText(getApplicationContext(), "Sent Hello :D",  Toast.LENGTH_SHORT).show();
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
		                    mConversationArrayAdapter.clear();
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
                @SuppressWarnings("unused")
				String writeMessage = new String(writeBuf);
                Log.d(TAG, "Writing: " + writeMessage);
                break;
                
            case MESSAGE_READ:
            	byte[] readBuf = (byte[]) msg.obj;
            	origMessage = new String(readBuf, 0, msg.arg1);
           	    SystemClock.sleep(500);
           	    if (readMessage == null){
           	    	readMessage = "";
           	    }
           	    readMessage = readMessage + origMessage;
           	    if (readMessage.contains("<")){
           	    	Log.d(TAG, "Received: " + readMessage);
                    handleHubMessage(readMessage);
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
    
    /**
     * Gets called upon user's selection of a device to which to connect to, retreives device name
     * and information and attempts to make a connection
     */
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

    /**
     * Private class that gets called when the handler receives a message. Based on the
     * contents of the message, it will do the following: parse the message to retrieve
     * the necessary information, process the received data, and reply to the hub with 
     * the appropriate message to continue the 'conversation'.
     */
    private void handleHubMessage(String message){
    	if (message.contains("setWallName")){
        		configButton.setEnabled(true);
    	}
    	if (message.contains("Starting configuration")){
    		configButton.setEnabled(false);
    		sendMessage("nextAddress");
    	}
    	if (message.contains("nextAddress")){
        	String[] nodeAddress = readMessage.split("\\r?\\n");
        	addressToAssign = null;
        	addressToAssign = nodeAddress[nodeAddress.length-2];
    	}
    	if (message.contains("undo")){
        	String[] nodeAddress = readMessage.split("\\r?\\n");
        	addressToAssign = null;
        	addressToAssign = nodeAddress[nodeAddress.length-2];
    	}
    	if (message.contains("setXY")){
    		if(message.contains("yes")){
	        	Wall.getMappedNode(currNode.getAddress()).setIcon(R.drawable.green_hold);
				assignedNodes.push(currNode);
	        	nodesConfigured++;
				
    			if(nodesConfigured < Wall.getNumNodes()){;
        			sendMessage("nextAddress");

    			}else{
    				sendMessage("startClimb");
    			}
    		}

    	}//end setxy
    	if(message.contains("fun")){
    		if(nodesConfigured == Wall.getNumNodes()){
        		climbButton.setEnabled(true);
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

}
