package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
//import jdk.jpackage.internal.Log;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    //拦截 com.sky.mapper 包下所有类的所有方法，并且这些方法必须标注了 @AutoFill 注解
    //execution(public String com.example.service.UserService.saveUser(String, Long))
    //            ↑      ↑      ↑                                ↑         ↑
    //        访问修饰符 返回类型  完整类名                        方法名    参数类型
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }
    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开启自动填充");
        //获取当前被拦截方法的数据库操作类型（update/insert）

        //方法签名是用来唯一标识一个方法的信息，包含方法名，参数类型列表，返回类型等
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //getMethod()获取Method对象；getAnnotation() 方法需要知道你要获取哪种类型的注解
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = annotation.value();

        //获取当前被拦截的方法的参数：实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {return;}
        Object entity = args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据不同的操作类型，为对应的属性通过反射赋值
        if(OperationType.INSERT.equals(value)){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreatTime",LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreatUser", Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        if(OperationType.UPDATE.equals(value)){
            try {
                //方法名称以及需要的参数类型
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
