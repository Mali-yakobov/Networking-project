package protocol_whatsapp;

public class Response
{
	public static String create(int code, String message)
	{	
		return String.format("HTTP/1.1 %d\n\n%s\n$", code, message);
	}

	public static String create(int code, String user_auth, String message)
	{	
		if (user_auth.length()>0)
			return String.format("HTTP/1.1 %d\nSet-Cookie: user_auth=%s\n\n%s\n$", code, user_auth, message);
		else
			return Response.create(code, message);
	}
}