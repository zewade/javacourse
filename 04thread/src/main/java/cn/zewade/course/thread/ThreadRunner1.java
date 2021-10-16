package cn.zewade.course.thread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Wade
 * @date 2021-10-16
 * @description
 */
public class ThreadRunner1 {
	
	private static int result = 0;
	private static String flag = "doing";
	
	public static void main(String[] args) throws InterruptedException {
		
		long start=System.currentTimeMillis();
		
		// 在这里创建一个线程或线程池，
		// 异步执行 下面方法 返回值 24157817
		Thread thread = new Thread(() -> {
			result = sum();
			flag = "done";
		});
		thread.start();
		
		while (true) {
			Thread.sleep(1);
			if (flag.equals("done")) {
				System.out.println("done");
				break;
			}
		}
		
		// 确保  拿到result 并输出
		System.out.println("异步计算结果为："+result);
		
		System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
		
		// 然后退出main线程
	}
	
	private static int sum() {
		return fibo(36);
	}
	
	private static int fibo(int a) {
		if ( a < 2)
			return 1;
		return fibo(a-1) + fibo(a-2);
	}
}
