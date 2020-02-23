package com.toesbieya.my.annoation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Transactional(rollbackFor = Exception.class)
public @interface Tx {
}
