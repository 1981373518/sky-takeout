package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
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

    @Pointcut("execution(* com.sky.mapper.*.* (..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut(){

    }

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint){
        // 1.得到运行方法的操作类型
        // (1).获取方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // (2).通过签名得到Method方法，然后拿到注解
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        // (3).拿到注解里的值
        OperationType value = annotation.value();

        // 2.得到参数类型
        Object[] args = joinPoint.getArgs();
        if (args == null){return;}
        Object arg = args[0];

        // 3.准备赋值数据
        LocalDateTime current = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();

        // 4.根据方法类型赋值
        if (value == OperationType.INSERT){
            try {
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                setCreateTime.invoke(arg,current);
                setUpdateTime.invoke(arg,current);
                setCreateUser.invoke(arg,id);
                setUpdateUser.invoke(arg,id);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
