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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({ "InlinedApi", "HandlerLeak" })
public class SplashScreen extends Activity {
	
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
    
    //store incoming bluetooth message
    public String readMessage;
    private String messageToSend;
    
    //Wall information 
    public String wallName;
    public int numNodes = 0;
    public int numRoutes = 0;
    
    //Bluetooth conversation with hub states
    public static final int HELLO = 1;
    public static final int CONFIG = 2;
    public static final int WALL_NAME = 3;
    public static final int NUM_NODES = 4;
    public static final int NEXT_NODE = 5;
    public static final int NUM_ROUTES = 6;
    public static final int NEXT_ROUTE = 7;
    public static final int START_CLIMB = 8;
    
    //Expected hub responces
    public static final String M_HELLO = "hello";
    public static final String M_CONFIG = "config";
    public static final String M_WALL_NAME = "wallName";
    public static final String M_NUM_NODES = "#nodes";
    public static final String M_NEXT_NODE = "nextNode";
    public static final String M_NUM_ROUTES = "#routes";
    public static final String M_NEXT_ROUTE = "nextRoute";
    public static final String M_START_CLIMB = "fun";
    public static final String M_RESEND = "resend";
    
    //state of conversation
    public int conversation_state = 0;

    //counters
    int nodeCount = 0;
    int routeCount = 0;
    
    boolean nodesCalled = false;
    boolean pathsCalled = false;
    boolean configured = false;

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
    
    RelativeLayout splashRelativeLayout;
	RelativeLayout.LayoutParams relativeLayoutParameters;
	Node tb = null;
	int status;
	SplashScreen cmain = this;
	StringBuilder sBuilder;
	//DragEventListener dragListen = new DragEventListener();
	private static final String TAG = "z";

	private ArrayList<Node> nodes = new ArrayList<Node>();
	private Node node;
	ArrayList<Node> routeToDisplay;
	
	ImageView loadingIcon;
	AnimationDrawable loadingAnim;
    @Override
    /**
     * When page is first loaded, this method is called
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up the window layout
        setContentView(R.layout.activity_splash);
        splashRelativeLayout = new RelativeLayout(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        loadingIcon = (ImageView) findViewById(R.id.loading_hold);
        loadingIcon.setVisibility(View.GONE);
        //loadingIcon.setVisibility(View.GONE);
        loadingIcon.setBackgroundResource(R.anim.hold_loading);
        loadingAnim = (AnimationDrawable) loadingIcon.getBackground();
        loadingIcon.post(new Starter());
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);   
        //Toast.makeText(getApplicationContext(), "just got done on creating!",  Toast.LENGTH_SHORT).show();

       
    }
    
    class Starter implements Runnable
    {
    	public void run()
    	{
    		new Operation().execute("");
    	}
    }
    
    private class Operation extends AsyncTask<String, Void, String>
    {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
        protected void onPreExecute() {
        loadingIcon.setVisibility(View.VISIBLE);
        loadingAnim.start();
        }
    	 
		/*@Override
		protected void onPostExecute(String result)
		{
	        loadingIcon.setVisibility(View.GONE);
	        loadingAnim.stop();
		}*/
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.splash, menu);        
        return true;
    }
    
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
	        case R.id.secure_connect_scan:
	            // Launch the DeviceListActivity to see devices and do scan
	            serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
	            return true;
	        case R.id.discoverable:
	            // Ensure this device is discoverable by others
	            ensureDiscoverable();
	            return true;
        }
        return false;
    }

	
    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothConnection.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothConnection(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
       //if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
	private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothConnection.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
           // mOutEditText.setText(mOutStringBuffer);
        }
    }

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
        
        if (subTitle.equals(getString(R.string.title_connected_to, mConnectedDeviceName))){
        	sendMessage("hello");
        	conversation_state = HELLO;
            Log.d(TAG, "sent hello");
            //Toast.makeText(getApplicationContext(), "Sent Hello :D",  Toast.LENGTH_SHORT).show();
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothConnection.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothConnection.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothConnection.STATE_LISTEN:
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
                Log.d(TAG, "Writing: " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                readMessage = new String(readBuf, 0, msg.arg1);
           	    SystemClock.sleep(500);            
                Log.d(TAG, "Received: " + readMessage);
                bluetoothMessgeHandler(readMessage);
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

    public void attemptConnection(){
        Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE); 
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
    
    
    private void bluetoothMessgeHandler (String btMessage){
    	messageToSend = null;
    	if (btMessage.contains(M_HELLO)){
    		readMessage = null;
        	conversation_state = CONFIG;
        	//sendMessage("config?");
        	messageToSend = "config?";
            Log.d(TAG, "sent config?");
    	}
    	if (btMessage.contains(M_CONFIG) ){
    		if(readMessage.contains("yes")){
                Log.d(TAG, "Wall configured");
            	configured = true;
            	readMessage = null;
            	messageToSend = "wallName";
        	}
        	else if (readMessage.contains("no")){
                Log.d(TAG, "Wall not configured");
        		configured = false;
        		readMessage = null;
        		messageToSend = "wallName";
        	}
        	else{
        		messageToSend = "config?";
        	}
    		conversation_state = WALL_NAME;
    	}
    	if (btMessage.contains(M_WALL_NAME)){
    		String[] name = readMessage.split(":");
        	wallName = name[name.length-1];
            Log.d(TAG, "WALL NAME SET TO:" + wallName);
        	readMessage = null;
        	conversation_state = NUM_NODES;
       	    SystemClock.sleep(100);        	
       	    messageToSend = "#nodes";
    	}
    	if (btMessage.contains(M_NUM_NODES)){
        	String[] nodes = readMessage.split("\\r?\\n");
        	numNodes = Integer.parseInt(nodes[nodes.length-1]);
            Log.d(TAG, "num nodes set to: " + numNodes);
        	Wall.setNumNodes(numNodes);
        	//if the wall is configured initiallize the NEXTNODE conversation to receive nodes
        	if(configured){
        		if (numNodes > 0){
                	messageToSend = "nextNode";
                	nodeCount = 0;
                	conversation_state = NEXT_NODE;
        		}
        		else{
        			messageToSend = "#routes";
        			conversation_state = NUM_ROUTES;
        		}

            	
        	}
        	//if the wall is not configured, switch views to setup view
        	else{
        		readMessage = null;
        		// mChatService.stop();
				Intent switchToSetupView = new Intent(this, SetupActivity.class);
				startActivity(switchToSetupView);
				
		        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		      
		       // finish();
        	}

    	}
    	if (btMessage.contains(M_NEXT_NODE)){
        	String[] parts = readMessage.split("\n");
        	String[] nodeParts = parts[1].split(" ");
    		Node tn = null;
    		Node temp = tn;
    		temp = new Node (this, tn);
    		temp.setAddress((nodeParts[0]));
    		temp.setX(Float.parseFloat(nodeParts[1]));
    		temp.setY(Float.parseFloat(nodeParts[2]));
            Log.d(TAG, "Node set to: " + temp.getAddress()+" , " + temp.getX() +" , " + temp.getY());
    		nodes.add(temp);
    		readMessage = null;    		
    		if (nodeCount < numNodes-1){
        		conversation_state = NEXT_NODE;
    			messageToSend = "nextNode";
    		}
    		else if (nodeCount == numNodes-1){
    			conversation_state = NUM_ROUTES;
    			messageToSend = "#routes";
    		}
    		nodeCount++;
    		
    	}
    	if (btMessage.contains(M_NUM_ROUTES)){
    		String[] routes = readMessage.split("\n");
        	numRoutes = Integer.parseInt(routes[routes.length-1]);
            Log.d(TAG, "num routes set to: " + numRoutes);
        	Wall.setNumRoutes(numRoutes); 
    		if (numRoutes > 0){
            	messageToSend = "nextRoute";
            	routeCount = 0;
            	conversation_state = NEXT_ROUTE;
    		}
    		else{
    			messageToSend = "startClimb";
    			conversation_state = START_CLIMB;
    		}
    	}
    	if (btMessage.contains(M_NEXT_ROUTE)){
        	String[] routeParts = readMessage.split("\n");
        	Route route = new Route(routeParts[2]);
        	route.setID(routeParts[1]);
        	String[] routeNodes = routeParts[3].split(" ");
        	for(int i=0; i<routeNodes.length; i++){
        		Node tn = null;
        		Node temp = tn;
        		temp = new Node (this, tn);
        		temp.setAddress((routeNodes[i]));
        		route.addNode(temp);
                Log.d(TAG, "added Node" + temp.getAddress()+" to route " + route.getid() +"  named  " + route.getName());
        	}
        	Wall.saveRoute(route);
        	readMessage = null;    		
    		if (routeCount < numRoutes-1){
        		conversation_state = NUM_ROUTES;
    			messageToSend = "nextRoute";
    		}
    		else{
    			conversation_state = START_CLIMB;
    			messageToSend = "startClimb";
    		}
    		routeCount++;
    	}
    	if (btMessage.contains(M_START_CLIMB)){
    		readMessage = null;
			Intent switchToClimbView = new Intent(this, ClimbActivity.class);
			startActivity(switchToClimbView);
	        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	        mChatService.stop();
	       // finish();
    	}
    	if (btMessage.contains(M_RESEND)){
    		messageToSend = bluetoothConversation (conversation_state);
    	}
    	
    	if (messageToSend != null){
       	    SystemClock.sleep(200);
        	sendMessage(messageToSend);
            Log.d(TAG, "message sent: " + messageToSend);
    	}

    	
    }
    
    private String bluetoothConversation (int currState){
    	String message = null;
    	switch (currState) {
        case HELLO:
        	readMessage = null;
        	message = "config?";
            break;
        case CONFIG:
        	readMessage = null;
        	message = "config?";
            break;
        case WALL_NAME:
        	readMessage = null;
        	message = "wallName";
            break;
        case NUM_NODES:
        	readMessage = null;
        	message = "#nodes";
            break;
        case NEXT_NODE:
        	readMessage = null;
        	message = "nextNode";
            break;
        case NUM_ROUTES:
        	readMessage = null;
        	message = "#routes";
            break;
        case NEXT_ROUTE:
        	readMessage = null;
        	message = "nextRoute";
            break;
        case START_CLIMB:
        	readMessage = null;
        	message = "startClimb";
            break;
        default:
            Log.d(TAG, "DID NOT MEET A CASE" + messageToSend);
        break;
            
        }
    	return message;
    }

}
