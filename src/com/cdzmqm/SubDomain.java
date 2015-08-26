package com.cdzmqm;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xbill.DNS.*;

public class SubDomain{
	public static void main(String[] args) throws Exception 
	{	
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
