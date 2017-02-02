package tokenizer.whatsapp;

import tokenizer.Message;
import tokenizer.http.HttpMessage;

public class WhatsAppMessage extends HttpMessage 
{
	private String message;

	public WhatsAppMessage(String message)
	{
		this.message = message;
	}

	public String toString()
	{
		return message;
	}
}

