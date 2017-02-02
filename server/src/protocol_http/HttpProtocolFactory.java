package protocol_http;

import protocol.ServerProtocol;
import protocol.ServerProtocolFactory;

public class HttpProtocolFactory<T> implements ServerProtocolFactory<T> {

	public ServerProtocol<T> create() {	
		return new HttpProtocol<T>();
	}
}
