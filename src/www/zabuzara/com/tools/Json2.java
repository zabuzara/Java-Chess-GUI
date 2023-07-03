package www.zabuzara.com.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JSON facade providing parse and stringify operations.
 * @author Sascha Baumeister
 */
public class Json2 {
	static private final char STRING_DELIMITER_1 = '\'';
	static private final char STRING_DELIMITER_2 = '"';
	static private final char ARRAY_DELIMITER_1 = '[';
	static private final char ARRAY_DELIMITER_2 = ']';
	static private final char MAP_DELIMITER_1 = '{';
	static private final char MAP_DELIMITER_2 = '}';
	static private final char ELEMENT_SEPARATOR = ',';
	static private final char KEY_VALUE_SEPARATOR = ':';

	/**
	 * Prevents external instantiation.
	 */
	private Json2 () {}


	/**
	 * Returns the JSON representation for the given object.
	 * @param object the object, or null
	 * @return the JSON representation
	 * @throws IllegalArgumentException if the object, or one of it's constituents, is neither
	 *         null nor an instance of Boolean, Number, String, List, or Map
	 */
	static public String stringify (Object object) {
		if (object == null) return "null";
		if (object instanceof Boolean | object instanceof Number) return object.toString();
		if (object instanceof String) return object.toString().contains(Character.toString(STRING_DELIMITER_2))
			? STRING_DELIMITER_1 + object.toString() + STRING_DELIMITER_1
			: STRING_DELIMITER_2 + object.toString() + STRING_DELIMITER_2;
		if (object instanceof List) object = ((List<?>) object).toArray();

		final StringBuilder builder = new StringBuilder();

		if (object.getClass().isArray() && object.getClass().getComponentType() == Object.class) {
			builder.append(ARRAY_DELIMITER_1);
			final Object[] array = (Object[]) object;
			for (int index = 0; index < array.length; ++index) {
				if (index > 0) builder.append(ELEMENT_SEPARATOR + " ");
				builder.append(stringify(array[index]));
			}
			builder.append(ARRAY_DELIMITER_2);
			return builder.toString();
		}

		if (object instanceof Map) {
			builder.append(MAP_DELIMITER_1);

			@SuppressWarnings("unchecked")
			final Map<String,Object> map = (Map<String,Object>) object;
			for (final String key : map.keySet()) {
				final Object value = map.get(key);
				builder.append(stringify(key));
				builder.append(KEY_VALUE_SEPARATOR + " ");
				builder.append(stringify(value));
				builder.append(ELEMENT_SEPARATOR + " ");
			}
			if (!map.isEmpty()) builder.delete(builder.length() - 2, builder.length());
			builder.append(MAP_DELIMITER_2);
			return builder.toString();
		}

		throw new IllegalArgumentException();
	}


	/**
	 * Converts the JSON representation into the content of a newly created map.
	 * @param json the JSON representation
	 * @return the map
	 * @throws IllegalArgumentException if the given JSON does not represent a valid JSON map
	 */
	@SuppressWarnings("unchecked")
	static public Map<String,Object> parseMap (String json) {
		try {
			return (Map<String,Object>) parse(json);
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(e);
		}
	}


	/**
	 * Converts the JSON representation into the content of a newly created array.
	 * @param json the JSON representation
	 * @return the array
	 * @throws IllegalArgumentException if the given JSON does not represent a valid JSON array
	 */
	static public Object[] parseArray (String json) {
		try {
			return (Object[]) parse(json);
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(e);
		}
	}


	/**
	 * Converts the JSON representation into the content of a newly created string.
	 * @param json the JSON representation
	 * @return the string
	 * @throws IllegalArgumentException if the given JSON does not represent a valid JSON string
	 */
	static public String parseString (String json) {
		try {
			return (String) parse(json);
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(e);
		}
	}


	/**
	 * Converts the JSON representation into the content of a newly created numeric value.
	 * @param json the JSON representation
	 * @return the numeric value
	 * @throws IllegalArgumentException if the given JSON does not represent a valid JSON number
	 */
	static public double parseNumber (String json) {
		try {
			return ((Number) parse(json)).doubleValue();
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(e);
		}
	}


	/**
	 * Converts the JSON representation into the content of a newly created boolean value.
	 * @param json the JSON representation
	 * @return the Boolean value
	 * @throws IllegalArgumentException if the given JSON does not represent a valid JSON boolean
	 */
	static public boolean parseBoolean (String json) {
		try {
			return (Boolean) parse(json);
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(e);
		}
	}


	/**
	 * Returns an object parsed from the given JSON representation.
	 * @param json the JSON representation
	 * @return the object, or null
	 * @throws NullPointerException if the given argument is null
	 * @throws IllegalArgumentException if the argument is not valid JSON
	 */
	static public Object parse (String json) {
		json = json.trim();

		if (json.isEmpty()) throw new IllegalArgumentException();
		if (json.equals("null") | json.equals("undefined")) return null;
		if (json.equals("true")) return true;
		if (json.equals("false")) return false;
		
		switch (json.charAt(0)) {
			case STRING_DELIMITER_1: {
				if (json.charAt(json.length() - 1) != STRING_DELIMITER_1) throw new IllegalArgumentException();
				return json.substring(1, json.length() - 1);
			}
			case STRING_DELIMITER_2: {
				if (json.charAt(json.length() - 1) != STRING_DELIMITER_2) throw new IllegalArgumentException();
				return json.substring(1, json.length() - 1);
			}
			case ARRAY_DELIMITER_1: {
				if (json.charAt(json.length() - 1) != ARRAY_DELIMITER_2) throw new IllegalArgumentException();
				final List<Object> list = new ArrayList<>();

				boolean withinStringLiteral1 = false, withinStringLiteral2 = false;
				for (int charIndex = 1, beginIndex = charIndex, depth = 0; charIndex < json.length() - 1; ++charIndex) {
					final char character = json.charAt(charIndex);

					if (charIndex == json.length() - 2) {
						final String text = json.substring(beginIndex, charIndex + 1);
						list.add(parse(text));
					} else if (!withinStringLiteral1 & !withinStringLiteral2 & depth == 0 & character == ELEMENT_SEPARATOR) {
						final String text = json.substring(beginIndex, charIndex);
						list.add(parse(text));
						beginIndex = charIndex + 1;
					} else if (character == ARRAY_DELIMITER_1 | character == MAP_DELIMITER_1) {
						depth += 1;
					} else if (character == ARRAY_DELIMITER_2 | character == MAP_DELIMITER_2) {
						depth -= 1;
					} else if (character == STRING_DELIMITER_1) {
						if (!withinStringLiteral1 | json.charAt(charIndex - 1) != '\\') withinStringLiteral1 = !withinStringLiteral1;
					} else if (character == STRING_DELIMITER_2) {
						if (!withinStringLiteral2 | json.charAt(charIndex - 1) != '\\') withinStringLiteral2 = !withinStringLiteral2;
					}
				}

				return list.toArray();
			}
			case MAP_DELIMITER_1: {
				if (json.charAt(json.length() - 1) != MAP_DELIMITER_2) throw new IllegalArgumentException();
				final Map<String,Object> map = new HashMap<>();

				boolean withinStringLiteral1 = false, withinStringLiteral2 = false;
				for (int charIndex = 1, beginIndex = charIndex, depth = 0; charIndex < json.length() - 1; ++charIndex) {
					final char character = json.charAt(charIndex);

					if (charIndex == json.length() - 2) {
						final String text = json.substring(beginIndex, charIndex + 1);
						final int delimiterPosition = text.indexOf(':');
						if (delimiterPosition == -1) throw new IllegalArgumentException();

						final Object key = parse(text.substring(0, delimiterPosition));
						final Object value = parse(text.substring(delimiterPosition + 1));
						if (!(key instanceof String)) throw new IllegalArgumentException();
						map.put((String) key, value);
					} else if (!withinStringLiteral1 & !withinStringLiteral2 & depth == 0 & character == ELEMENT_SEPARATOR) {
						final String text = json.substring(beginIndex, charIndex);
						final int delimiterPosition = text.indexOf(':');
						if (delimiterPosition == -1) throw new IllegalArgumentException();

						final Object key = parse(text.substring(0, delimiterPosition));
						final Object value = parse(text.substring(delimiterPosition + 1));
						if (!(key instanceof String)) throw new IllegalArgumentException();
						map.put((String) key, value);
						beginIndex = charIndex + 1;
					} else if (character == ARRAY_DELIMITER_1 | character == MAP_DELIMITER_1) {
						depth += 1;
					} else if (character == ARRAY_DELIMITER_2 | character == MAP_DELIMITER_2) {
						depth -= 1;
					} else if (character == STRING_DELIMITER_1) {
						if (!withinStringLiteral1 | json.charAt(charIndex - 1) != '\\') withinStringLiteral1 = !withinStringLiteral1;
					} else if (character == STRING_DELIMITER_2) {
						if (!withinStringLiteral2 | json.charAt(charIndex - 1) != '\\') withinStringLiteral2 = !withinStringLiteral2;
					}
				}

				return map;
			}
			default: {
				try {
					return Long.parseLong(json);
				} catch (final NumberFormatException e) {
					return Double.parseDouble(json);
				}
			}
		}
	}
}