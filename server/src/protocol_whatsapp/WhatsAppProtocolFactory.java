package protocol_whatsapp;

import protocol.ServerProtocol;
import protocol_http.HttpProtocolFactory;

public class WhatsAppProtocolFactory<T> extends HttpProtocolFactory<T> {

	public ServerProtocol<T> create() {
		return new WhatsAppProtocol<T>();
	}
}
