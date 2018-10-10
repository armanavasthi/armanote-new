package com.arman.armaNote.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrapingService {
	
	// this service is not in use as of now, may be in future we write proper code in it to get the main content of the web page
	public static void scrapeWeb() {
		Document doc = null;
		try {
			doc = Jsoup.connect("https://blog.angular-university.io/angular-jwt-authentication/").get();
		} catch(IOException ioe) {
			System.out.println("error");
		}
		if (doc == null) {
			return;
		}
		Elements rows = doc.getElementsByTag("P");
		System.out.println("iterating the rows");
		for (Element row : rows) {
		   System.out.println(row);
		}
	}
	
	/*public static void main(String[] args) {
		System.out.println("started");
		scrapeWeb();
		System.out.println("ended");
	}*/
}
