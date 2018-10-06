package com.winston.reflect;
/**
 *用户类
 @Author Winston
 */
public class User {
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 年龄
	 */
	private int age;
	private User() {
		super();
		System.out.println("无参构造方法且是private");
	}
	
	public User(String name, int age) {
		super();
		this.name = name;
		this.age = age;
		System.out.println("有参构造方法");
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + "]";
	}
	
}
