package protocol.whatsapp;

import java.util.Map;
import java.util.HashMap;
import java.net.URLDecoder;

public class Request
{
	String method = "";
	String uri = "";
	String user_auth = "";
	Map<String, String> params = new HashMap<String, String>();

	public Request(String request)
	{
		String[] lines = request.split("\n");
		String authCode = "";		

		for(int i=0; i<lines.length; i++)
		{
			if (lines[i].startsWith("Cookie: user_auth="))
				user_auth = lines[i].substring(18);				
			if (lines[i].length()==0)
				break;
		}

		int dataLineIndex = -1;

		if (lines[0].startsWith("POST"))
		{
			for(int i=0; i<lines.length; i++)
			{
				if (lines[i].length()==0)
				{				
					dataLineIndex = i+1;
					break;
				}
			}

			if (dataLineIndex<lines.length)
			{
				String[] paramsString = lines[dataLineIndex].split("&");
				for(int i=0; i<paramsString.length; i++)
				{
					String[] entries = paramsString[i].split("=");
					if (entries.length>1)
					{
						params.put(entries[0], URLDecoder.decode(entries[1]));
					}
				}
			}

			method = "POST";

		} else if (lines[0].startsWith("GET")) {

			method = "GET";

		} else {

			method = "UNKNOWN";
		}

		String[] parts = lines[0].split(" ");
		if (parts.length>1)
		{
			uri = parts[1].toLowerCase();
			while(uri.startsWith("/") || uri.startsWith("\\"))
				uri = uri.substring(1);
		}
	}

	public String getMethod()
	{
		return method;
	}

	public String getUri()
	{
		return uri;
	}

	public String getUserAuth()
	{
		return user_auth;
	}

	public String getParam(String key)
	{
		return params.get(key);
	}
}