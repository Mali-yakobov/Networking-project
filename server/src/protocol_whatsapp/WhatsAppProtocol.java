package protocol_whatsapp;

import java.net.URLDecoder;
import java.util.Dictionary;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import protocol.ServerProtocol;
import protocol_http.HttpProtocol;

public class WhatsAppProtocol<T> extends HttpProtocol<T> {

	private Engine engine = new Engine();

	public WhatsAppProtocol()
	{

	}
	

    /**
     * processes a message
     * @param msg the message to process
     * @return the reply that should be sent to the client, or null if no reply needed
     */
	public T processMessage(T msg)
	{
		try
		{
			Request request = new Request(msg.toString());

			if(request.getUri().equals("login.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return (T)engine.login(request);
				} else {					
					return (T)Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed");
				}
			}
			else if(request.getUri().equals("logout.jsp"))
			{
				if (request.getMethod().equals("GET"))
				{
					return (T)engine.logout(request);
				} else {
					return (T)Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed");
				}
			} 
			else if(request.getUri().equals("send.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return (T)engine.send(request);
				} else {
					return (T)Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed");
				}
			}
			else if(request.getUri().equals("queue.jsp"))
			{
				if (request.getMethod().equals("GET"))
				{
					return (T)engine.queue(request);
				} else {
					return (T)Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed");
				}
			}
			else if(request.getUri().equals("create_group.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return (T)engine.create_group(request);
				} else {
					return (T)Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed");
				}
			}
			else if(request.getUri().equals("list.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return (T)engine.list(request);
				} else {
					return (T)Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed");
				}
			}
			else if(request.getUri().equals("add_user.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return (T)engine.add_user(request);
				} else {
					return (T)Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed");
				}
			}
			else if(request.getUri().equals("remove_user.jsp"))
			{
				if (request.getMethod().equals("POST"))
				{
					return (T)engine.remove_user(request);
				} else {
					return (T)Response.create(StatusCode.NOT_ALLOWED, "Method Not Allowed");
				}
			}
			else {
				return (T)Response.create(StatusCode.NOT_FOUND, "Page Not Found");
			}

		}

		catch(Exception ex)
		{
			T response = super.processMessage(msg);
			return response;			
		}		
	}
	
    /**
     * Determine whether the given message is the termination message
     * @param msg the message to examine
     * @return true if the message is the termination message, false otherwise
     */
	public boolean isEnd(T msg)
	{
		return false;
	}
}