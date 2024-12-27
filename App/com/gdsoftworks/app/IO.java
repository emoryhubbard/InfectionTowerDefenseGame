package com.gdsoftworks.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IO {
	public InputStream readResource(String fileName) throws IOException;
	public InputStream readFile(String fileName) throws IOException;
	public OutputStream writeFile(String fileName) throws IOException;
}