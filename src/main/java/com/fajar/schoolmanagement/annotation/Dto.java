package com.fajar.schoolmanagement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fajar.schoolmanagement.dto.FormInputColumn;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.TYPE)  
public @interface Dto {

	FormInputColumn formInputColumn() default FormInputColumn.TWO_COLUMN;
	boolean ignoreBaseField() default true;
	boolean editable() default true;
}
