package com.netalign.netascutter;

import java.util.Random;

import com.netalign.netascutter.interfaces.IUrlListener;

public class DummyListener implements IUrlListener {
   	private Random rand;
	
	public DummyListener() {
		rand = new Random();
	}
	
	@Override
	public boolean addURL(String url) {
		System.out.println(url);
		return true;		
	}

	@Override
	public boolean addURLUnchecked(String url) {
		System.out.println(url);		
		return true;
	}

	@Override
	public int getNumOfUrls() {
		return rand.nextInt(1);		
	}

	@Override
	public String removeURL() {
		return "NO-URL";
	}

	@Override
	public int getNumOfUrlsSeen() {
		return 0;
	}

	@Override
	public void setPattern(String domain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxNumOfUrls(int maxNumOfUrls) {
		// TODO Auto-generated method stub
		
	}
	
}