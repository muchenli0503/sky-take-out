package com.sky.annotation;
import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 公共字段自动填充
 * 1. 自定义注解AutoFill，用于标识某个方法需要功能字段自动填充处理
 * 2. 自定义切面类AutoFillAspect, 统一拦截Autofill注解的方法
 * 3. 在Mapper加入AutoFill注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)// 运行时保留，这样反射才能获取
public @interface AutoFill {
    //数据库操作类型：UPDATE INSERT
    OperationType value();

}
