package com.thinkwin.order;

public class SomeRunnable2 implements Runnable {
	@Override
	public void run() {
		try{
			TestUtil.bytes2hex01(null);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		finally {
			System.out.println("execute complete.");
		}
	}
}
