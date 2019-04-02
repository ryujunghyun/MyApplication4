package junghyun.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class bell extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT=3; // 요청코드 상수 정의

    private BluetoothAdapter btAdapter = null; // 객체선언


    private static final String TAG = "bluetooth";
    final int RECIEVE_MESSAGE = 1;        // Status  for Handler;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    private static int flag = 0;

    Button drop, cancel;
    Handler h;

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "20:16:02:30:44:18";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bell);

        drop = (Button) findViewById(R.id.drop);
        cancel = (Button) findViewById(R.id.cancel);
        Intent intent=getIntent();
        //기기가 블루투스를 지원하는지 확인
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter==null){
            Toast.makeText(this, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        drop = (Button) findViewById(R.id.drop);
        cancel = (Button) findViewById(R.id.cancel);

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        drop.setEnabled(true);
                        cancel.setEnabled(true);
                }
            }
        };

        drop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");
                //Toast.makeText(getBaseContext(), "Turn on First LED", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("2");
                //Toast.makeText(getBaseContext(), "Turn on Second LED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onStart(){
        super.onStart(); //블루투스가 활성화 되어있는지 확인

        if(!btAdapter.isEnabled()){
            Intent enableIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE); //객체생성
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT); //실행
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            //블루투스 장치를 켜기 위한 요청코드인 경우
            case REQUEST_ENABLE_BT:
                //장치 켜짐의 여부에 따라 토스트 메시지 출력
                if(resultCode== Activity.RESULT_OK){
                    Toast.makeText(this, "블루투스를 활성화하였습니다.", Toast.LENGTH_LONG).show();
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);

                    Log.d(TAG, "...onResume - try connect...");
                    try {
                        btSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
                    }

                    btAdapter.cancelDiscovery();

                    Log.d(TAG, "...Connecting...");
                    try {
                        btSocket.connect();
                        Log.d(TAG, "....Connection ok...");
                    } catch (IOException e) {
                        try {
                            btSocket.close();
                        } catch (IOException e2) {
                            errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                        }
                    }

                    Log.d(TAG, "...Create Socket...");

                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();

                }
                else{
                    onBackPressed();
                    Toast.makeText(this, "블루투스를 활성화하지 못했습니다.", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();



        // Set up a pointer to the remote node using it's address.

    }

    @Override
    public void onPause() {
        super.onPause();
    }




    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }



        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }
}
