package com.cdzmqm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class NetServer {
	public static void main(String[] args) throws IOException
	{
		ServerSocket ss = new ServerSocket(1234);
		while(true)
		{
			Socket s = ss.accept();
			OutputStream ots = s.getOutputStream();
			PrintStream ps = new PrintStream(ots);
			ps.println("欢迎来到Chora的服务器"+new Date());
			ps.close();
			s.close();
		}
	}
}
