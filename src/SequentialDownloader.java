import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;


public class SequentialDownloader implements Runnable
{
	/**
	 * Contains the URLs of the files to be downloaded
	 */
	private URL[] fileURLs;
	
	public SequentialDownloader(String[] urlStrings)
	{
		fileURLs = new URL[urlStrings.length];
		
		for (int i = 0; i < urlStrings.length; i++)
		{
			try
			{
				fileURLs[i] = new URL(urlStrings[i]);
			} catch (MalformedURLException e)
			{
				System.out.println(urlStrings[i] + " is not a valid URL");
			}
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			int fileNumber = 1;
			
			for (URL url : fileURLs)
			{
				String urlString = url.toString();
				String fileName = urlString.substring(urlString.lastIndexOf('/') + 1);
				
				InputStream in = url.openStream();
				Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
				in.close();
				
				System.out.print(" file" + fileNumber + " -> done");
				if (fileNumber != fileURLs.length) System.out.print(",");
				
				fileNumber++;
			}
		} catch (IOException e)
		{
			System.out.println("Download was not successful. Please make sure that the file doesn't already exist");
		}
	}
}
