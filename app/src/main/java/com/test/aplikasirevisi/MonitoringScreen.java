package com.test.aplikasirevisi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import static com.test.aplikasirevisi.Information.Highest;
import static com.test.aplikasirevisi.Information.Lowest;
import static com.test.aplikasirevisi.Information.PhoneNum;
import static com.test.aplikasirevisi.Information.mypreference;


public class MonitoringScreen extends Activity {
    private GpsTracker gpsTracker;
    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    TextView highest;
    TextView lowest;
    TextView phonenum;
    private boolean mIsUserInitiatedDisconnect = false;
    private TextView mTxtReceive;
    private Button mBtnClearInput;
    private Button mBtnGetBPM;
    private ScrollView scrollView;
    private CheckBox chkScroll;
    private CheckBox chkReceiveText;
    String msg2;
    int number, hi,lo;
    Timer j = new java.util.Timer();


    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_screen);
        ActivityHelper.initialize(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);
        Log.d(TAG, "Ready");
        mTxtReceive = (TextView) findViewById(R.id.txtReceive);
        chkScroll = (CheckBox) findViewById(R.id.chkScroll);
        chkReceiveText = (CheckBox) findViewById(R.id.chkReceiveText);
//        scrollView = (ScrollView) findViewById(R.id.viewScroll);
        mBtnClearInput = (Button) findViewById(R.id.btnClearInput);
        mBtnGetBPM = (Button) findViewById(R.id.mBtnGetBPM);

        mTxtReceive.setMovementMethod(new ScrollingMovementMethod());
        highest = (TextView) findViewById(R.id.etHighest);
        lowest = (TextView) findViewById(R.id.etLowest);
        phonenum = (TextView) findViewById(R.id.etPhoneNum);

        SharedPreferences sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Highest)) {
            highest.setText(sharedpreferences.getString(Highest, ""));
        }
        if (sharedpreferences.contains(Lowest)) {
            lowest.setText(sharedpreferences.getString(Lowest, ""));
        }
        if (sharedpreferences.contains(PhoneNum)) {
            phonenum.setText(sharedpreferences.getString(PhoneNum, ""));
        }
        mBtnClearInput.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mTxtReceive.setText("");
            }
        });
        getLocation();

        String getPhoneNum = phonenum.getText().toString();
    }
    public void getLocation(){
        gpsTracker = new GpsTracker(this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            msg2="\n"+"https://www.google.com/maps/search/?api=1&query="+latitude+","+ longitude;
        }else{
            gpsTracker.showSettingsAlert();
        }
    }
    private class ReadInput implements  Runnable{
        private boolean bStop = false;
        private boolean bStop2 = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();

        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;
            checkBpm();
            try {
                inputStream = mBTSocket.getInputStream();
                String getHi = null;
                String getLo =null;
                SharedPreferences sharedpreferences = getSharedPreferences(mypreference,
                        Context.MODE_PRIVATE);
                if (sharedpreferences.contains(Highest)) {
                    highest.setText(sharedpreferences.getString(Highest, ""));
                    getHi=highest.getText().toString();
                }
                if (sharedpreferences.contains(Lowest)) {
                    lowest.setText(sharedpreferences.getString(Lowest, ""));
                    getLo=lowest.getText().toString();
                }
                if (sharedpreferences.contains(PhoneNum)) {
                    phonenum.setText(sharedpreferences.getString(PhoneNum, ""));
                }
                hi = Integer.parseInt(getHi);
                lo = Integer.parseInt(getLo);
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 1; i++) {
                        }
                        final String strInput = new String(buffer, 0, i).trim();

                        String tmp = new String(buffer);
                        String str = tmp.substring(0,tmp.indexOf('\u0000'));
//                        System.out.println(str);

                        number = Integer.parseInt(strInput);
                        System.out.println(number);


//                        int count = 0;
//                        while (!bStop2){
//                            if(number <lo){
//                                if(count>30){
//                                    showAlertDialogWithAutoDismiss();
//                                    break;
//                                }
//                                count++;
//                            }
//                            if(number > lo && number<hi){
//                                count=0;
//                            }
//                            if(number>hi){
//
//                                if(count>30){
//                                    showAlertDialogWithAutoDismiss();
//                                    break;
//                                }
//                                count++;
//                            }
//                        }

//                        j.schedule(new java.util.TimerTask() {
//                            @Override
//                            public void run() {
//                                MonitoringScreen.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                            if(number<lo){
//                                                showAlertDialogWithAutoDismiss();
//                                                j.cancel();
//                                            }else if(number>lo && number<hi){
//                                                System.out.println("Ok");
//                                            }else if(number>hi){
//                                                showAlertDialogWithAutoDismiss();
//                                                j.cancel();
//                                            }
//
//                                    }
//                                });
//
//                                };
//                            },5000
//                        );

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */

                        if (chkReceiveText.isChecked()) {
//                            mTxtReceive.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mTxtReceive.append(strInput);
//
//                                    int txtLength = mTxtReceive.getEditableText().length();
//                                    if(txtLength > mMaxChars){
//                                        mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
//                                        Log.d(TAG, "text longer than allowed:" + mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars));
//                                    }
//
//                                    if (chkScroll.isChecked()) { // Scroll only if this is checked
//                                        scrollView.post(new Runnable() { // Snippet from http://stackoverflow.com/a/4612082/1287554
//                                            @Override
//                                            public void run() {
//                                                scrollView.fullScroll(View.FOCUS_DOWN);
//                                            }
//                                        });
//                                    }
//                                }
//                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTxtReceive.setText(String.valueOf(strInput));
                                }
                            });



                        }


                    }

                    Thread.sleep(500);

                }





            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }
    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MonitoringScreen.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }
    public void showAlertDialogWithAutoDismiss() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ALERT!!!")
                .setMessage("We Detected An Abnormality Are You OKAY?")
                .setCancelable(false)
                .setPositiveButton("Yes, I'm Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //this for skip dialog
                        checkBpm();
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()){
                    alertDialog.dismiss();
                }
            }
        }, 5000); //change 5000 with a specific time you want
    }
    private void checkBpm() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        // run something after 5 sec delay
//                        if(number == 0){
//                            checkBpm();
//                        }
                        if(number<lo){
                            showAlertDialogWithAutoDismiss();

                        }
                        if(number>hi){
                            showAlertDialogWithAutoDismiss();

                        }
                        // Rest of your if condition


                    }
                }, 5000);
            }
        });

    }

}