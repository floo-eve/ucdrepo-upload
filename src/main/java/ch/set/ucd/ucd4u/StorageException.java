package ch.set.ucd.ucd4u;

public class StorageException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 580925718375418667L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}