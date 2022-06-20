package com.victorvision.i2c_demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.victorvision.i2c.I2CManager;
import com.victorvision.i2c.PortNotOpenException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "I²C Demo";

    private byte _slaveAddress = (byte) 0x08;
    private I2CManager _i2cManager;

    private TextView textViewStatus;
    private TextView textViewReceivedDataText;
    private TextView textViewReceivedDataHex;

    private final int autoReadPeriodMilliseconds = 500;
    private Runnable autoReadTask;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewStatus = this.findViewById(R.id.textViewStatus);
        textViewReceivedDataText = this.findViewById(R.id.textViewReceivedDataText);
        textViewReceivedDataHex = this.findViewById(R.id.textViewReceivedDataHex);

        _i2cManager = new I2CManager();
//        _i2cManager.open();
        setStatusText();
    }

    private void StartAutoRequester() {
        autoReadTask = new Runnable() {
            @Override
            public void run() {
                int expectedResponseLength = 10;
                byte[] inputDataBuffer = new byte[0];
                try {
                    inputDataBuffer = _i2cManager.read(_slaveAddress, expectedResponseLength);
                    Log.d(TAG, "Read " + inputDataBuffer.length + " bytes");
                } catch (PortNotOpenException e) {
                    handler.postDelayed(this, autoReadPeriodMilliseconds);
                    return;
                }

                setDataText(inputDataBuffer);

                handler.postDelayed(this, autoReadPeriodMilliseconds);
            }
        };

        autoReadTask.run();
    }

    private void stopAutoRequester(){
        handler.removeCallbacks(autoReadTask);
    }

    public void sendButtonClicked(View view) {
        byte valueToSend = 1;
        try {
            _i2cManager.write(_slaveAddress, valueToSend);
            Toast.makeText(this, "Sending value '" + valueToSend + "' to slave " + _slaveAddress, Toast.LENGTH_SHORT).show();
        } catch (PortNotOpenException e) {
            Toast.makeText(this, "Cannot send, I²C is closed", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void requestButtonClicked(View view) {
        int expectedResponseLength = 1;
        byte[] inputDataBuffer = new byte[0];
        try {
            inputDataBuffer = _i2cManager.read(_slaveAddress, expectedResponseLength);
            Toast.makeText(this, "Requesting " + expectedResponseLength + " bytes from slave " + _slaveAddress, Toast.LENGTH_SHORT).show();
        } catch (PortNotOpenException e) {
            Toast.makeText(this, "Cannot request, I²C is closed", Toast.LENGTH_SHORT).show();
            return;
        }
        setDataText(inputDataBuffer);
    }
    public byte[] readFromI2C(byte slaveAddress, int expectedResponseLength) {
        byte[] inputDataBuffer = new byte[0];
        try {
            inputDataBuffer = _i2cManager.read(slaveAddress, expectedResponseLength);
            Toast.makeText(this, "Requesting " + expectedResponseLength + " bytes from slave " + _slaveAddress, Toast.LENGTH_SHORT).show();
        } catch (PortNotOpenException e) {
            Toast.makeText(this, "Cannot request, I²C is closed", Toast.LENGTH_SHORT).show();
            return inputDataBuffer;
        }

        return inputDataBuffer;
    }
    public void openButtonClicked(View view) {
        _i2cManager.open();
        setStatusText();
    }

    public void closeButtonClicked(View view) {
        _i2cManager.close();
        setStatusText();
    }

    private void setStatusText(){
        if (_i2cManager.getIsOpen()) {
            textViewStatus.setText("I²C is open");
        } else {
            textViewStatus.setText("I²C is closed");
        }
    }

    private void setDataText(byte[] inputDataBuffer){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String textData = new String(inputDataBuffer);
                String hexData = byteArrayToHex(inputDataBuffer);

                textViewReceivedDataText.setText("Text = " + textData);
                textViewReceivedDataHex.setText("Hex = " + "0x" + hexData);
            }
        });
    }

    private String byteArrayToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        for(byte b: bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString().toUpperCase();
    }
}