package ru.winlocker.utils.config.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a method should be run when an object is deserialized from config
 * @author Redempt
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigPostInit {
}
