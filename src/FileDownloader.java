import java.io.IOException;
import java.security.InvalidParameterException;

public class FileDownloader
{
	/**
	 * Contains the URL strings of the files to be downloaded
	 */
	private static final String[] FILE_URL_STRINGS = {"https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
			"http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"};
	
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
			case 1 -> startMultithreadedDownload();
			default -> throw new InvalidParameterException();
		}
	}
	
	/**
	 * Starts the download of files in sequential (single-threaded) mode.
	 */
	private static void startSequentialDownload()
	{
		System.out.print("Mode: Single threaded \nFiles:");
		
		SequentialDownloader downloader = new SequentialDownloader(FILE_URL_STRINGS);
		
		long startTime = System.currentTimeMillis();
		
		Thread thread = new Thread(downloader);
		thread.start();
		
		try
		{
			thread.join();
			System.out.println("\nTime: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the download of files in multi-threaded mode
	 */
	private static void startMultithreadedDownload()
	{
		System.out.print("Mode: Multi threaded \n Files:");
		
	}
}