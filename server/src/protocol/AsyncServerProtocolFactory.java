package protocol;

public interface AsyncServerProtocolFactory<T> {
   AsyncServerProtocol<T> create();
}
