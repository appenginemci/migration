package com.sogeti.mci.migration.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ByteArrayInOutStream extends ByteArrayOutputStream {

	public ByteArrayInOutStream() {
		super();
	}
 
	public ByteArrayInOutStream(int size) {
		super(size);
	}
 
	public ByteArrayInputStream getInputStream() {
		// create new ByteArrayInputStream that respect the current count
		ByteArrayInputStream in = new ByteArrayInputStream(this.buf, 0, this.count);
 
		// set the buffer of the ByteArrayOutputStream 
		// to null so it can't be altered anymore
		this.buf = null;
 
		return in;
	}
}