package ru.winlocker.utils.config;

import org.bukkit.inventory.*;
import org.bukkit.plugin.*;
import ru.winlocker.utils.config.annotations.*;
import ru.winlocker.utils.config.conversion.*;
import ru.winlocker.utils.config.conversion.bukkit.*;
import ru.winlocker.utils.config.conversion.inventory.*;
import ru.winlocker.utils.config.conversion.messages.*;
import ru.winlocker.utils.config.instantiation.*;
import ru.winlocker.utils.inventory.inventories.*;
import ru.winlocker.utils.messages.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class ConversionManager {
	
	private final Map<ConfigType<?>, TypeConverter<?>> convertersByType;
	private final Map<String, Class<?>> classesByName = new ConcurrentHashMap<>();
	private final ClassLoader loader;
	
	{
		convertersByType = new HashMap<>();
		convertersByType.put(new ConfigType<>(String.class), StringConverter.create(s -> s, s -> s));
		convertersByType.put(new ConfigType<>(int.class), PrimitiveConverter.create(Integer::parseInt, String::valueOf));
		convertersByType.put(new ConfigType<>(double.class), PrimitiveConverter.create(Double::parseDouble, String::valueOf));
		convertersByType.put(new ConfigType<>(float.class), PrimitiveConverter.create(Float::parseFloat, String::valueOf));
		convertersByType.put(new ConfigType<>(boolean.class), PrimitiveConverter.create(Boolean::parseBoolean, String::valueOf));
		convertersByType.put(new ConfigType<>(long.class), PrimitiveConverter.create(Long::parseLong, String::valueOf));
		convertersByType.put(new ConfigType<>(Integer.class), PrimitiveConverter.create(Integer::parseInt, String::valueOf));
		convertersByType.put(new ConfigType<>(Double.class), PrimitiveConverter.create(Double::parseDouble, String::valueOf));
		convertersByType.put(new ConfigType<>(Float.class), PrimitiveConverter.create(Float::parseFloat, String::valueOf));
		convertersByType.put(new ConfigType<>(Boolean.class), PrimitiveConverter.create(Boolean::parseBoolean, String::valueOf));
		convertersByType.put(new ConfigType<>(Long.class), PrimitiveConverter.create(Long::parseLong, String::valueOf));
	}
	
	public ConversionManager(Plugin plugin) {
		loader = plugin.getClass().getClassLoader();
	}
	
	/**
	 * Loads a class by name, using a cache
	 * @param name The name of the class to load
	 * @return The class
	 */
	public Class<?> loadClass(String name) {
		return classesByName.computeIfAbsent(name, k -> {
			try {
				return Class.forName(k, true, loader);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		});
	}
	
	/**
	 * Gets the TypeConverter for a given type
	 * @param type The ConfigType
	 * @param <T> The parameter type
	 * @return The TypeConverter, making a new one if none exists for the given type
	 */
	public <T> TypeConverter<T> getConverter(ConfigType<T> type) {
		if (type == null) {
			return null;
		}
		TypeConverter<T> converter = (TypeConverter<T>) convertersByType.get(type);
		if (converter == null) {
			converter = (TypeConverter<T>) createConverter(type);
			convertersByType.put(type, converter);
		}
		return converter;
	}
	
	/**
	 * Registers a string converter to this ConversionManager
	 * @param clazz The class type the converter is for
	 * @param loader A function to convert a string to the given type
	 * @param saver A function to convert the given type to a string
	 * @param <T> The type
	 * @return This ConversionManager
	 */
	public <T> ConversionManager addConverter(Class<T> clazz, Function<String, T> loader, Function<T, String> saver) {
		convertersByType.put(new ConfigType<>(clazz), StringConverter.create(loader, saver));
		return this;
	}
	
	/**
	 * Registers a converter to this ConversionManager
	 * @param type The type the converter is for
	 * @param converter The converter
	 * @param <T> The type
	 * @return This ConversionManager
	 */
	public <T> ConversionManager addConverter(ConfigType<T> type, TypeConverter<T> converter) {
		convertersByType.put(type, converter);
		return this;
	}
	
	/**
	 * Gets a StringConverter for a given type
	 * @param type The type to get a StringConverter for
	 * @param <T> The type
	 * @return The StringConverter associated with the given type
	 * @throws IllegalStateException If a StringConverter does not exist for the given type
	 */
	public <T> StringConverter<T> getStringConverter(ConfigType<T> type) {
		TypeConverter<T> keyConverter = getConverter(type);
		if (!(keyConverter instanceof StringConverter)) {
			throw new IllegalStateException("No appropriate string converter for key type " + type);
		}
		return (StringConverter<T>) keyConverter;
	}
	
	private TypeConverter<?> createConverter(ConfigType<?> type) {
		if(Messages.class.isAssignableFrom(type.getType())) {
			return MessagesConverter.create();
		}
		if(ConfigurableInventory.class.isAssignableFrom(type.getType())) {
			return ConfigurableInventoryConverter.create(type.getType());
		}
		if(ItemStack.class.isAssignableFrom(type.getType())) {
			return ItemStackConverter.create();
		}
		if (Enum.class.isAssignableFrom(type.getType())) {
			return EnumConverter.create(type.getType());
		}
		if (Collection.class.isAssignableFrom(type.getType())) {
			return CollectionConverter.create(this, type);
		}
		if (Map.class.isAssignableFrom(type.getType())) {
			return MapConverter.create(this, type);
		}
		if (type.getType().isAnnotationPresent(ConfigMappable.class) || Instantiator.isRecord(type.getType())) {
			Class<?> clazz = type.getType();
			boolean isAbstract = clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
			if (isAbstract || clazz.isAnnotationPresent(ConfigSubclassable.class)) {
				return SubclassConverter.create(this, clazz, isAbstract);
			}
			return ObjectConverter.create(this, type);
		}
		return NativeConverter.create();
	}
}
