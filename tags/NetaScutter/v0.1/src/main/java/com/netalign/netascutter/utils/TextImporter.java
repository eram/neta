/**
 * 
 */
package com.netalign.netascutter.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import com.netalign.netascutter.interfaces.IThreadedUrlListener;

/**
 * reads a file in which every line is a url, and adds these urls to a url listener
 * @author yoavram
 *
 */
public class TextImporter extends Thread {
	private static final long COUNTER_MODULO = 25;
	private static final long SLEEP_TIME = 100;
	
	private static Logger logger = Logger.getLogger(TextImporter.class);

	private IThreadedUrlListener listener = null;
	private String filepath = null;
	private LineIterator lineIterator = null;
	
	public TextImporter() {
	}

	public TextImporter(String filepath, IThreadedUrlListener listener) {
		setListener(listener);
		setFilepath(filepath);
	}
	
	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public IThreadedUrlListener getListener() {
		return listener;
	}

	public void setListener(IThreadedUrlListener listener) {
		this.listener = listener;
	}
	
	public boolean init() {
		if (listener == null) {
			logger.error("Listener is not set");
			return false;
		}
		if (filepath == null) {
			logger.error("File path is not set");
			return false;
		}
		if (filepath.isEmpty()) {
			logger.error("File path is empty");
			return false;
		}
		try {
			lineIterator = FileUtils.lineIterator(new File(filepath));
		} catch (IOException e) {
			logger.error("Failed opening file: " + e);
		}
		return true;
	}
	
	@Override
	public void run() {
		if (lineIterator == null) {
			if (!init()) {
				logger.error("Init failed, exiting");
				return;
			}
		}
		if (listener == null) {
			logger.error("Listener is not set");
			return;		
		}
		
		int counter = 0;
		while(lineIterator.hasNext()) {
			String line = lineIterator.nextLine();
			line = line.trim();
			if (!line.isEmpty()) {
				waitForEmptyQueue();
				listener.addURL(line);
				if (++counter % COUNTER_MODULO == 0) {
					logger.debug("Read " + counter + " URLs from file " + filepath);
				}
			}
		}
		
		logger.debug("Finished after reading " + counter + " URLs");
		return;	
	}
	
	/**
	 * waits until the queue of the listener is empty (size == 0)
	 */
	private void waitForEmptyQueue() {
		while (listener.getNumOfUrls() > 0) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				return;
			}
		}
		return;
	}

}
