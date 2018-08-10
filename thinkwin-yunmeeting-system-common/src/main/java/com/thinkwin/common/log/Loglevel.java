package com.thinkwin.common.log;

/**
 * 日志级别 从低到高
 */
public enum Loglevel {

    /*
    *   该级别日志的主要作用是对系统每一步的运行状态进行精确的记录
    * */
    debug,
    /*
    * 警告 录系统的正常运行状态
    * */
    info,
    /*
    *
    * 警告日志表示系统可能出现问题，也可能没有
    * */
    warn,
    /*
    * 错误 该级别的错误也需要马上被处理
    * */
    error,
    /*
    * 需要立即被处理的系统级错误
    * */
    fatal;
}
