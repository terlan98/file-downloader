import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Reads data from the configuration file.
 * May terminate the whole program if the config file is not found.
 */
public class ConfigReader
{
	public static final String CONFIG_FILE_NAME = "config.cfg";
	
	public ArrayList<FileURL> read()
	{
		ArrayList<FileURL> fileURLs = new ArrayList<>();
		
		try
		{
			exitIfConfigNotFound();
			
			BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE_NAME));
			
			String line = reader.readLine();
			
			for (int i = 1; line != null; i++)
			{
				try
				{
					URL url = new URL(line);
					fileURLs.add(new FileURL(i, url));
				}
				catch (MalformedURLException e)
				{
					System.err.println(line + " is not a valid URL!");
				}
				
				line = reader.readLine();
			}
			
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (fileURLs.isEmpty())
		{
			System.err.println("No URLs found in " + CONFIG_FILE_NAME);
		}
		
		return fileURLs;
	}
	
	/**
	 * Checks for the existence of the config file.
	 * If the file doesn't exist, creates the file, informs the user about what to do next, and terminates the program.
	 */
	private void exitIfConfigNotFound()
	{
		try
		{
			if (Files.notExists(Paths.get(CONFIG_FILE_NAME)))
			{
				System.err.println("Couldn't find the config file. The file will be created. Please paste links to " + CONFIG_FILE_NAME + " and restart the program.");
				Files.createFile(Paths.get(CONFIG_FILE_NAME));
				System.exit(1);
			}
		}
		catch (IOException ignored)
		{
		}
	}
}