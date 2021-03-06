import java.net.URL;

/**
 * Contains the URL and ID of a file.
 */
public class FileURL
{
	private final URL url;
	private final int id;
	
	public FileURL(int fileId, URL url)
	{
		this.id = fileId;
		this.url = url;
	}
	
	public int getId()
	{
		return id;
	}
	
	public URL getURL()
	{
		return url;
	}
	
	public String getURLString()
	{
		return url.toString();
	}
}
