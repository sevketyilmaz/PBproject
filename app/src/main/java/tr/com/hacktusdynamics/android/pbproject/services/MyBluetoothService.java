package tr.com.hacktusdynamics.android.pbproject.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.R;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class MyBluetoothService {
    private static final String TAG = MyBluetoothService.class.getSimpleName();

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    //Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    private Context mContext;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    //constructors
    public MyBluetoothService(Context context, Handler handler){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        mContext = context;
    }

    /**
     * Set current state of the MyBluetoothService
     */
    private synchronized void setState(int state){
        Log.d(TAG, "setState() " + mState + " -> " + state );
        mState = state;

        //Give new state to Handler so the UI can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state
     */
    public synchronized int getState(){
        return mState;
    }

    /**
     * Start to ConnectThread to initiate a connection to a remote device
     */
    public synchronized void connect(BluetoothDevice bluetoothDevice){
        Log.d(TAG, "connect to: " + bluetoothDevice);

        //Cancel any thread attempting to make a connection
        if(mState == STATE_CONNECTING){
            if(mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        //Cancel any thread currently running a connection
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        //Start the thread to connect with the given device
        mConnectThread = new ConnectThread(bluetoothDevice);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start ConnectedThread to begin managing a Bluetooth connection
     */
    public synchronized void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice){
        Log.d(TAG, "connected to: " + bluetoothDevice);

        //Cancel the thread that completed the connection
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        //Cancel any thread currently running a connection
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        //Start the thread to manage the connection and perform transmission
        mConnectedThread = new ConnectedThread(bluetoothSocket);
        mConnectedThread.start();

        //Send the name of the connected device back to UI
        Message message = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, bluetoothDevice.getName());
        message.setData(bundle);
        mHandler.sendMessage(message);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop(){
        Log.d(TAG, "stop all threads");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI
     */
    private void connectionFailed(){
        //Send a failure message back to UI
        Message message = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, mContext.getString(R.string.connection_failed));
        message.setData(bundle);
        mHandler.sendMessage(message);

        setState(STATE_NONE);
    }

    /**
     * Indicate that the conncetion was lost and notify the UI
     */
    private void connectionLost(){
        //Send a failure message back to UI
        Message message = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, mContext.getString(R.string.connection_lost));
        message.setData(bundle);
        mHandler.sendMessage(message);

        setState(STATE_NONE);
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a device.
     * The connection either succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        //constructor
        public ConnectThread(BluetoothDevice bluetoothDevice){
            mmDevice = bluetoothDevice;
            BluetoothSocket tmp = null; // Use a temporary object that is later assigned to mmSocket, because mmSocket is final

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try{
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException e){
                Log.e(TAG, "ConnectThread create() failed." , e);
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            Log.i(TAG, " BEGIN ConnectThread.");
            setName("ConnectThread"); //set name for the thread

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "ConnectThreat: unable to close() socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (MyBluetoothService.this) {
                mConnectThread = null;
            }

            // Start the ConnectedThread
            connected(mmSocket, mmDevice);
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            }catch (IOException e){
                Log.e(TAG, "ConnectThread close() failed.", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //constructor
        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            Log.d(TAG, "ConnectedThread");
            mmSocket = bluetoothSocket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because member streams are final
            try{
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            }catch (IOException e){
                Log.e(TAG, "ConnectedThread: tmp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            Log.i(TAG, "ConnectedThread BEGIN");

            final byte delimeter = 10; //ASCII code for a newline character
            int readBufferPosition = 0;
            byte[] readBuffer = new byte[1024];

            // Keep listening to the InputStream until an exception occurs
            while(true){
                try {
                    int bytesAvailable = mmInStream.available();
                    if(bytesAvailable > 0){
                        byte[] packetBytes = new byte[bytesAvailable];
                        mmInStream.read(packetBytes);

                        for(int i = 0; i < bytesAvailable; i++){
                            byte b = packetBytes[i];
                            if(b == delimeter){
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                mHandler.obtainMessage(Constants.MESSAGE_READ, readBufferPosition, -1, data).sendToTarget();
                                readBufferPosition = 0;
                            }else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                }catch (IOException e){
                    Log.e(TAG, "Disconnected.", e);
                    connectionLost();
                    break;
                }
            }

            /*
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothChatService.this.start();
                    break;
                }
            }
            */
        }

        /**
         * Write to the connected OutStream.
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread: Exception during write", e);
            }
        }

        /**
         * Call this to shutdown connection
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread: close() of connect socket failed", e);
            }
        }


    }
}
