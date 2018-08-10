package com.thinkwin.web.annotation;

import com.thinkwin.TenantUser;
import com.thinkwin.common.log.*;
import com.thinkwin.common.model.log.SysLog;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.service.TenantContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

/**
 * log  切面
 * spring  AOP(Aspect-oriented programming) 是用于切面编程，简单的来说：AOP相当于一个拦截器，
 * 去拦截一些处理，例如：当一个方法执行的时候，Spring 能够拦截正在执行的方法，在方法执行的前或者后增加额外的功能和处理
 * 在Spring AOP中支持5中类型的通知：
 * 日志采集目前只采用 afterRunning 和 after  throwing
 */
@Aspect
@Component
public class SystemLogAspect {

    //注入dubboService用于把日志保存数据库
    @Resource
    private SysLogService sysLogService;

    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

    //Controller层切点
    @Pointcut("execution (* com.thinkwin.web.controller..*.*(..))")
    public void controllerAspect() {
    }

    /**
     * 一、前置通知 用于拦截Controller层记录用户的操作
     * 对于要增强的方法，在方法之前执行
     */
//    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("before " + joinPoint);
        }
    }

    /**
     * 二、后置通知 用于拦截Controller层记录用户的操作
     * 对于要增强的方法，在方法之后执行
     *
     * @param joinPoint 切点
     */
//    @After("controllerAspect()")
    public void after(JoinPoint joinPoint) {

    }


    /**
     * 三、异常通知 用于拦截记录异常日志
     * 对于要增强的方法，方法抛出异常后，对异常的增强处理，比如记录异常，或者根据异常分析数据什么的
     *
     * @param joinPoint
     * @param e         target执行后抛出的异常
     */
    @AfterThrowing(pointcut = "controllerAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        try {
            saveLog(joinPoint, e);
        } catch (Exception ex) {
            logger.error("业务日志切面抛出异常", ex);
        }
    }

    /*
    * 四、配置后置返回通知,使用在方法aspect()上注册的切入点
    * 对于要增加的方法，方法返回结果后，对结果进行记录或者分析
    * */
    @AfterReturning("controllerAspect()")
    public void afterReturn(JoinPoint joinPoint) {
        try {
            saveLog(joinPoint, null);
        } catch (Exception e) {
            logger.error("业务日志切面抛出异常", e);
        }
    }

    /*
     * 五、配置controller环绕通知,使用在方法aspect()上注册的切入点
     *  Around  advice 在方法执行前后和抛出异常时执行，相当于综合了以上三种通知
     * */
//    @Around("controllerAspect()")
    public void around(JoinPoint joinPoint) {
        try {
            ((ProceedingJoinPoint) joinPoint).proceed();
        } catch (Throwable e) {
        }
    }

    /*
    * 数据库日志记录
    * */
    private void saveLog(JoinPoint joinPoint, Throwable e) {
        BusinessType businessType = null;
        EventType eventType = null;
        String content = "";
        try {
            /*
            * 反射获取自定义注解
            * */
            String targetName = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] arguments = joinPoint.getArgs();
            Class targetClass = Class.forName(targetName);
            Method[] methods = targetClass.getMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    Class[] clazzs = method.getParameterTypes();
                    if (clazzs.length == arguments.length) {
                        Log annotation = method.getAnnotation(Log.class);
                        if (annotation != null) {
                            businessType = annotation.businessType();
                            eventType = annotation.eventType();
                            content = annotation.content();
                        }
                        break;
                    }
                }
            }

            //*========异步插入数据库日志=========*//
            //等于null 代表代码未使用自定义注解
            if (businessType != null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String userName = "";
                String userId = "";
                //未登录的时候auth为空
                if (auth != null) {
                    TenantUser tenantUser = (TenantUser) auth.getPrincipal();
                    userName = tenantUser.getUserName();
                    userId = tenantUser.getUserId();
                }

                SysLog log = new SysLog();
                log.setId(UUID.randomUUID().toString());
                log.setBusinesstype(businessType.toString());
                log.setBusinessname(TypeDesc.formatTypeDesc(businessType.toString()));
                log.setBusinessid("");
                log.setEventtype(eventType.toString());
                log.setEventname(TypeDesc.formatTypeDesc(eventType.toString()));
                log.setState(0);

                String contents = LogContext.getContents();
                //判断上下文
                if (contents != null) {
                    log.setContent(content);
                } else if (!"".equals(content)) {
                    //判断自定义注解
                    log.setContent(content);
                } else {
                    log.setContent("");
                }
                if (e != null) {
                    log.setResult(e.toString());
                } else {
                    log.setResult("");
                }

                log.setMethodname((joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()") + "." + businessType);
                log.setClassname(joinPoint.getTarget().getClass().getName());
                log.setMethodarg(joinPoint.getSignature().getName());
                log.setOperateUserId(userId);
                log.setOperatedate(new Date().toString());
                log.setOperator(userName);
                log.setIp(LocalIpUtil.getLocalIp());
                log.setTenantId(TenantContext.getTenantId());
                log.setSource("web");
                //保存数据库
                sysLogService.insertSysLog(log);
            }
        } catch (Exception e1) {
            //记录本地异常日志
            logger.error("异常信息:{}", e1.getMessage());
        }
    }
}
