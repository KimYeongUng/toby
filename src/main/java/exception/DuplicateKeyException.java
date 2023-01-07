package exception;

public class DuplicateKeyException extends RuntimeException{
    public DuplicateKeyException(Throwable cause){
        super(cause);
    }
}
