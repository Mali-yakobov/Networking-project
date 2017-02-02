import java.io.IOException;
import java.net.ServerSocket;

import threadPerClient.MultipleClientProtocolServer;
import protocol_whatsapp.WhatsAppProtocolFactory;
import tokenizer_whatsaap.WhatsAppTokenizerFactory;

import protocol_http.HttpProtocolFactory;
import tokenizer_http.HttpTokenizerFactory;

public class server {

	public static void main(String[] args) {

		if (args.length>0) {
			// Get port
			int port = Integer.decode(args[0]).intValue();

			MultipleClientProtocolServer<String> appserver = new MultipleClientProtocolServer(port, new WhatsAppProtocolFactory<String>(), new WhatsAppTokenizerFactory<String>());

			Thread serverThread = new Thread(appserver);
			serverThread.start();

			try {
				serverThread.join();
			}
			catch (InterruptedException e)
			{
				System.out.println("Server stopped");
			}
		}		
	}

}

