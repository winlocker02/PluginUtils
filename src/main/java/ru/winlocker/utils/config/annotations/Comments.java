package ru.winlocker.utils.config.annotations;

import java.lang.annotation.*;

/**
 * A wrapper for multiple {@link Comment} annotations
 * @author Redempt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Comments {
	
	Comment[] value();
	
}
