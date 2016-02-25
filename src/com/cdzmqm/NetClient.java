package com.cdzmqm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class NetClient {

	public static void main(String[] args) throws Exception {
		Socket s = new Socket("127.0.0.1", 10016);
		OutputStream out = s.getOutputStream();
		InputStream in = s.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		System.out.println(br.readLine());
		PrintStream ps = new PrintStream(out);
		Scanner c = new Scanner(System.in);
		String str;
		while ((str = c.nextLine()) != null) {
			ps.println(str);
			System.out.println(br.readLine());
		}
	}
}
