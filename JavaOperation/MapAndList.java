package com.sunsd.lfw.modules.agency.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MapAndList {
	
	/**
	 * 获取HashMap中交集/反转于ArrayList的key值,inversion做控制参数
	 * @param al
	 * @param hm
	 * @param inversion
	 * @return
	 */
	public static ArrayList<Object> IntersectAL(ArrayList<Object> al,HashMap<Object,Object> hm,Boolean inversion){
		ArrayList<Object> value = new ArrayList<Object>();
		for(int i=0;i<al.size();i++){
			Object obj = al.get(i);
			if(hm.containsKey(obj)&&!inversion){
				value.add(hm.get(obj));
			}else if(hm.containsKey(obj)&&inversion){
				hm.remove(obj);
			}
		}
		if(inversion){
			Iterator<Object> it=hm.keySet().iterator();
			while (it.hasNext()) {
				Object inversionKey=it.next();
				value.add(hm.get(inversionKey));
			}
		}
		return value;
	}
	
	public static HashMap<Object,Object> IntersectHM(ArrayList<Object> al,HashMap<Object,Object> hm){
		HashMap<Object,Object> value = new HashMap<Object,Object>();
		for(int i=0;i<al.size();i++){
			Object obj = al.get(i);
			if(hm.containsKey(obj))
				value.put(obj, hm.get(obj));
		}
		return value;
	}
	
	public static void main(String[] args) {
		ArrayList<Object> al=new  ArrayList<Object>();
		al.add("1");
		al.add("2");
		al.add("3");
		al.add("4");
		
		HashMap<Object,Object> hm = new HashMap<Object,Object>();
		hm.put("4", "第四");
		hm.put("7", "第七");
		hm.put("6", "第六");
		
		ArrayList<Object> ao = IntersectAL(al, hm, true);
		for(Object value:ao){
			System.out.println(value);
		}
		
	}
}

	
