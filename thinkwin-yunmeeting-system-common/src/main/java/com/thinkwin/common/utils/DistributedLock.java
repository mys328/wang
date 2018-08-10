package com.thinkwin.common.utils;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {
	/**
	 * 获取锁，如果没有得到锁就一直等待
	 *
	 * @throws Exception
	 */
	public void acquire() throws Exception;

	/**
	 * 获取锁，如果没有得到锁就一直等待直到超时
	 *
	 * @param time 超时时间, 毫秒
	 *
	 * @return 是否获取到锁
	 * @throws Exception
	 */
	public boolean acquire(long time) throws Exception;

	/**
	 * 释放锁
	 *
	 * @throws Exception
	 */
	public void release() throws Exception;
}
