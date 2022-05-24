package com.hyperlcd.iicdemo;

/**
 * This is an I2C operation class
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
