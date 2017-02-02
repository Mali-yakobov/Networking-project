package tokenizer_http;

import tokenizer.Tokenizer;
import tokenizer.TokenizerFactory;

public class HttpTokenizerFactory<T> implements TokenizerFactory<T> {

	public Tokenizer<T> create() {
		return new HttpTokenizer<T>();
	}
}