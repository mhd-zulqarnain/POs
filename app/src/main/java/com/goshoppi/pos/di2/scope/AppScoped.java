package com.goshoppi.pos.di2.scope;


import javax.inject.Scope;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Replacement scope for @Singleton to improve readability
 */
@Documented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AppScoped {
}