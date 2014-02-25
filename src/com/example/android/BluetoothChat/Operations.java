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

package com.example.android.BluetoothChat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This is the main Activity that displays the current chat session.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({ "InlinedApi", "HandlerLeak" })
public class Operations extends Activity {
    // Debugging
    //private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
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
    
    RelativeLayout mainRelativeLayout;
	RelativeLayout.LayoutParams relativeLayoutParameters;
	ToggleButton tb;
	Operations main = this;
	StringBuilder sBuilder;
	//DragEventListener dragListen = new DragEventListener();
	private static final String TAG = "z";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        mainRelativeLayout = new RelativeLayout(this);
        relativeLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        
        setContentView(mainRelativeLayout, relativeLayoutParameters);
        
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        addToggleButton("y", 20, 20);
        addToggleButton("x", 20, 400);
        
    }

    
    
    public void AddButtonLayout(Button button, int centerInParent, int marginLeft, int marginTop, int marginRight, int marginBottom) 
    {
        RelativeLayout.LayoutParams buttonLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParameters.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        buttonLayoutParameters.addRule(centerInParent);
        
        button.setLayoutParams(buttonLayoutParameters);     
    }

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			
			  tb = new ToggleButton(this);
			  tb.setTag(TAG);
			  tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						sBuilder = new StringBuilder();
						sBuilder.append((String) buttonView.getTag());
						sBuilder.append("8A2BE2");
						Toast.makeText(main, sBuilder.toString(), 1).show();
						sendMessage(sBuilder.toString());
					}
					else
					{
						sBuilder = new StringBuilder();
						sBuilder.append((String)buttonView.getTag());
						sBuilder.append("000000");
						Toast.makeText(main, sBuilder.toString(), 1).show();
						sendMessage(sBuilder.toString());
					}
					
				}
			});
			  tb.setOnLongClickListener(new View.OnLongClickListener() {
			
				@Override
				public boolean onLongClick(View v) {
					
					ClipData.Item item = new ClipData.Item((CharSequence) tb.getTag());
					ClipData dragData = new ClipData((CharSequence)tb.getTag(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

					View.DragShadowBuilder myShadow = new View.DragShadowBuilder(tb);
					
					v.startDrag(dragData, myShadow, null, 0);
					
					return false;
				}
				
				
			  });
			  
			  //tb.setOnDragListener(new DragEventListener());
			  
			  
			  //tb.setOnDragListener(dragListen);
			/*  tb.setOnTouchListener(new View.OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent me) {
					if(me.getAction() == MotionEvent.
					{
						ViewGroup.MarginLayoutParams mParams = new ViewGroup.MarginLayoutParams(v.getLayoutParams());
						
						int left = (int) me.getX();//- (v.getWidth()/2);
						int top = (int) me.getY();// - (v.getWidth()/2);
						mParams.setMargins(left, top-200, 0, 0);
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mParams);
						v.setLayoutParams(layoutParams);
					      
					}
					return false;
				}  
			  });   */
			  //mainRelativeLayout.setOnDragListener(dragListen);
		        tb.setText(".");

		        // Add a Layout to the Button with Margin
		        AddButtonLayout(tb, RelativeLayout.ALIGN_PARENT_LEFT, Math.round(event.getX()), Math.round(event.getY()) - 200, 0, 0);
		        mainRelativeLayout.addView(tb);
		        
				
		}
		return super.onTouchEvent(event);
	}
	
	private void addToggleButton(String address, float x, float y) {
		
        tb = new ToggleButton(this);
		tb.setTag(address);
		tb.setX(x);
		tb.setY(y);
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		        if(isChecked)
		        {
					sBuilder = new StringBuilder();
					sBuilder.append((String) buttonView.getTag());
					sBuilder.append("8A2BE2");
					Toast.makeText(main, sBuilder.toString(), 1).show();
					sendMessage(sBuilder.toString());
		        }
		        else
		        {
					sBuilder = new StringBuilder();
					sBuilder.append((String) buttonView.getTag());
					sBuilder.append("000000");
					Toast.makeText(main, sBuilder.toString(), 1).show();
					sendMessage(sBuilder.toString());
		        }
		    }
		});
        mainRelativeLayout.addView(tb);
		
	}
	
	/*protected class DragEventListener implements View.OnDragListener
	{
		public boolean onDrag(View v, DragEvent event)
		{
			final int action = event.getAction();
			
			switch(action)
			{
			
			case DragEvent.ACTION_DROP:
			    
			    /*View view = (View) event.getLocalState();
			      ViewGroup owner = (ViewGroup) view.getParent();
			      owner.removeView(view);
			      RelativeLayout container = (RelativeLayout) mainRelativeLayout;
			      container.addView(view);
			      view.setVisibility(View.VISIBLE);
				ClipData d = event.getClipData();
				
				ViewGroup.MarginLayoutParams mParams = new ViewGroup.MarginLayoutParams(v.getLayoutParams());
				
				int left = (int) event.getX() - (v.getWidth()/2);
				int top = (int) event.getY() - (v.getWidth()/2);
				mParams.setMargins(left, top-200, 0, 0);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mParams);
				v.setLayoutParams(layoutParams);
				Toast.makeText(main, "im here", Toast.LENGTH_SHORT).show();
			      break;
			}
			return true;
		}
	}*/
    
    
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

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };

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
                    //setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
       /* case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;*/
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
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
       /* case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;*/
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        case R.id.action_reset:
			Toast.makeText(this,  "Reset selected", Toast.LENGTH_SHORT).show();
			mainRelativeLayout.removeAllViews(); // clears the screen
	        addToggleButton("y", 20, 20);
	        addToggleButton("x", 20, 400);
			break;
        }
        return false;
    }

}
