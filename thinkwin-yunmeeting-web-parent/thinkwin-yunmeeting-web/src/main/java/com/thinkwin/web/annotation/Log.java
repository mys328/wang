package com.thinkwin.web.annotation;

import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;

import java.lang.annotation.*;

/**
 * Log?自定义注解
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 业务类型
     *
     * @return 业务类型
     */
    BusinessType businessType() default BusinessType.general;

    /****
     *<p>功能描述：事件类型 </p>
     * @return 事件类型
     */
    EventType eventType() default EventType.general;

    /*
    * 日志内容
    * */
    String content() default "";

}
