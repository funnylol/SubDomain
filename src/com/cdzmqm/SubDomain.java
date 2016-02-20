package com.cdzmqm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.lang.model.element.ExecutableElement;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class SubDomain {
	public static void main(String args[]) throws Exception {

		String domain = "baidu.com";
		String file = "www.txt";
		int threads =200;
		ExecutorService pool = Executors.newCachedThreadPool();
		SubDomain sd = new SubDomain();
	
		Set<String> treeset = sd.makeDomain(domain, file);
		String rnd = sd.getRnd(domain);
//		BruteForce bf = new BruteForce(treeset, sd.getRnd(domain),threads);
		long l = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("开始时间：" + sdf.format(new Date()));
		int sp=0;int div = treeset.size()/threads;int j = 0 ;
		for (int i = 0; i < threads; i++) {
			sp = sp + div;
//			pool.execute(new Thread(bf, "线程" + String.valueOf(i)));
			pool.execute(new BruteForce(treeset, rnd, sp,j));
			j = sp;
		}
		pool.shutdown();
		while (true) {
			if (pool.isTerminated() == true) {
				System.out.println("结束时间：" + sdf.format(new Date()));
				long leave = System.currentTimeMillis() - l;
				System.out.println("耗时：" + leave / 1000 + "秒");
				break;
			}
		}

	}

	public Set<String> makeDomain(String domain, String file) throws Exception {
		domain = this.addPoint(domain);
		TreeSet<String> treeset = new TreeSet<String>();
		FileReader f = new FileReader(file);
		BufferedReader br = new BufferedReader(f);
		String tmp;
		while ((tmp = br.readLine()) != null) {
			if (!tmp.equals("")) {
				String name = tmp + domain;
				treeset.add(name);
			}
		}
		return treeset;
	}

	private String addPoint(String npdomain) {
		String domain = "";
		if (!npdomain.startsWith(".")) {
			domain = "." + npdomain;
		} else {
			domain = npdomain;
		}
		return domain;
	}

	public String getRnd(String domain) {
		String rnd = String.valueOf(System.currentTimeMillis());
		String name = rnd.substring(rnd.length() - 5) + "ms509." + domain;
		String tmp = SubDomain.getA(name);
		if (!tmp.startsWith("ERROR")) {
			return tmp.split("\t")[1];
		} else {
			return null;
		}
	}

	public static String getA(String domain) {
		String data = "";
		try {
			Lookup lookup = new Lookup(domain, Type.A);
			lookup.run();
			if (lookup.getResult() != Lookup.SUCCESSFUL) {
				data = "ERROR: " + lookup.getErrorString();
				// ERROR: host not found
				// ERROR: SERVFAIL
			} else {
				Record[] answers = lookup.getAnswers();
				for (Record rec : answers) {
					String tdomain = rec.getName().toString();
					String rdomain = tdomain.substring(0, tdomain.length() - 1);
					String rip = rec.rdataToString();
					data += domain + "\t" + rip + System.lineSeparator();
//					data += rdomain + "\t" + rip + System.lineSeparator();
				}
			}
		} catch (Exception e) {

		}
		return data;
	}

}

class Domain implements Comparable {
	private String name;
	private String ip;
	private String iscdn;

	@Override
	public int compareTo(Object o) {
		Domain d2 = (Domain) o;
		int ipc = this.ip.compareTo(d2.getIp());
		if(ipc==0)
		{
			int namec = this.name.compareTo(d2.getName());
			return namec;
		} 
		return ipc;
	}
	public String getIscdn() {
		return iscdn;
	}

	public void setIscdn(String iscdn) {
		this.iscdn = iscdn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	@Override
	public String toString() {
		return this.ip+this.name;
	}
}

class BruteForce implements Runnable {
	private List<String> al;
	private int i;
	private String rnd;
	private int sp;
	private static int j;

	public BruteForce(Set<String> treeset, String rnd,int sp,int i ) {
		this.al = Collections.synchronizedList(new ArrayList<String>(treeset));
		this.rnd = rnd;
		this.sp = sp;
		this.i = i;
	}

	public void run() {
		this.execute();
	}

	private void execute() {
		for (; i < this.sp; i++) {
			test(al.get(i));
//			System.out.println(Thread.currentThread().getName()+":"+al.get(i));
		}
	}

	private void test(String name) {
		String tmp = SubDomain.getA(name);
		if (!tmp.startsWith("ERROR") && !tmp.split("\t")[1].equals(this.rnd)) {
//			 System.out.print(tmp);
			 
		}
		System.out.println(Thread.currentThread().getName()+":"+j+++"/"+1000);
	}

}
