package com.cdzmqm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest implements Runnable {
	private int start;
	private int step;
	private List<String> list;

	public ThreadTest(List<String> list, int start, int step) {
		this.list = list;
		this.start = start;
		this.step = step;
	}

	public void run() {
		if (this.start < list.size()) {
			for (int i = 0; i < this.step; i++) {
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				System.out.println(Thread.currentThread().getName() + ":"
						+ this.list.get(this.start++));
			}
		}
	}

	public static void main(String[] args) {
		int threads = 5;
		List<String> al = Collections.synchronizedList(new ArrayList<String>());
		for (int ai = 1; ai <= 101; ai++) {
			al.add("元素:" + ai);
		}
		long l = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("开始时间：" + sdf.format(new Date()));
		double d = al.size() / (double) threads;
		int step = (int) Math.ceil(d);
		// int step = (al.size() / threads) + 1;
		// ExecutorService pool = Executors.newCachedThreadPool();
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		for (int i = 0; i < threads; i++) {
			int start = i * step;
			if(start>al.size())
			{
				break;
			}
			System.out.println(i+":"+start);
			pool.execute(new ThreadTest(al, start, step));
			

		}
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				System.out.println("结束时间：" + sdf.format(new Date()));
				long leave = System.currentTimeMillis() - l;
				System.out.println("耗时：" + leave / 1000 + "秒");
				break;
			}
		}
	}
}
