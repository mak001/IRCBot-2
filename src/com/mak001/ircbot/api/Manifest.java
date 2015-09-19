package com.mak001.ircbot.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Manifest {

    /**
     * @return The full name of the plugin
     */
    String name();

    /**
     * @return The description of the plugin
     */
    String description() default "";

    /**
     * @return The version of the plugin
     */
    double version() default 1.0;

    /**
     * @return The authors of the plugin
     */
    String[] authors();

    /**
     * @return The website of the plugin
     */
    String website() default "";
}