package com.winston.reflect;
/**
 用户实体类，用于导入
 @Author Winston
 @date 2018年10月6日
 * 
 */
public class UserEntity {

	/**
	 * 用户名
	 */
	private String name;
	/*
	 * 年龄
	 */
	private int age;
	/*
	 * 手机
	 */
	private String tel;
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
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	@Override
	public String toString() {
		return "UserEntity [name=" + name + ", age=" + age + ", tel=" + tel + "]";
	}
	
}
