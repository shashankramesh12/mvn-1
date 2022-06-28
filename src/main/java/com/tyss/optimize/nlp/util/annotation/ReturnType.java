package com.tyss.optimize.nlp.util.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;



@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface ReturnType{
    String name();
    String type();
}

