package com.cdzmqm;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetServer {
  public static void main(String[] args) throws Exception {
	  String str= "HTTP/1.1 200 OK";
	  System.out.println(str.indexOf("HTTP/1."));
  }
}


