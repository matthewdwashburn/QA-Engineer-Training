package com.revature.expensemanager.allure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.qameta.allure.LabelAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@LabelAnnotation(name = "parentSuite")
public @interface ParentSuite {
    String value();
}
