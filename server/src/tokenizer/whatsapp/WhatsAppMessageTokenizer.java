package tokenizer.whatsapp;

import tokenizer.MessageTokenizer;
import tokenizer.http.*;
import java.lang.StringBuilder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;

public class WhatsAppMessageTokenizer implements MessageTokenizer<WhatsAppMessage> {
   

	private StringBuilder accumulator = new StringBuilder();
   /**
    * Add some bytes to the message stream.
    * @param bytes an array of bytes to be appended to the message stream.
    */
	public void addBytes(ByteBuffer bytes)
	{					
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		
		try
		{
			accumulator.append(decoder.decode(bytes).toString());
		}
		catch(CharacterCodingException ex)
		{
		}

		//for(int i=0; i<bytes.limit(); i++)
		//{
		//	accumulator.append((char)(bytes.get(i)));
		//}
	}

   /**
    * Is there a complete message ready?.
    * @return true the next call to nextMessage() will not return null, false otherwise.
    */
	public boolean hasMessage()
	{
		if (accumulator.length()>0)
		{
			return accumulator.charAt(accumulator.length()-1)=='$';
		}
		return false;
	}

   /**
    * Get the next complete message if it exists, advancing the tokenizer to the next message.
    * @return the next complete message, and null if no complete message exist.
    */
	public WhatsAppMessage nextMessage()
	{

		if (accumulator.length()>0)
		{			
			String message = accumulator.toString();
			accumulator = new StringBuilder();
			return new WhatsAppMessage(message);
		} else {
			return null;
		}
	}

   /**
    * Convert the String message into bytes representation, taking care of encoding and framing.
    * @return a ByteBuffer with the message content converted to bytes, after framing information has been added.
    */
	public ByteBuffer getBytesForMessage(WhatsAppMessage msg) throws CharacterCodingException
	{
		Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();
		return encoder.encode(CharBuffer.wrap(msg.toString()));
	}
}
