package pub.javabean.base;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;



public abstract class SuperVO implements Serializable  {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private transient static Map<Class, String[]> map = new HashMap<Class, String[]>();
	private transient static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	
	public String[] getTableAttribute(){
		return new String[]{""};
	}
	public void setAttributeValue(String name, Object value) {
		String key = name;
		String pkName = getPKFieldName();
		if (pkName != null && pkName.equals(name)) {
			key = "primarykey";
		}
		BeanHelper.setProperty(this, key, value);

	}

	public Object getAttributeValue(String name) {
		String key = name;
		String pkName = this.getPKFieldName();
		if (pkName != null && pkName.equals(name)) {
			key = "primarykey";
		}
		return BeanHelper.getProperty(this, key);
	}

	public Object getPrimaryKey() {
		String pkName = getPKFieldName();
		if (pkName == null) {
			return null;
		} else {
			return BeanHelper.getProperty(this, pkName);
		}

	}
	
	public String[] getAttributeNames() {
		rwl.readLock().lock();
		try {
			return getAttributeAryNoMetaData();
		} finally {
			rwl.readLock().unlock();
		}
	}
	
	private String[] getAttributeAryNoMetaData() {
		String[] arys = map.get(this.getClass());
		if (arys == null) {
			rwl.readLock().unlock();
			rwl.writeLock().lock();
			try {
				arys = map.get(this.getClass());
				if (arys == null) {
					// List<String> al = new ArrayList<String>();
					Set<String> set = new HashSet<String>();
					String[] strAry = BeanHelper.getInstance()
							.getPropertiesAry(this);
					for (String str : strAry) {
						if (getPKFieldName() != null&& str.equals("primarykey")) {
							set.add(getPKFieldName());
						} else if (!(str.equals("status")|| str.equals("dirty") || str.equals("valueIndex"))){
							set.add(str);
						}
					}
					arys = set.toArray(new String[set.size()]);
					map.put(this.getClass(), arys);
				}
			} finally {
				rwl.readLock().lock();
				rwl.writeLock().unlock();
			}
		}
		return arys;
	}
	
	public void setPrimaryKey(String key) {
		String pkName = this.getPKFieldName();
		if (pkName == null) {
			return;
		} else {
			BeanHelper.setProperty(this, pkName, key);
			return;
		}

	}

	public String getPKFieldName() {
		// 返回数据库表的主健名
		return null;
	}

	public String getTableName() {
		// 返回vo的主表
		return null;
	}
}
