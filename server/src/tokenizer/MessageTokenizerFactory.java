package tokenizer;

public interface MessageTokenizerFactory<T> {
   MessageTokenizer<T> create();
}
