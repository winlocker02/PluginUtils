package ru.winlocker.utils.config.annotations;

import java.lang.annotation.*;

/**
 * Indicates that this type can be
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface ConfigSubclassable {
}
