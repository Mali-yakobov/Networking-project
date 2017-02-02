package protocol.whatsapp;

public class WhatsAppMessage
{
	private String from;
	private String message;

	public WhatsAppMessage(String from, String message)
	{
		this.from = from;
		this.message = message;
	}

	public String getFrom()
	{
		return from;
	}

	public String getMessage()
	{
		return message;
	}
}