package ch.set.ucd.ucd4u.exception;

public class ComponentExistsException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -5903340062798308248L;

    public ComponentExistsException(String errorMessage) {
        super(errorMessage);
    }
}