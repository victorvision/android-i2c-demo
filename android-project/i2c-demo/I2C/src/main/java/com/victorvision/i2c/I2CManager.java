package com.victorvision.i2c;

import android.util.Log;

import com.hyperlcd.iicdemo.I2c;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * The high-level I²C port wrapper. This is the easiest class to use for I²C communication. Wraps the class {@link I2c}.
 */
public class I2CManager {

    private static final String TAG = "I2C Manager";
    private static String _i2cDevice = "/dev/i2c-0";

    private I2c _i2c;
    private int _fileHandle = -1;
    private boolean _isOpen = false;

    /**
     * Default constructor for class {@link I2CManager}. The default I²C device name is used: {@value #_i2cDevice}.
     */
    public I2CManager(){
        _i2c = new I2c();
    }

    /**
     * Constructor for class {@link I2CManager} able to override the I²C device name.
     * @param i2cDevice The I²C device name.
     */
    public I2CManager(String i2cDevice){
        _i2cDevice = i2cDevice;
        _i2c = new I2c();
    }

    /**
     * Gets I²C port status.
     * @return True if open, false if closed.
     */
    public boolean getIsOpen(){
        return _isOpen;
    }

    /**
     * Opens the I²C port. Uses 100kHz clock.
     * @return True if the port was successfully open, false otherwise.
     */
    public boolean open() {
        // Make sure the device is accessible.
        execShell("chmod 777 " + _i2cDevice);

        // Maybe it's not immediately accessible, so try many times!
        int connectionTries = 5000;
        while (connectionTries > 0) {

            Log.d(TAG, "Trying to open I2C device, " + connectionTries + " left");

            _fileHandle = _i2c.open(_i2cDevice);
            if (_fileHandle > 0) {
                // Valid handle obtained.
                break;
            }

            connectionTries--;
        }

        if (_fileHandle > 0) {
            _isOpen = true;
            return true;
        } else {
            _isOpen = false;
            return false;
        }
    }

    /**
     * Closes the I²C port.
     */
    public void close(){
        _i2c.close(_fileHandle);
        _isOpen = false;
    }

    /**
     * Writes the bytes from {@code outputData}.
     * @param slaveAddress The I²C slave device address.
     * @param outputData The byte array of data to send.
     * @return The number of bytes written.
     * @throws PortNotOpenException Throws if the I²C port is closed.
     */
    public int write(byte slaveAddress, byte[] outputData) throws PortNotOpenException {
        if (_isOpen == false){
            throw new PortNotOpenException();
        }

        return _i2c.write(_fileHandle, slaveAddress, outputData, outputData.length);
    }

    /**
     * Writes the byte from {@code outputData}.
     * @param slaveAddress The I²C slave device address.
     * @param outputData The byte of data to send.
     * @return The number of bytes written.
     * @throws PortNotOpenException Throws if the I²C port is closed.
     */
    public int write(byte slaveAddress, byte outputData) throws PortNotOpenException {
        if (_isOpen == false){
            throw new PortNotOpenException();
        }

        byte[] buffer = new byte[]{outputData};
        return _i2c.write(_fileHandle, slaveAddress, buffer, 1);
    }

    /**
     * Writes "{@code length}" bytes from {@code outputData}.
     * @param slaveAddress The I²C slave device address.
     * @param outputData The byte array containing data to send.
     * @param length How many bytes to send.
     * @return The number of bytes written.
     * @throws PortNotOpenException Throws if the I²C port is closed.
     */
    public int write(byte slaveAddress, byte[] outputData, int length) throws PortNotOpenException {
        if (_isOpen == false){
            throw new PortNotOpenException();
        }

        return _i2c.write(_fileHandle, slaveAddress, outputData, length);
    }

    /**
     * Reads data and returns it.
     * @param slaveAddress The I²C slave device address.
     * @param expectedResponseLength How many bytes are expected to be read.
     * @return A byte array of the received data.
     * @throws PortNotOpenException
     */
    public byte[] read(byte slaveAddress, int expectedResponseLength) throws PortNotOpenException {
        if (_isOpen == false){
            throw new PortNotOpenException();
        }

        byte[] inputDataBuffer = new byte[expectedResponseLength];
        readToBuffer(slaveAddress, inputDataBuffer, expectedResponseLength);

        return inputDataBuffer;
    }

    /**
     * Reads data and copies it to a buffer.
     * @param slaveAddress The I²C slave device address.
     * @param inputDataBuffer The byte array buffer that will received the bytes read.
     * @param expectedResponseLength How many bytes are expected to be read.
     * @return The number of bytes read.
     * @throws PortNotOpenException
     */
    public int readToBuffer(byte slaveAddress, byte[] inputDataBuffer, int expectedResponseLength) throws PortNotOpenException {
        if (_isOpen == false){
            throw new PortNotOpenException();
        }

        int bytesRead = _i2c.read(_fileHandle, slaveAddress, inputDataBuffer, expectedResponseLength);
        return bytesRead;
    }

    /**
     * Executes shell commands as root.
     * @param cmd The comamand to execute.
     */
    private void execShell(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec("su");
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
