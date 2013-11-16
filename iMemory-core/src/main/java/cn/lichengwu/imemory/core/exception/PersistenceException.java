package cn.lichengwu.imemory.core.exception;

/**
 * exception while persist Object
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-16 3:37 PM
 */
public class PersistenceException extends Exception {
    private static final long serialVersionUID = -4554380145004069160L;

    public PersistenceException() {
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
