package reactor;

import java.util.concurrent.ExecutorService;
import java.nio.channels.Selector;

import protocol.*;
import tokenizer.*;

/**
 * a simple data structure that hold information about the reactor, including getter methods
 */
public class ReactorData<T> {

    private final ExecutorService _executor;
    private final Selector _selector;
    private final AsyncServerProtocolFactory<T> _protocolMaker;
    private final MessageTokenizerFactory<T> _tokenizerMaker;
    
    public ExecutorService getExecutor() {
        return _executor;
    }

    public Selector getSelector() {
        return _selector;
    }

	public ReactorData(ExecutorService _executor, Selector _selector, AsyncServerProtocolFactory<T> protocol, MessageTokenizerFactory<T> tokenizer) {
		this._executor = _executor;
		this._selector = _selector;
		this._protocolMaker = protocol;
		this._tokenizerMaker = tokenizer;
	}

	public AsyncServerProtocolFactory<T> getProtocolMaker() {
		return _protocolMaker;
	}

	public MessageTokenizerFactory<T> getTokenizerMaker() {
		return _tokenizerMaker;
	}

}
