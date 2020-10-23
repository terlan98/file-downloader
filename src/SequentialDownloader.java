import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;


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
			for (int i = 0; i < fileURLs.size(); i++)
			{
				FileURL fileURL = fileURLs.get(i);
				String urlString = fileURL.getURLString();
				String fileName = "file" + fileURL.getId();
				String fileExtension = urlString.substring(urlString.lastIndexOf('.')); // extracting the last
				// component of the url string
				
				InputStream in = fileURL.getURL().openStream();
				Files.copy(in, Paths.get(fileName + fileExtension), StandardCopyOption.REPLACE_EXISTING);
				in.close();
				
				System.out.print(" " + fileName + " -> done");
				if (fileURL.getId() != fileURLs.size()) System.out.print(",");
			}
		}
		catch (IOException e)
		{
			System.out.println("Download was not successful. Please make sure that you have a valid Internet connection");
		}
	}
	
	/**
	 * Adds a new FileURL to this downloader's list.
	 */
	public void addFileURL(FileURL fileURL)
	{
		fileURLs.add(fileURL);
	}
}
