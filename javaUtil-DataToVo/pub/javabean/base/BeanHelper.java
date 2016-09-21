package pub.javabean.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeanHelper {
	static class ReflectionInfo {
		/**
		 * Get方法集合 key：field name value:Method
		 * key 都是小写
		 */
		Map<String, Method> readMap = new HashMap<String, Method>();
		/**
		 * Set方法集合 key：field name value:Method
		 * key 都是小写
		 */
		Map<String, Method> writeMap = new HashMap<String, Method>();
		Method getReadMethod(String prop) {
			return prop == null ? null : readMap.get(prop.toLowerCase());
		}
		Method getWriteMethod(String prop) {
			return prop == null ? null : writeMap.get(prop.toLowerCase());
		}
	}
	
	
	protected static final Object[] NULL_ARGUMENTS = {};
	/**
	 *缓存： 根据VO名称(pub.javabean.BillYs)找到该对象的ReflectionInfo
	 */
	private static Map<String, ReflectionInfo> cache = new ConcurrentHashMap<String, ReflectionInfo>();
	private static BeanHelper bhelp = new BeanHelper();
	public static BeanHelper getInstance() {
		return bhelp;
	}
	private BeanHelper() {}
	/**
	 * 返回对象的所有属性名
	 * 
	 * @param bean
	 * @return
	 */
	public static List<String> getPropertys(Object bean) {
		return Arrays.asList(getInstance().getPropertiesAry(bean));
	}
	/**
	 * 返回对象的所有属性名
	 * 
	 * @param bean
	 * @return
	 */
	public String[] getPropertiesAry(Object bean) {
		ReflectionInfo reflectionInfo = null;
		reflectionInfo = cachedReflectionInfo(bean.getClass());
		Set<String> propertys = new HashSet<String>();
		for (String key : reflectionInfo.writeMap.keySet()) {
			if (reflectionInfo.writeMap.get(key) != null) {
				propertys.add(key);
			}
		}
		return propertys.toArray(new String[0]);
	}
	/**
	 * 获取bean对象的属性值
	 * 
	 * @param bean
	 * @param propertyName
	 * @return
	 */
	public static Object getProperty(Object bean, String propertyName) {
		try {
			Method method = getInstance().getMethod(bean, propertyName, false);
			if (propertyName != null && method == null) {
				return null;
			} else if (method == null) {
				return null;
			}
			return method.invoke(bean, NULL_ARGUMENTS);
		} catch (Exception e) {
			String errStr = "Failed to get property: " + propertyName;
			// Logger.warn(errStr, e);
			throw new RuntimeException(errStr, e);
		}
	}
	/**
	 * 批量返回bean对象的属性值
	 * 
	 * @param bean
	 * @param propertys
	 * @return
	 */
	public static Object[] getPropertyValues(Object bean, String[] propertys) {
		Object[] result = new Object[propertys.length];
		try {
			Method[] methods = getInstance().getMethods(bean, propertys, false);
			for (int i = 0; i < propertys.length; i++) {
				if (propertys[i] == null || methods[i] == null) {
					result[i] = null;
				} else {
					result[i] = methods[i].invoke(bean, NULL_ARGUMENTS);
				}
			}
		} catch (Exception e) {
			String errStr = "Failed to get getPropertys from " + bean.getClass();
			throw new RuntimeException(errStr, e);
		}
		return result;
	}
	
	public static Method getMethod(Object bean, String propertyName) {
		return getInstance().getMethod(bean, propertyName, true);
	}
	public static Method getGetMethod(Object bean, String propertyName) {
		return getInstance().getMethod(bean, propertyName, false);
	}
	public static Method getSetMethod(Object bean, String propertyName) {
		return getInstance().getMethod(bean, propertyName, true);
	}
	/**
	 * 批量返回属性的set方法
	 * 
	 * @param bean
	 * @param propertys
	 * @return
	 */
	public static Method[] getMethods(Object bean, String[] propertys) {
		return getInstance().getMethods(bean, propertys, true);
	}
	
	/**
	 * 解析传过来的VO有哪些方法，其中将Set方法存进全局变量readMap里(key=field,value=Method).
	 * @param bean
	 * @param propertys
	 * @param isSetMethod
	 * @return
	 */
	private Method[] getMethods(Object bean, String[] propertys, boolean isSetMethod) {
		Method[] methods = new Method[propertys.length];
		ReflectionInfo reflectionInfo = null;
		reflectionInfo = cachedReflectionInfo(bean.getClass());//解析传过来的VO有哪些方法
		for (int i = 0; i < propertys.length; i++) {
			Method method = null;
			if (isSetMethod) {
				method = reflectionInfo.getWriteMethod(propertys[i]);
			} else {
				method = reflectionInfo.getReadMethod(propertys[i]);
			}
			methods[i] = method;
		}
		return methods;
	}
	
	/**
	 * return field Method
	 * @param bean
	 * @param propertyName
	 * @param isSetMethod
	 * @return
	 */
	private Method getMethod(Object bean, String propertyName, boolean isSetMethod) {
		Method method = null;
		ReflectionInfo reflectionInfo = null;
		reflectionInfo = cachedReflectionInfo(bean.getClass());
		if (isSetMethod) {
			method = reflectionInfo.getWriteMethod(propertyName);
		} else {
			method = reflectionInfo.getReadMethod(propertyName);
		}
		return method;
	}
	private ReflectionInfo cachedReflectionInfo(Class<?> beanCls) {
		return cacheReflectionInfo(beanCls, null);
	}
	
	
	private ReflectionInfo cacheReflectionInfo(Class<?> beanCls, List<PropDescriptor> pdescriptor) {
		String key = beanCls.getName();
		ReflectionInfo reflectionInfo = cache.get(key);
		if (reflectionInfo == null) {
			reflectionInfo = cache.get(key);
			if (reflectionInfo == null) {
				reflectionInfo = new ReflectionInfo();
				List<PropDescriptor> propDesc = new ArrayList<PropDescriptor>();
				if (pdescriptor != null) {
					propDesc.addAll(pdescriptor);
				} else {
					propDesc = getPropertyDescriptors(beanCls);//解析传过来的VO有哪些方法
					
					
				}
				for (PropDescriptor pd : propDesc) {
					Method readMethod = pd.getReadMethod(beanCls);
					Method writeMethod = pd.getWriteMethod(beanCls);
					if (readMethod != null)
						reflectionInfo.readMap.put(pd.getName().toLowerCase(), readMethod); // store
																							// as
					// lower
					// case
					if (writeMethod != null)
						reflectionInfo.writeMap.put(pd.getName().toLowerCase(), writeMethod);
				}
				cache.put(key, reflectionInfo);
			}
		}
		return reflectionInfo;
	}
	
	public static void invokeMethod(Object bean, Method method, Object value) {
		try {
			if (method == null)
				return;
			Object[] arguments = { value };
			method.invoke(bean, arguments);
		} catch (Exception e) {
			String errStr = "Failed to set property: " + method.getName();
			// Logger.error(errStr, e);
			throw new RuntimeException(errStr, e);
		}
	}
	public static void setProperty(Object bean, String propertyName, Object value) {
		try {
			Method method = getInstance().getMethod(bean, propertyName, true);
			if (propertyName != null && method == null) {
				return;
			} else if (method == null) {
				return;
			}
			method.invoke(bean, value);
		} catch (java.lang.IllegalArgumentException e) {
			String errStr = "Failed to set property: " + propertyName + " at bean: " + bean.getClass().getName() + " with value:" + value + " type:" + (value == null ? "null" : value.getClass().getName());
			// Logger.error(errStr, e);
			throw new IllegalArgumentException(errStr, e);
		} catch (Exception e) {
			String errStr = "Failed to set property: " + propertyName + " at bean: " + bean.getClass().getName() + " with value:" + value;
			// Logger.error(errStr, e);
			throw new RuntimeException(errStr, e);
		}
	}
	/*
	 * 返回所有get的方法
	 */
	public Method[] getAllGetMethod(Class<?> beanCls, String[] fieldNames) {
		Method[] methods = null;
		ReflectionInfo reflectionInfo = null;
		List<Method> al = new ArrayList<Method>();
		reflectionInfo = cachedReflectionInfo(beanCls);
		for (String str : fieldNames) {
			al.add(reflectionInfo.getReadMethod(str));
		}
		methods = al.toArray(new Method[al.size()]);
		return methods;
	}
	
	/**
	 * 根据class参数，
	 * @param clazz
	 * @return
	 */
	private List<PropDescriptor> getPropertyDescriptors(Class<?> clazz) {
		List<PropDescriptor> descList = new ArrayList<PropDescriptor>();
		List<PropDescriptor> superDescList = new ArrayList<PropDescriptor>();
		List<String> propsList = new ArrayList<String>();
		Class<?> propType = null;
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().length() < 4) {
				continue;
			}
			if (method.getName().charAt(3) < 'A' || method.getName().charAt(3) > 'Z') {
				continue;
			}
			if (method.getName().startsWith("set")) {
				if (method.getParameterTypes().length != 1) {
					continue;
				}
				if (method.getReturnType() != void.class) {
					continue;
				}
				propType = method.getParameterTypes()[0];
			} else if (method.getName().startsWith("get")) {
				if (method.getParameterTypes().length != 0) {
					continue;
				}
				propType = method.getReturnType();
			} else {
				continue;
			}
			String propname = method.getName().substring(3, 4).toLowerCase();
			if (method.getName().length() > 4) {
				propname = propname + method.getName().substring(4);
			}
			if (propname.equals("class")) {
				continue;
			}
			if (propsList.contains(propname)) {
				continue;
			} else {
				propsList.add(propname);
			}
			descList.add(new PropDescriptor(clazz, propType, propname));
		}
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null) {
			superDescList = getPropertyDescriptors(superClazz);
			descList.addAll(superDescList);
			if (!isBeanCached(superClazz)) {
				cacheReflectionInfo(superClazz, superDescList);
			}
		}
		return descList;
	}
	private boolean isBeanCached(Class<?> bean) {
		String key = bean.getName();
		ReflectionInfo cMethod = cache.get(key);
		if (cMethod == null) {
			cMethod = cache.get(key);
			if (cMethod == null) {
				return false;
			}
		}
		return true;
	}
	
	
}
class PropDescriptor {
	/**
	 * VO对象类型
	 */
	private Class<?> beanType;
	/**
	 * 返回类型 
	 */
	private Class<?> propType;
	/**
	 * field name example:id
	 */
	private String name;
	/**
	 * first field name is Upper  example:Id
	 */
	private String baseName;
	public PropDescriptor(Class<?> beanType, Class<?> propType, String propName) {
		if (beanType == null) {
			throw new IllegalArgumentException("Bean Class can not be null!");
		}
		if (propName == null) {
			throw new IllegalArgumentException("Bean Property name can not be null!");
		}
		this.propType = propType;
		this.beanType = beanType; // in which this property is declared.
		this.name = propName;
		if (name.startsWith("m_") && name.length() > 2) {
			this.baseName = changeFirstCharacterCase(true,name.substring(2));
		} else {
			this.baseName =changeFirstCharacterCase(true, propName);
		}
	}
	/**
	 * 将首字母转化成大写
	 * @param capitalize
	 * @param str
	 * @return
	 */
	private static String changeFirstCharacterCase(boolean capitalize,
			String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		StringBuffer buf = new StringBuffer(strLen);
		if (capitalize) {
			buf.append(Character.toUpperCase(str.charAt(0)));
		} else {
			buf.append(Character.toLowerCase(str.charAt(0)));
		}
		buf.append(str.substring(1));
		return buf.toString();
	}
	/**
	 * PropDescriptor
	 * 根据参数currBean获取PropDescriptor中baseName的Get方法
	 * @param currBean
	 * @return
	 */
	public synchronized Method getReadMethod(Class<?> currBean) {
		Method readMethod;
		String readMethodName = null;
		if (propType == boolean.class || propType == null) {
			readMethodName = "is" + baseName;
		} else {
			readMethodName = "get" + baseName;
		}
		Class<?> classStart = currBean;
		if (classStart == null) {
			classStart = beanType;
		}
		readMethod = findMemberMethod(classStart, readMethodName, 0, null);
		if (readMethod == null && readMethodName.startsWith("is")) {
			readMethodName = "get" + baseName;
			readMethod = findMemberMethod(classStart, readMethodName, 0, null);
		}
		if (readMethod != null) {
			int mf = readMethod.getModifiers();//拿到该方法的修饰符，public，private,protected static 等等..
			if (!Modifier.isPublic(mf)) {
				return null;
			}
			Class<?> retType = readMethod.getReturnType();
			if (!propType.isAssignableFrom(retType)) {
			
			}
		}
		return readMethod;
	}
	
	/**
	 * PropDescriptor
	 * 根据参数currBean获取PropDescriptor中baseName的Set方法
	 * @param currBean
	 * @return
	 */
	public synchronized Method getWriteMethod(Class<?> currBean) {
		Method writeMethod;
		String writeMethodName = null;
		if (propType == null) {
			propType = findPropertyType(getReadMethod(currBean), null);
		}
		if (writeMethodName == null) {
			writeMethodName = "set" + baseName;
		}
		Class<?> classStart = currBean;
		if (classStart == null) {
			classStart = beanType;
		}
		writeMethod = findMemberMethod(classStart, writeMethodName, 1, (propType == null) ? null : new Class[] { propType });
		if (writeMethod != null) {
			int mf = writeMethod.getModifiers();
			if (!Modifier.isPublic(mf) || Modifier.isStatic(mf)) {
				writeMethod = null;
			}
		}
		return writeMethod;
	}
	
	/**
	 * 返回readMethod或者writeMethod方法的返回类型 两个参数中如果都不为空,优先readMethod
	 * @param readMethod
	 * @param writeMethod
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Class<?> findPropertyType(Method readMethod, Method writeMethod) {
		Class<?> propertyType = null;
		if (readMethod != null) {
			propertyType = readMethod.getReturnType();
		}
		if (propertyType == null && writeMethod != null) {
			Class params[] = writeMethod.getParameterTypes();
			propertyType = params[0];
		}
		return propertyType;
	}
	
	/**
	 * 在beanClass中，找到mName的方法，如果在beanClass找不到就在父类中查找,直到Object都找不到为止
	 * @param beanClass
	 * @param mName
	 * @param num
	 * @param args
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Method findMemberMethod(Class<?> beanClass, String mName, int num, Class[] args) {
		Method foundM = null;
		Method[] methods = beanClass.getDeclaredMethods();
		if (methods.length < 0) {
			return null;
		}
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase(mName)) {
				Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes.length == num) {
					boolean match = true;
					for (int i = 0; i < num; i++) {
						// parameter should be compatible with prop type
						if (!args[i].isAssignableFrom(paramTypes[i])) {
							match = false;
							break;
						}
					}
					if (match) {
						foundM = method;
						break;
					}
				}
			}
		}
		// recursively find super
		if (foundM == null) {
			if (beanClass.getSuperclass() != null) {
				foundM = findMemberMethod(beanClass.getSuperclass(), mName, num, args);
			}
		}
		return foundM;
	}
	public String getName() {
		return name;
	}
}
