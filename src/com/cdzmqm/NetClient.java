package com.cdzmqm;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetClient {

	public static void main(String[] args) throws UnknownHostException, IOException
	{
//		Socket s = new Socket("127.0.0.1", 1234);
		PrintStream ps = new PrintStream("f:/1.txt");
		ps.write(arg0);
	}
}
