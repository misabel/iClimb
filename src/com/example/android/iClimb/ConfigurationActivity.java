package com.example.android.iClimb;

import java.util.ArrayList;

import com.example.android.BluetoothChat.R;
import com.example.android.BluetoothChat.R.layout;
import com.example.android.BluetoothChat.R.menu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfigurationActivity extends Activity {
	
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    //Status of pending message
    public static final int PENDING_MESSAGE = 1;
    public static final int NO_MESSAGE = 0;
    
    //incoming message status
    public boolean MESSAGE_PENDING = false;
    
    //store incoming bluetooth message
    private String readMessage;
    
    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
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
    
    
    RelativeLayout mainRelativeLayout;
	RelativeLayout.LayoutParams relativeLayoutParameters;
	RelativeLayout configRelativeLayout;
	ArrayList<Node> nodes = new ArrayList<Node>();//array list that will hold the nodes present on this activity
	Node node = null;

    
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
        node = new Node(this, null);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        mainRelativeLayout = new RelativeLayout(this);
        
        this.setTitle("Climb");
        relativeLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        
        setContentView(mainRelativeLayout, relativeLayoutParameters);
		
		configRelativeLayout = new RelativeLayout(this);
			
		
/*		//This section will transfer the nodes that were placed on the first page so that they can be displayed on the configurations page
		  ArrayList<Node> refNodes = SetupActivity.nodes;
	      for(int i = 0 ; i < refNodes.size() ; i++)
	      {
	    	  Node reference = refNodes.get(i);
	    	  node = new Node(this, node.getBefore(), reference.getAddress(), reference.getX(), reference.getY());
	    	  configRelativeLayout.addView(node);
	    	  nodes.add(node);
	      }
*/	      
		
		
		setnodes();
		
	        // Get local Bluetooth adapter
	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        
	        // If the adapter is null, then Bluetooth is not supported
	        if (mBluetoothAdapter == null) {
	            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        }
	      
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configuration, menu);
		return true;
	}
	
/*	 public boolean onOptionsItemSelected(MenuItem item) {
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
	 
*/
	
	
    @Override
    public void onStart() {
        super.onStart();
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

	
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
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
    
    
    
    private void setnodes(){
        ArrayList<Node> refNodes = SetupActivity.nodes;
        for(int i = 0 ; i < refNodes.size() ; i++)
        {
        	Node reference = refNodes.get(i);
        	node = new Node(this, node.getBefore(), reference.getAddress(), reference.getX(), reference.getY());
        	
        	node.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();

                    //if node selected
                    if (action == MotionEvent.ACTION_DOWN ) {
                    	//if button pushed do nothing, wait for release
                    }
                    
                    //else if node is released
                    else if (action == MotionEvent.ACTION_UP ) {
                    	if (MESSAGE_PENDING){
                    		if (node.getAddress() == null){
                    			node.setAddress(readMessage);
        			        	readMessage = null;
        			        	MESSAGE_PENDING = false;
        			        	/**
        			        	 * 
        			        	 * fix bluetooth message to be sent
        			        	 * 
        			        	 * 
        			        	 * 
        			        	 * 
        			        	 * */
        			        	sendMessage("GIVE ME NEXT ADDRESS");
                    		}
                    		else{
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
                        			        	node.setAddress(readMessage);
                        			        	readMessage = null;
                        			        	MESSAGE_PENDING = false;
                        			        	/**
                        			        	 * 
                        			        	 * fix bluetooth message to be sent
                        			        	 * 
                        			        	 * 
                        			        	 * 
                        			        	 * 
                        			        	 * */
                        			        	sendMessage("GIVE ME NEXT ADDRESS");
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
	    	configRelativeLayout.addView(node);
        	nodes.add(node);
        	
        	
        }
    }
    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }
    
    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
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
                    //setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                readMessage = new String(readBuf, 0, msg.arg1);
                MESSAGE_PENDING = true;
                Toast.makeText(getApplicationContext(), "Received Message",  Toast.LENGTH_SHORT).show();
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

}
