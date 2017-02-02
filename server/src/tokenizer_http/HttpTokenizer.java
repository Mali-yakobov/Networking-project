package tokenizer_http;

import java.lang.StringBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import tokenizer.Message;
import tokenizer.Tokenizer;

public class HttpTokenizer<T> implements Tokenizer<T> {

	InputStreamReader inputStreamReader;

   /**
    * Get the next complete message if it exists, advancing the tokenizer to the next message.
    * @return the next complete message, and null if no complete message exist.
    */
	public T nextMessage() {
		StringBuilder data = new StringBuilder();
		String messageContent = "";
		
		try
		{
			if (inputStreamReader.ready())
			{
				do
				{
					int c = inputStreamReader.read();
					data.append((char)c);					
				}while(!data.toString().endsWith("$"));
			}
			else
			{
				return null;
			}			

			return (T)data.toString();
		}
		catch(IOException ex)
		{
			System.out.println("IO Error: "+ex.getMessage());
			return null;
		}
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
		this.inputStreamReader = inputStreamReader;
	}
}
