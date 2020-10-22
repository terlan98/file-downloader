package com.tarlan;

import java.net.URL;

public class SequentialDownloader implements Runnable
{
	private URL[] fileURLs;
	
	public SequentialDownloader(URL[] fileURLs)
	{
		this.fileURLs = fileURLs;
	}
	
	@Override
	public void run()
	{
	
	}
}
