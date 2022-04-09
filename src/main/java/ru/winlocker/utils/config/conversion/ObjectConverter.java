package ru.winlocker.utils.config.conversion;

import ru.winlocker.utils.config.*;
import ru.winlocker.utils.config.data.*;
import ru.winlocker.utils.config.instantiation.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * A converter which builds objects from configuration sections
 * @author Redempt
 */
public class ObjectConverter {
	
	/**
	 * Creates an object converter
	 * @param manager The ConversionManager handling converters
	 * @param type The config type to convert
	 * @param <T> The type
	 * @return An object converter for the given type
	 */
	public static <T> TypeConverter<T> create(ConversionManager manager, ConfigType<?> type) {
		if (type.getType().isInterface() || Modifier.isAbstract(type.getType().getModifiers())) {
			throw new IllegalStateException("Cannot automatically convert abstract classe or interface " + type.getType());
		}
		Instantiator instantiator = Instantiator.getInstantiator(type.getType());
		FieldSummary summary = FieldSummary.getFieldSummary(manager, type.getType(), false);
		return new TypeConverter<T>() {
			@Override
			public T loadFrom(DataHolder section, String path, T currentValue) {
				DataHolder newSection = path == null ? section : section.getSubsection(path);
				List<Object> objs = new ArrayList<>();
				for (ConfigField field : summary.getFields()) {
					Object value = summary.getConverters().get(field).loadFrom(newSection, field.getName(), null);
					objs.add(value);
				}
				return (T) instantiator.instantiate(manager, currentValue, type.getType(), objs, path, summary);
			}
			
			@Override
			public void saveTo(T t, DataHolder section, String path) {
				saveTo(t, section, path, true);
			}
			
			@Override
			public void saveTo(T t, DataHolder section, String path, boolean overwrite) {
				if (path != null && section.isSet(path) && !overwrite) {
					return;
				}
				DataHolder newSection = path == null ? section : section.createSubsection(path);
				if (t == null) {
					return;
				}
				for (ConfigField field : summary.getFields()) {
					saveWith(summary.getConverters().get(field), field.get(t), newSection, field.getName(), overwrite);
				}
				summary.applyComments(newSection);
			}
		};
	}
	
	private static <T> void saveWith(TypeConverter<T> converter, Object obj, DataHolder section, String path, boolean overwrite) {
		converter.saveTo((T) obj, section, path, overwrite);
	}
	
}
