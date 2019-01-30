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
			doc = Jsoup.connect("https://medium.com/@armanavasthi/dont-look-for-motivation-start-working-now-9effb5d973ee").get();
		} catch(IOException ioe) {
			// log the error
		}
		if (doc == null) {
			return;
		}
		// Elements rows = doc.getElementsByTag("P");
		Elements rows = doc.getElementsByClass("section-inner");
		for (Element row : rows) {
			// print row
		}
	}
//	
//	public static void main(String[] args) {
//		scrapeWeb();
//	}
}
