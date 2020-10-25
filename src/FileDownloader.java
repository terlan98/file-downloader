import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDownloader
{
	public static final String DOWNLOAD_FOLDER = "downloaded_files";
	
	/**
	 * Stores the count of files that have been downloaded so far.
	 * It is atomic to ensure thread safety.
	 */
	public static AtomicInteger downloadCount = new AtomicInteger(0);
	
	/**
	 * Contains the URLs of the files to be downloaded
	 */
	private static final ArrayList<FileURL> fileURLs;
	static
	{
		// Reading URLs from config
		ConfigReader reader = new ConfigReader();
		fileURLs = reader.read();
	}
	
	public static void main(String[] args)
	{
		// Creating the download directory if it doesn't exist
		File dir = new File(DOWNLOAD_FOLDER);
		if (!dir.exists()) dir.mkdirs();
		
		if (args.length == 0) // if no arguments were specified
		{
			System.err.println("Please specify the thread mode. (0 - single threaded, 1 - multi threaded)");
			return;
		}
		
		// Parsing the argument as an int and starting the download
		try
		{
			int threadMode = Integer.parseInt(args[0]);
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
	 * @param threadMode Use 0 for single threaded, and 1 for multi threaded.
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
			SequentialDownloader downloader = new SequentialDownloader(fileURLs);
			Thread thread = new Thread(downloader);
			
			long startTime = System.currentTimeMillis();
			thread.start();
			
			thread.join();
			String duration = String.format("%.2f", (System.currentTimeMillis() - startTime) / 1000.0);
			System.out.println("\nTime: " + duration + " sec");
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
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
			
			// Creating and starting the threads
			for (int i = 0; i < threads.length; i++)
			{
				threads[i] = new Thread(downloaders[i]);
				threads[i].start();
			}
			
			// Joining the threads
			for (Thread thread : threads)
			{
				thread.join();
			}
			
			String duration = String.format("%.2f", (System.currentTimeMillis() - startTime) / 1000.0);
			System.out.println("\nTime: " + duration + " sec");
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the total number files that the program will download.
	 */
	public static int getTotalFileCount()
	{
		return fileURLs.size();
	}
}