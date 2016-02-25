package com.cdzmqm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class NetServer {
	public static void main(String[] args) throws Exception {
		ServerSocket ss = new ServerSocket();
		InetSocketAddress isa = new InetSocketAddress("192.168.8.158",10003);
		ss.bind(isa);
		int i = 1;
		while(true)
		{
			Socket s = ss.accept();
			
			System.out.println("客户端数量："+i);
			System.out.println(s.getInetAddress());
			System.out.println(s.getRemoteSocketAddress());
			System.out.println(s.getPort());
			System.out.println(s.getLocalAddress());
			System.out.println(s.getLocalSocketAddress());
			System.out.println(s.getLocalPort());
			Thread t = new Thread(new NetServerThread(s,String.valueOf(i)));
			t.start();
			i++;
		}
	}
}
class NetServerThread implements Runnable
{
	private Socket s;
	private String name;
	public NetServerThread(Socket s,String name)
	{
		this.s = s;
		this.name = name;
	}
	@Override
	public void run() {
		try {
			InputStream in = s.getInputStream();
	        OutputStream out = s.getOutputStream();
	        PrintStream ps = new PrintStream(out);
	        ps.println("输入exit退出程序");
	        Scanner c = new Scanner(in);
	        boolean done = false;
	        while (!done && c.hasNextLine()) {
	            String str = c.nextLine();
	            if(str.equals("exit"))
	            {
	                ps.println("程序已退出");
	                done = true;
	            } else {
	                ps.println(this.name+"说："+str);
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}