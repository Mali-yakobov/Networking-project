import java.util.Scanner;
import java.util.logging.Logger;

import protocol.*;
import protocol.http.*;
import protocol.whatsapp.*;
import tokenizer.*;
import tokenizer.whatsapp.WhatsAppMessage;
import tokenizer.whatsapp.WhatsAppMessageTokenizer;

import reactor.*;

public class reactor
{
	static Reactor<WhatsAppMessage> reactor = null;

	/**
	 * Main program, used for demonstration purposes. Create and run a
	 * Reactor-based server for the Echo protocol. Listening port number and
	 * number of threads in the thread pool are read from the command line.
	 */
	public static void main(String args[]) {
		if (args.length != 2) {
			System.err.println("Usage: java Reactor <port> <pool_size>");
			System.exit(1);
		}

		try {
			int port = Integer.parseInt(args[0]);
			int poolSize = Integer.parseInt(args[1]);

			reactor = startWhatsAppServer(port, poolSize);

			new Thread()
			{
				public void run()
				{
					Scanner scanner = new Scanner(System.in);
					boolean stopped = false;
					do
					{
						System.out.println("Type exit to stop: ");
						stopped = scanner.next().toLowerCase().equals("exit");
					}while(!stopped);			
					System.out.println("Stopping...");
					protocol.whatsapp.Engine.gracefulClosing();
					if (reactor!=null)
						reactor.stopReactor();					
					System.out.println("Stopped...");
				}
			}.start();

			Thread thread = new Thread(reactor);
			thread.start();
			reactor.logReadyMessage();
			thread.join();			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Reactor<WhatsAppMessage> startWhatsAppServer(int port, int poolSize) throws Exception{
		AsyncServerProtocolFactory<WhatsAppMessage> protocolMaker = new AsyncServerProtocolFactory<WhatsAppMessage>() {
			public AsyncServerProtocol<WhatsAppMessage> create() {
				return new WhatsAppProtocol();
			}
		};

		MessageTokenizerFactory<WhatsAppMessage> tokenizerMaker = new MessageTokenizerFactory<WhatsAppMessage>() {
			public MessageTokenizer<WhatsAppMessage> create() {
				return new WhatsAppMessageTokenizer();
			}
		};

		Reactor<WhatsAppMessage> reactor = new Reactor<WhatsAppMessage>(port, poolSize, protocolMaker, tokenizerMaker);
		return reactor;
	}
}