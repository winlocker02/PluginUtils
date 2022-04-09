package ru.winlocker.utils.config.conversion;

import ru.winlocker.utils.config.data.*;

import java.util.function.*;

/**
 * A converter which can convert to and from strings, but saves values directly instead of as strings
 * @author Redempt
 */
public class PrimitiveConverter {
	
	/**
	 * Creates a StringConverter which saves values directly rather than as strings
	 * @param loader The function to convert from strings
	 * @param saver The function to convert to strings
	 * @param <T> The type to convert
	 * @return The converter
	 */
	public static <T> StringConverter<T> create(Function<String, T> loader, Function<T, String> saver) {
		return new StringConverter<T>() {
			@Override
			public T fromString(String str) {
				return loader.apply(str);
			}
			
			@Override
			public String toString(T t) {
				return saver.apply(t);
			}
			
			@Override
			public void saveTo(T t, DataHolder section, String path) {
				section.set(path, t);
			}
		};
	}
	
}
