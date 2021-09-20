package com.zewade.course;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Wade
 * @date 2021-09-20
 * @description
 *
 * 作业2（必做）
 *
 * 打开 Spring 官网: https://spring.io/
 * 找到 Projects --> Spring Initializr: https://start.spring.io/
 * 填写项目信息, 生成 maven 项目; 下载并解压。
 * Idea或者Eclipse从已有的Source导入Maven项目。
 * 从课件资料中找到资源 Hello.xlass 文件并复制到 src/main/resources 目录。
 * 编写代码，实现 findClass 方法，以及对应的解码方法
 * 编写main方法，调用 loadClass 方法；
 * 创建实例，以及调用方法
 * 执行.
 */
public class MyClassLoader extends ClassLoader {
	
	/**
	 * 作业相关的类按作业要求加载，其他则使用默认加载方法
	 */
	private static final List<String> courseClasses = Arrays.asList("Hello");
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (courseClasses.contains(name)) {
			InputStream inputStream = this.getResourceAsStream(String.format("%s.xlass", name));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
				byte[] buffer = new byte[1];
				while (inputStream.read(buffer) != -1) {
					buffer[0] = (byte) (255 - buffer[0]);
					outputStream.write(buffer, 0, 1);
				}
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return defineClass(name, outputStream.toByteArray(), 0, outputStream.toByteArray().length);
		} else {
			return super.findClass(name);
		}
	}
	
	public static void main(String[] args) {
		try {
			Object myClass = new MyClassLoader().loadClass("Hello").newInstance(); // 加载并初始化Hello类
			Method helloMethod = myClass.getClass().getMethod("hello");
			helloMethod.invoke(myClass);
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
