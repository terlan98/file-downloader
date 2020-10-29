import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Downloads files from a given list of FileURLs sequentially.
 */
public class SequentialDownloader implements Runnable
{
	/**
	 * Contains the FileURLs of the files to be downloaded
	 */
	private final ArrayList<FileURL> fileURLs;
	
	public SequentialDownloader(ArrayList<FileURL> fileURLs)
	{
		this.fileURLs = fileURLs;
	}
	
	public SequentialDownloader()
	{
		this.fileURLs = new ArrayList<>();
	}
	
	@Override
	public void run()
	{
		try
		{
			for (FileURL fileURL : fileURLs)
			{
				String urlString = fileURL.getURLString();
				String fileName = "file" + fileURL.getId();
				String fileExtension = urlString.substring(urlString.lastIndexOf('.')); // extracting the last component of the url string
				
				InputStream in = fileURL.getURL().openStream();
				Files.copy(in, Paths.get(FileDownloader.DOWNLOAD_FOLDER + '/' + fileName + fileExtension), StandardCopyOption.REPLACE_EXISTING);
				in.close();
				
				printSuccessMessage(fileName);
			}
		}
		catch (IOException e)
		{
			System.err.println("Download was not successful. Please make sure that you have a valid Internet connection");
		}
	}
	
	/**
	 * Adds a new FileURL to this downloader's list.
	 */
	public void addFileURL(FileURL fileURL)
	{
		fileURLs.add(fileURL);
	}
	
	/**
	 * Prints the name of the file and '-> done' followed by a comma if necessary.
	 *
	 * @param fileName name of the file that was downloaded successfully
	 */
	private void printSuccessMessage(String fileName)
	{
		int downloadCount = FileDownloader.downloadCount.incrementAndGet();
		
		System.out.print(" " + fileName + " -> done");
		if (downloadCount != FileDownloader.getTotalFileCount()) // no comma if this is the last file
			System.out.print(",");
	}
}
