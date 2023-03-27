package ru.nsu.fit.persistance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO special key

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializeField {
    public String Name() default "";
}
