package cn.toesbieya.jxc.annoation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    String[] value() default {};
}
