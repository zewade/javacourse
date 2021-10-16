package cn.zewade.course.thread;

import java.util.concurrent.*;

/**
 * @author Wade
 * @date 2021-10-16
 * @description
 */
public class ThreadRunner4 {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		long start=System.currentTimeMillis();
		
		// 在这里创建一个线程或线程池，
		// 异步执行 下面方法 返回值 24157817
		FutureTask<Integer> task = new FutureTask<>(() -> {
			return sum();
		});
		Thread thread = new Thread(task);
		thread.start();
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Integer> future = executorService.submit(() -> {
			return sum();
		});
		
		// 确保  拿到result 并输出
		System.out.println("异步计算结果为："+future.get());
		
		System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
		
		// 然后退出main线程
		executorService.shutdown();
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
