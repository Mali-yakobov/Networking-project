package tokenizer_whatsaap;

import tokenizer.Tokenizer;
import tokenizer_http.HttpTokenizerFactory;

public class WhatsAppTokenizerFactory<T> extends HttpTokenizerFactory<T> {

	public Tokenizer<T> create() {
		return new WhatsAppTokenizer<T>();
	}
}