package ru.winlocker.utils.commands;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDescription {

    String command();
    String permission() default "";
    String description() default "";

    boolean onlyPlayers() default false;
}
