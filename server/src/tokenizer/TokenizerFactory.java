package tokenizer;

public interface TokenizerFactory<T> {
   Tokenizer<T> create();
}
