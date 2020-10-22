package com.tarlan;

import java.util.Arrays;

public class FileDownloader
{
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.err.println("Please specify the thread mode. (0 - single threaded, 1 - multi threaded)");
			return;
		}
		
		int threadMode = Integer.parseInt(args[0]);
		
		switch (threadMode)
		{
			case 0 -> startSequentialDownload();
			case 1 -> startMultithreadedDownload();
			default -> System.err.println("Invalid thread mode. (0 - single threaded, 1 - multi threaded)");
		}
	}
	
	private static void startSequentialDownload()
	{
	
	}
	
	private static void startMultithreadedDownload()
	{
	
	}
}