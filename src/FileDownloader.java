import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class FileDownloader
{
	/**
	 * Contains the URL strings of the files to be downloaded
//	 */
//	private static final String[] FILE_URL_STRINGS = {"http://www.ubicomp.org/ubicomp2003/adjunct_proceedings/proceedings.pdf",
//			"https://www.hq.nasa.gov/alsj/a17/A17_FlightPlan.pdf", "https://ars.els-cdn.com/content/image/1-s2.0-S0140673617321293-mmc1.pdf",
//			"http://www.visitgreece.gr/deployedFiles/StaticFiles/maps/Peloponnese_map.pdf"};
	
		private static final String[] FILE_URL_STRINGS = {"https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
				"http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf", "https://erj.ersjournals.com/content/erj/32/5/1141.full.pdf"};
		
	
	public static void main(String[] args) throws IOException
	{
		//		if (args.length == 0) // if no arguments were specified
		//		{
		//			System.err.println("Please specify the thread mode. (0 - single threaded, 1 - multi threaded)");
		//			return;
		//		}
		
		// Parsing the argument as an int and starting the download
		try
		{
			//			int threadMode = Integer.parseInt(args[0]);
			int threadMode = 0;
			startDownload(threadMode);
		}
		catch (NumberFormatException | InvalidParameterException e)
		{
			System.err.println("Invalid thread mode. (0 - single threaded, 1 - multi threaded)");
		}
	}
	
	/**
	 * Starts the download of files in the specified thread mode.
	 *
	 * @param threadMode indicates the thread mode. Use 0 for single threaded, and 1 for multi threaded.
	 * @throws InvalidParameterException if the thread mode is different from 0 or 1
	 */
	private static void startDownload(int threadMode) throws InvalidParameterException
	{
		switch (threadMode)
		{
			case 0 -> startSequentialDownload();
			case 1 -> startMultiThreadedDownload();
			default -> throw new InvalidParameterException();
		}
	}
	
	/**
	 * Starts the download of files in sequential (single-threaded) mode.
	 */
	private static void startSequentialDownload()
	{
		System.out.print("Mode: Single threaded \nFiles:");
		
		try
		{
			ArrayList<FileURL> fileURLs = stringsToFileURLs(FILE_URL_STRINGS);
			SequentialDownloader downloader = new SequentialDownloader(fileURLs);
			Thread thread = new Thread(downloader);
			
			long startTime = System.currentTimeMillis();
			thread.start();
			
			thread.join();
			System.out.println("\nTime: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			System.err.println("One of the URLs is invalid.");
		}
	}
	
	/**
	 * Starts the download of files in multi-threaded mode
	 */
	private static void startMultiThreadedDownload()
	{
		System.out.print("Mode: Multi threaded \nFiles:");
		
		try
		{
			ArrayList<FileURL> fileURLs = stringsToFileURLs(FILE_URL_STRINGS);
			Queue<FileURL> fileURLQueue = new LinkedList<>(fileURLs);
			
			int cores = Runtime.getRuntime().availableProcessors();
			int threadCount = Math.min(cores, fileURLQueue.size());
			
			Thread[] threads = new Thread[threadCount];
			SequentialDownloader[] downloaders = new SequentialDownloader[threadCount];
			
			// Indicates which downloader (thread) should handle the next URL polled from the queue.
			int downloaderIndex = 0;
			
			// Each downloader thread is assigned a number of files to download. The logic is similar to Round Robin.
			while (!fileURLQueue.isEmpty())
			{
				if (downloaderIndex >= downloaders.length)
					downloaderIndex = 0; // reset the index to prevent IndexOutOfBounds
				
				FileURL fileUrl = fileURLQueue.poll();
				
				if (downloaders[downloaderIndex] == null)
				{
					downloaders[downloaderIndex] = new SequentialDownloader();
				}
				
				downloaders[downloaderIndex].addFileURL(fileUrl);
				
				downloaderIndex++;
			}
			
			long startTime = System.currentTimeMillis();
			
			// Starting the threads
			for (int i = 0; i < threads.length; i++)
			{
				threads[i] = new Thread(downloaders[i]);
				threads[i].start();
			}
			
			// Joining the threads
			for (int i = 0; i < threads.length; i++)
			{
				threads[i].join();
				
				if (i < threads.length - 1) // we need to print a comma unless we reach the last thread
				{
					System.out.print('|');
				}
			}
			
			System.out.println("\nTime: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			System.err.println("One of the URLs is invalid.");
		}
	}
	
	/**
	 * Converts the given array of URL strings to a FileURL <b>ArrayList</b>.
	 *
	 * @param urlStrings an array containing url strings
	 */
	private static ArrayList<FileURL> stringsToFileURLs(String[] urlStrings) throws MalformedURLException
	{
		FileURL[] fileURLS = new FileURL[urlStrings.length];
		
		for (int i = 0; i < urlStrings.length; i++)
		{
			URL url = new URL(urlStrings[i]);
			fileURLS[i] = new FileURL(i + 1, url);
		}
		
		return new ArrayList<>(Arrays.asList(fileURLS));
	}
}