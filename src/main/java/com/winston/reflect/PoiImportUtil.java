package com.winston.reflect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PoiImportUtil {

	    //正则表达式 用于匹配属性的第一个字母
	    private static final String REGEX = "[a-zA-Z]";

	    /**<pre>
	     * 功能: Excel数据导入到数据库
	     * 参数: originUrl Excel表的所在路径 
	     * 参数: startRow 从第几行开始  0开始算
	     * 参数: endRow[到第几行结束
	     *                  (0表示所有行;
	     *                  正数表示到第几行结束;
	     *                  负数表示到倒数第几行结束)]
	     * 参数: clazz[要返回的对象集合的类型]
	     * 匹配属性的时候从前往后匹配，所以把需要设置的属性放在前面
	     * attrributeCount 设置属性的个数
	     * </pre>
	     */
	    public static List<Object> doImportExcel(String originUrl,int startRow,int endRow,Class<?> clazz,int attrributeCount) throws IOException {
	        // 判断文件是否存在
	        File file = new File(originUrl);
	        if (!file.exists()) {
	            throw new IOException("文件名为" + file.getName() + "Excel文件不存在！");
	        }
	        HSSFWorkbook wb = null;
	        FileInputStream fis=null;
	        List<Row> rowList = new ArrayList<Row>();
	        try {
	            fis = new FileInputStream(file);
	            // 读Excel里面的内容
	            wb = new HSSFWorkbook(fis);
	            //获取excel工作空间
	            Sheet sheet = wb.getSheetAt(0);
	            // 获取最后行号
	            int lastRowNum = sheet.getLastRowNum();
	            Row row = null;
	            
	            // 循环读取获取每一行
	            for (int i = startRow; i <= lastRowNum + endRow; i++) {
	                row = sheet.getRow(i);
	                if (row != null) {
	                	
	                    rowList.add(row);
	                }
	            }
	        } catch (IOException e) {
	        	
	            e.printStackTrace();
	            
	        } finally{
	        	
	        	try{
	        		
	        		if(fis != null){
	        			
	        			fis.close();
	        		}
	        		
	        	}catch(Exception e){
	        		
	        		e.printStackTrace();
	        	}
	        }
	        //调用
	        return returnObjectList(rowList,clazz,attrributeCount);
	    }

	    /**
	     * 功能:获取单元格的值
	     */
	    private static String getCellValue(Cell cell) {
	        Object result = "";
	        if (cell != null) {
	            switch (cell.getCellType()) {
	            case Cell.CELL_TYPE_STRING:
	                result = cell.getStringCellValue();
	                break;
	            case Cell.CELL_TYPE_NUMERIC:
	                result = cell.getNumericCellValue();
	                break;
	            case Cell.CELL_TYPE_BOOLEAN:
	                result = cell.getBooleanCellValue();
	                break;
	            case Cell.CELL_TYPE_FORMULA:
	                result = cell.getCellFormula();
	                break;
	            case Cell.CELL_TYPE_ERROR:
	                result = cell.getErrorCellValue();
	                break;
	            case Cell.CELL_TYPE_BLANK:
	                break;
	            default:
	                break;
	            }
	        }
	        return result.toString();
	    }

	    /**
	     * 功能:返回指定的对象集合
	     */
	    private static List<Object> returnObjectList(List<Row> rowList,Class<?> clazz,int attributeCount) {
	        List<Object> objectList=null;
	        Object obj=null;
	        String attribute=null;
	        String value=null;
	        int j=0;
	        try {   
	            objectList=new ArrayList<Object>();
	            //获取所有的属性，不论private还是public
	            Field[] declaredFields = clazz.getDeclaredFields();
	            //遍历每一行
	            for (Row row : rowList) {
	                j=0;
	                obj = clazz.newInstance();
	                for (Field field : declaredFields) {    
	                    attribute=field.getName().toString();
	                    //获取单元格的值
	                    value = getCellValue(row.getCell(j));
	                    //给属性赋值
	                    setAttrributeValue(obj,attribute,value);    
	                    j++;
	                    //只需要读取到我们指定多少个属性个数
	                    if(j == attributeCount){
	                    	
	                    	break;
	                    }
	                }
	                objectList.add(obj);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return objectList;
	    }

	    /**
	     * 功能:给指定对象的指定属性赋值
	     */
	    private static void setAttrributeValue(Object obj,String attribute,String value) {
	        //得到该属性的set方法名
	        String methodName = convertToMethodName(attribute,obj.getClass(),true);
	        //通过反射获取到所有的方法
	        Method[] methods = obj.getClass().getMethods();
	        for (Method method : methods) {
	            /**
	             * 因为这里只是调用bean中属性的set方法，属性名称不能重复
	             * 所以set方法也不会重复，所以就直接用方法名称去锁定一个方法
	             * 匹配 方法
	            */
	            if(method.getName().equals(methodName))
	            {
	            	//获取方法的参数类型
	                Class<?>[] parameterC = method.getParameterTypes();
	                try {
	                    /**如果是(整型,浮点型,布尔型,字节型,时间类型),
	                     * 按照各自的规则把value值转换成各自的类型
	                     * 否则一律按类型强制转换(比如:String类型)
	                    */
	                    if(parameterC[0] == int.class || parameterC[0]==java.lang.Integer.class)
	                    {
	                        value = value.substring(0, value.lastIndexOf("."));
	                        //调用方法
	                        method.invoke(obj,Integer.valueOf(value));
	                        break;
	                    }else if(parameterC[0] == float.class || parameterC[0]==java.lang.Float.class){
	                        method.invoke(obj, Float.valueOf(value));
	                        break;
	                    }else if(parameterC[0] == double.class || parameterC[0]==java.lang.Double.class)
	                    {
	                        method.invoke(obj, Double.valueOf(value));
	                        break;
	                    }else if(parameterC[0] == byte.class || parameterC[0]==java.lang.Byte.class)
	                    {
	                        method.invoke(obj, Byte.valueOf(value));
	                        break;
	                    }else if(parameterC[0] == boolean.class|| parameterC[0]==java.lang.Boolean.class)
	                    {
	                        method.invoke(obj, Boolean.valueOf(value));
	                        break;
	                    }else if(parameterC[0] == java.util.Date.class)
	                    {
	                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                        Date date=null;
	                        try {
	                            date=sdf.parse(value);
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                        method.invoke(obj,date);
	                        break;
	                    }else
	                    {
	                        method.invoke(obj,parameterC[0].cast(value));
	                        break;
	                    }
	                } catch (IllegalArgumentException e) {
	                    e.printStackTrace();
	                } catch (IllegalAccessException e) {
	                    e.printStackTrace();
	                } catch (InvocationTargetException e) {
	                    e.printStackTrace();
	                } catch (SecurityException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

	    /**
	     * 功能:根据属性生成对应的set/get方法
	     */
	    private static String convertToMethodName(String attribute,Class<?> objClass,boolean isSet) {
	        /** 通过正则表达式来匹配第一个字符 **/
	        Pattern p = Pattern.compile(REGEX);
	        Matcher m = p.matcher(attribute);
	        StringBuilder sb = new StringBuilder();
	        /** 如果是set方法名称 **/
	        if(isSet)
	        {
	            sb.append("set");
	        }else{
	        /** get方法名称 **/
	            try {
	                Field attributeField = objClass.getDeclaredField(attribute);
	                /** 如果类型为boolean **/
	                if(attributeField.getType() == boolean.class||attributeField.getType() == Boolean.class)
	                {
	                    sb.append("is");
	                }else
	                {
	                    sb.append("get");
	                }
	            } catch (SecurityException e) {
	                e.printStackTrace();
	            } catch (NoSuchFieldException e) {
	                e.printStackTrace();
	            }
	        }
	        /** 针对以下划线开头的属性 **/
	        if(attribute.charAt(0)!='_' && m.find())
	        {
	            sb.append(m.replaceFirst(m.group().toUpperCase()));
	        }else{
	            sb.append(attribute);
	        }
	        return sb.toString();
	    }
	    
	    public static void main(String[] args) {
			
	 	   try {
	 				List<Object> doImportExcel = PoiImportUtil.doImportExcel("D://user.xls", 1, 0, UserEntity.class,3);
	 				/**
	 				 * UserEntity [name=张三, age=1, tel=1.54545445E8]
					   UserEntity [name=李四, age=2, tel=1.5454545E7]
                       UserEntity [name=王五, age=3, tel=154545.0]
	 				 */
	 				for (Object object : doImportExcel) {
						
	 					System.out.println(object.toString());
					}
	 			} catch (IOException e) {

	 				e.printStackTrace();
	 			}
		}
}
