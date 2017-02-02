package protocol_http;

import protocol.ServerProtocol;

public class HttpProtocol<T> implements ServerProtocol<T> {
	
    /**
     * processes a message
     * @param msg the message to process
     * @return the reply that should be sent to the client, or null if no reply needed
     */
	public T processMessage(T msg)
	{
		String response = "HTTP/1.1 500\n\nServer Unavailable\n$";
		return (T)response;
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