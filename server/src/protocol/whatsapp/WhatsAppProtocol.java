package protocol.whatsapp;

import protocol.*;
import tokenizer.whatsapp.WhatsAppMessage;


public class WhatsAppProtocol implements AsyncServerProtocol<WhatsAppMessage>
{
	private Engine engine = new Engine();

	public WhatsAppProtocol()
	{
		System.out.println("Constructor WhatsAppProtocol");
	}

	public WhatsAppMessage processMessage(WhatsAppMessage msg)
	{
		try
		{
			Request request = new Request(msg.toString());

			if(request.getUri().equals("login.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return new WhatsAppMessage(engine.login(request));
				} else {
					return new WhatsAppMessage(Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed"));
				}
			}
			else if(request.getUri().equals("logout.jsp"))
			{
				if (request.getMethod().equals("GET"))
				{
					return new WhatsAppMessage(engine.logout(request));
				} else {
					return new WhatsAppMessage(Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed"));
				}
			} 
			else if(request.getUri().equals("send.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return new WhatsAppMessage(engine.send(request));
				} else {
					return new WhatsAppMessage(Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed"));
				}
			}
			else if(request.getUri().equals("queue.jsp"))
			{
				if (request.getMethod().equals("GET"))
				{
					return new WhatsAppMessage(engine.queue(request));
				} else {
					return new WhatsAppMessage(Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed"));
				}
			}
			else if(request.getUri().equals("create_group.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return new WhatsAppMessage(engine.create_group(request));
				} else {
					return new WhatsAppMessage(Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed"));
				}
			}
			else if(request.getUri().equals("list.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return new WhatsAppMessage(engine.list(request));
				} else {
					return new WhatsAppMessage(Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed"));
				}
			}
			else if(request.getUri().equals("add_user.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return new WhatsAppMessage(engine.add_user(request));
				} else {
					return new WhatsAppMessage(Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed"));
				}
			}
			else if(request.getUri().equals("remove_user.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return new WhatsAppMessage(engine.remove_user(request));
				} else {
					return new WhatsAppMessage(Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed"));
				}
			}
			else {
				return new WhatsAppMessage(Response.create(StatusCode.NOT_FOUND, "Page Not Found"));
			}

		}

		catch(Exception ex)
		{
			System.out.println("WhatsAppProtocol.processMessage");
			return new WhatsAppMessage("HTTP 1/1 500\n\nServer error\n$");
		}			
	}

	public boolean isEnd(WhatsAppMessage msg)
	{
		System.out.println("WhatsAppProtocol.isEnd");
		return true;
	}

	public boolean shouldClose()
	{
		System.out.println("WhatsAppProtocol.shouldClose");
		return false;
	}

	public void connectionTerminated()
	{
		System.out.println("WhatsAppProtocol.connectionTerminated");
	}
}