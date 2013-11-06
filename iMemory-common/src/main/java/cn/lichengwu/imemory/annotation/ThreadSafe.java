package cn.lichengwu.imemory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method or class which tag this annotation must be thread safe.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-06 10:52 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ThreadSafe {
}
