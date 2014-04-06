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
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

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
    
    //Wall information 
    public String wallName;
    public int numNodes = 0;
    public int numPaths = 0;
    
    //Bluetooth conversation with hub states
    public static final int HELLO = 1;
    public static final int CONFIG = 2;
    public static final int WALL_NAME = 3;
    public static final int NUM_NODES = 4;
    public static final int NEXT = 5;
    public static final int NUM_PATHS = 6;
    public static final int START_CLIMB = 7;
    
    //state of conversation
    public int conversation_state = 0;

    //counters
    int nodeCount = 0;
    int pathCount = 0;
    
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
        
        Toast.makeText(getApplicationContext(), "just got done on creating!",  Toast.LENGTH_SHORT).show();

       
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
    	
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.configuration, menu);        
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
			case R.id.action_climb:
				//Intent i=new Intent(context, ClimbActivity.class);
				Intent switchView = new Intent(this, ClimbActivity.class);
				startActivity(switchView);
		        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
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
        if (mChatService != null) mChatService.stop();
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
        
        /*if (subTitle.equals(R.string.title_not_connected)){
        	Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            Toast.makeText(getApplicationContext(), "trying here yo",  Toast.LENGTH_SHORT).show();

        }*/
        
        if (subTitle.equals(getString(R.string.title_connected_to, mConnectedDeviceName))){
        	sendMessage("hello");
        	conversation_state = 1;
            Toast.makeText(getApplicationContext(), "Sent Hello :D",  Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "just got done not connecting!",  Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothConnection.STATE_NONE:
                    //setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                readMessage = new String(readBuf, 0, msg.arg1);
                Toast.makeText(getApplicationContext(), "Received Message" + readMessage,  Toast.LENGTH_SHORT).show();
                bluetoothConversation(conversation_state);
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
    
    
    private void bluetoothConversation (int currState){
    	switch (currState) {
        case HELLO:
        	readMessage = null;
        	conversation_state = CONFIG;
        	sendMessage("config?");
            break;
        case CONFIG:
        	if(readMessage.contains("yes")){
            	readMessage = null;
            	configured = true;
        	}
        	else if (readMessage.contains("no")){
        		readMessage = null;
        		configured = false;
        	}
    		conversation_state = WALL_NAME;
    		sendMessage("wallName");
            break;
        case WALL_NAME:
        	wallName = readMessage;
        	readMessage = null;
        	conversation_state = NUM_NODES;
        	sendMessage("#nodes");
            break;
        case NUM_NODES:
        	if (configured){
            	if(nodesCalled){
                	String[] parts = readMessage.split(" ");
            		if (nodeCount < numNodes -1){
                		Node tn = null;
                		Node temp = tn;
                		temp = new Node (this, tn);
                		temp.setAddress((parts[0]));
                		temp.setX(Float.parseFloat(parts[1]));
                		temp.setY(Float.parseFloat(parts[2]));
                		nodes.add(temp);
                		nodeCount++;
                		readMessage = null;
                		sendMessage("next");
                	}
            		Node tn = null;
            		Node temp = tn;
            		temp = new Node (this, tn);
            		temp.setAddress((parts[0]));
            		temp.setX(Float.parseFloat(parts[1]));
            		temp.setY(Float.parseFloat(parts[2]));
            		nodes.add(temp);
            		nodeCount++;
            		readMessage = null;
                	conversation_state = START_CLIMB;
                	Wall.saveNodes(nodes);
                	sendMessage ("#paths");
            	}
            	else{
                	numNodes = Integer.parseInt(readMessage);
                	readMessage = null;
                	sendMessage("next");
                	nodesCalled = true;
            	}
        	}//end if configured
        	//if not configured switch to setup view
        	else if(!configured){
            	numNodes = Integer.parseInt(readMessage);
            	Wall.setNumNodes(numNodes);
            	readMessage = null;
				Intent switchToSetupView = new Intent(this, SetupActivity.class);
				startActivity(switchToSetupView);
		        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        	}

        case NUM_PATHS:
        	
        	if(pathsCalled){
        		if (pathCount < numPaths -1){
					Route route = new Route(readMessage);
					Wall.saveRoute(route);
            		pathCount++;
            		readMessage = null;
            		sendMessage("next");
            	}
        		Route route = new Route(readMessage);
        		Wall.saveRoute(route);
        		pathCount++;
        		readMessage = null;
            	conversation_state = START_CLIMB;
            	sendMessage ("start climb");
        	}
        	else{
            	numPaths = Integer.parseInt(readMessage);
            	Wall.setNumPaths(numPaths);
            	readMessage = null;
            	sendMessage("next");
            	pathsCalled = true;
        	}
            break;
        case START_CLIMB:
    		readMessage = null;
			Intent switchToClimbView = new Intent(this, ClimbActivity.class);
			startActivity(switchToClimbView);
	        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            break;
        }
    	
    }

}
