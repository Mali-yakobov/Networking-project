package protocol.http;

import protocol.*;
import tokenizer.http.HttpMessage;

public class HttpProtocol implements AsyncServerProtocol<HttpMessage>
{
	public HttpProtocol()
	{
		System.out.println("HERE");
	}

	public HttpMessage processMessage(HttpMessage msg)
	{
		return null;
	}

	public boolean isEnd(HttpMessage msg)
	{
		return false;
	}

	public boolean shouldClose()
	{
		return true;
	}

	public void connectionTerminated()
	{
	}
}