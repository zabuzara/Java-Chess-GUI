package www.zabuzara.com.tools;

import java.sql.SQLException;


/**
 * Unchecked exception wrapper for SQL exceptions.
 * @author Sascha Baumeister
 */
public class UncheckedSQLException extends RuntimeException {
	static private final long serialVersionUID = 1L;

	public UncheckedSQLException () {
		super();
	}

	public UncheckedSQLException (final String message, final SQLException cause) {
		super(message, cause);
	}

	public UncheckedSQLException (final String message) {
		super(message);
	}

	public UncheckedSQLException (final SQLException cause) {
		super(cause);
	}


	@Override
	public SQLException getCause () {
		return (SQLException) super.getCause();
	}


	@Override
	public Throwable initCause (Throwable cause) {
		if (!(cause instanceof SQLException)) throw new IllegalArgumentException();
		return super.initCause(cause);
	}
}