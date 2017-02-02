package tokenizer_whatsaap;

import java.io.InputStream;
import java.io.InputStreamReader;
import tokenizer.Tokenizer;
import tokenizer_http.HttpTokenizer;

public class WhatsAppTokenizer<T> extends HttpTokenizer<T> {

   /**
    * Get the next complete message if it exists, advancing the tokenizer to the next message.
    * @return the next complete message, and null if no complete message exist.
    */
	public T nextMessage() {
		T msg = super.nextMessage();
		return msg;
	}

   
   /**
    * @return whether the input stream is still alive.
    */
	public boolean isAlive() {
		return false;
	}
	
   /**
    * adding a bufferedReader from which the tokenizer reads the input.
    */
	public void addInputStream(InputStreamReader inputStreamReader) {
		super.addInputStream(inputStreamReader);
	}
}
