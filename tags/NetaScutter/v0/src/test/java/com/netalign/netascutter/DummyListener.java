package com.netalign.netascutter;

import java.net.URL;
import java.util.Random;

import com.hp.hpl.jena.rdf.model.Model;
import com.netalign.netascutter.interfaces.IUrlListener;

public class DummyListener implements IUrlListener {
   	private Random rand;
	
	public DummyListener() {
		rand = new Random();
	}
	
	@Override
	public boolean addSeeAlsos(Model incoming) {
		System.out.println("addSeeAlsos not implemented");	
		return true;
	}

	@Override
	public boolean addURL(URL url) {
		System.out.println(url.toString());			
		return true;
	}

	@Override
	public boolean addURL(String url) {
		System.out.println(url);
		return true;		
	}

	@Override
	public boolean addURLUnchecked(URL url) {
		System.out.println(url.toString());		
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
	
}