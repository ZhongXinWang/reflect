package com.winston.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 入口类
 * @author winston
 */
public class App 
{
    public static void main( String[] args )
    {

    	try {
    		
			testReflectConstructor();
			changeFieldContent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    /**
     *修改属性值的内容 
     @Author Winston
     @email 940945444@qq.com
     @return
     @param
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    private static void changeFieldContent() throws IllegalArgumentException, IllegalAccessException {

    	FieldEntity fieldObj = new FieldEntity();
    	//结果：FieldEntity [fieldX=fieldX, fieldY=fieldY]
    	 System.out.println(fieldObj.toString());
    	//通过 对象.getClass()方式来初始化
    	Class  clazz = fieldObj.getClass();
    	//获取所有的属性(无论是private还是public)
    	Field[] declaredFields = clazz.getDeclaredFields();
    	String fieldName = "fieldX";
    	//遍历所有的属性
    	for (Field item : declaredFields) {
			
    		//判断当前属性类型是否是String,比较字节码采用的是 ==
    		if(item.getType() == String.class && fieldName.equals(item.getName())){
    			//设置可以通过private修饰
    			item.setAccessible(true);
    			//修改值,第一个参数是指明那个对象，第二个参数是需要修改的值
    			item.set(fieldObj, "hello java");
    		}
		}
    	//结果：FieldEntity [fieldX=hello java, fieldY=fieldY]
    	System.out.println(fieldObj.toString());
    	
	}

	/**
     *测试通过构造方法反射获取对象
     @Author Winston
     @email 940945444@qq.com
     @return
     @param
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
	private static void testReflectConstructor() throws Exception{
		
		/*
		 * 通过 类名.class获取到Class对象,在获取到类的构造方法，并指定调用那个构造方法
		 * User.class.getConstructor(),不传参数表示调用的是无参的构造方法
		 * 调用公有且有参的构造方法
		 */
		//获取有参的构造方法 写法一
		//Constructor<User> constructor1 = User.class.getConstructor(String.class,int.class);
		//有参的构造方法 写法二
		Constructor<User> constructor1 = User.class.getConstructor(new Class[]{String.class,int.class});
		/*
		 * 根据构造方法获得对象，并初始化,提供两种方式
		 */
		//设置参数 写法一
		//User userParam = constructor1.newInstance("张三",20);
		//设置参数 写法二
		User userParam = constructor1.newInstance(new Object[]{"张三",20});
		//输出结果
		System.out.println(userParam.toString());
		/*
		 * 通过Class.forName("com.winston.reflect.User") 获取到Class对象
		 * getDeclaredConstructor()获取到所有公有，私有的构造方法
		 * 并调用无参，且私有的构造方法
		 */
		Constructor<User> constructor2 = (Constructor<User>) Class.forName("com.winston.reflect.User").getDeclaredConstructor();
		//反射private修饰的方法,需要constructor2.setAccessible(true);设置通行证，暴力访问
		constructor2.setAccessible(true);
		
		User user2 = constructor2.newInstance();
	}
}
