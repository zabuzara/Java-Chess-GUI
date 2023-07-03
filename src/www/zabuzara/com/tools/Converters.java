package www.zabuzara.com.tools;


/**
 * Facade providing conversion operations.
 */
public class Converters {

	/**
	 * Prevents external instantiation.
	 */
	private Converters () {}


	/**
	 * Returns the given Euro value converted into Cent.
	 * @param euroValue the value in Euro
	 * @return the value converted into Cent
	 */
	static public long toCent (final double euroValue) {
		return Math.round(100L * euroValue);
	}


	/**
	 * Returns the given Cent value converted into Euro.
	 * @param centValue the value in Cent
	 * @return the value converted into Euro
	 */
	static public double toEuro (final long centValue) {
		return 0.01 * centValue;
	}
}