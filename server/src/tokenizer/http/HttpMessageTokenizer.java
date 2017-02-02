package tokenizer.http;

import tokenizer.MessageTokenizer;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public class HttpMessageTokenizer implements MessageTokenizer<HttpMessage> {
   
   /**
    * Add some bytes to the message stream.
    * @param bytes an array of bytes to be appended to the message stream.
    */
	public void addBytes(ByteBuffer bytes)
	{
	}

   /**
    * Is there a complete message ready?.
    * @return true the next call to nextMessage() will not return null, false otherwise.
    */
	public boolean hasMessage()
	{
		return false;
	}

   /**
    * Get the next complete message if it exists, advancing the tokenizer to the next message.
    * @return the next complete message, and null if no complete message exist.
    */
	public HttpMessage nextMessage()
	{
		return null;
	}

   /**
    * Convert the String message into bytes representation, taking care of encoding and framing.
    * @return a ByteBuffer with the message content converted to bytes, after framing information has been added.
    */
	public ByteBuffer getBytesForMessage(HttpMessage msg) throws CharacterCodingException
	{
		return null;
	}
}
