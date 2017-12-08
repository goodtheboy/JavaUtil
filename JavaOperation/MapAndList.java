package com.sunsd.lfw.modules.agency.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapAndList {
	/**
	  * 使用Map和List的特性进行匹配:
	  * Map为key-value结构，不能放重复数据
	  * List可以放重复数据
	  * 使用String型id做key,List<Person>做value
	  * 遍历List<String>, map.get(String)则取出id == str 的List
	  */
	public static void main(String[] args) {
		MapAndList();
	}
	
	
	 private static void  MapAndList(){
	  //构造一个10万个Person对象的list
	  List<Person> personList = new ArrayList<Person>();
	  
	  for (int i = 0; i < 100000; i++) {
	   int j =10000;
	   personList.add(new Person((i%j) +"", "张三"+i));
	  }
	  //构造一个1万个String对象的list
	  List<String> strList = new ArrayList<String>();
	  for (int i = 0; i < 10000; i++) {
	   strList.add(i +"");
	  }
	  
	  long start = System.currentTimeMillis();
	  //利用Map和List的特性整理数据
	        Map<String, List<Person>> map = new HashMap<String, List<Person>>();
	        //将10W条数据根据id放入map
	        for(int i=0;i<personList.size();i++){
	         Person person = personList.get(i);
	             String id = person.getId();
	              if(map.containsKey(id)){
	                  map.get(id).add(person);
	              }else{
	                   List<Person> pList = new ArrayList<Person>();
	                   pList.add(person);
	                   //id为key,相同id的person的List为value
	                   map.put(id,pList);
	             }
	        }
	      //保存匹配结果
	        List<Person> resultList = new ArrayList<Person>();
	        //根据1W条str,map.get(str)取匹配上的数据
	        for (String str : strList) {
	            List<Person> pList = map.get(str);
	            if (pList != null) {
	                resultList.addAll(pList);
	            }
	        }
	        long end = System.currentTimeMillis();
	        System.out.println("map.size:" +map.size());
	        System.out.println("reslutList.size:"+ resultList.size());
	        System.out.println("10W+1W次循环耗时："+ (end - start) + "毫秒");
	        System.out.println("----------------------------");
	 }

	}

	/**
	 * Person实体类
	 */
	class Person{
	 private String id;
	 private String name;

	 public Person() {}
	 
	 public Person(String id, String name) {
	  this.id = id;
	  this.name = name;
	 }

	 @Override
	 public String toString() {
	  return this.id +"::>"+ this.name;
	 }
	 
	 public String getId() {
	  return id;
	 }
	 public void setId(String id) {
	  this.id = id;
	 }
	 public String getName() {
	  return name;
	 }
	 public void setName(String name) {
	  this.name = name;
	 }
	
}
