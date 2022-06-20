package com.hyperlcd.iicdemo;

import com.victorvision.i2c.I2CManager;

/**
 * Base I2C functionality. This class should not be used directly (use {@link I2CManager} instead).
 */
public class I2c {
	public native int open(String nodeName);
	public native int read(int fileHander, byte i2c_adr, byte buf[],int Length);
	public native int write(int fileHander, byte i2c_adr, byte buf[], int Length);
	public native void close(int fileHander);
	static {
		System.loadLibrary("i2craw");
	}
}
