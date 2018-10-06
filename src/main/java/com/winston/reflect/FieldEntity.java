package com.winston.reflect;
/**
   属性反射测试实体类
 @Author Winston
 * 
 */
public class FieldEntity {

	private String fieldX = "fieldX";
	private String fieldY = "fieldY";
	
	public FieldEntity(){}

	@Override
	public String toString() {
		return "FieldEntity [fieldX=" + fieldX + ", fieldY=" + fieldY + "]";
	}
	
}
