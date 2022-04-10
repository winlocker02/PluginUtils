package ru.winlocker.utils.config.conversion;

import com.google.common.base.*;

import java.util.*;

/**
 * A converter which converts enum values
 * @author Redempt
 */
public class EnumConverter {
	
	/**
	 * Creates an enum converter
	 * @param clazz The enum class
	 * @param <T> The type
	 * @return A StringConverter for the given enum type
	 */
	public static <T extends Enum> StringConverter<T> create(Class<?> clazz) {
		Class<T> enumClass = (Class<T>) clazz;
		return new StringConverter<T>() {
			@Override
			public T fromString(String str) {
				return (T) Enums.getIfPresent(enumClass, str.toUpperCase(Locale.ROOT)).orNull();
			}
			
			@Override
			public String toString(T t) {
				return t == null ? null : t.name();
			}
		};
	}
	
}
