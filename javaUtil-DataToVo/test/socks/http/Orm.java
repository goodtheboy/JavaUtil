package test.socks.http;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import pub.javabean.BillYs;
import pub.javabean.base.SuperVO;


public class Orm {
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		 Object value=BeanListPrection("select * from bill_ys", BillYs.class);
		 System.out.println(value);
	//	addSql();
	}
	
	/**
	 * add sql statement
	 */
	public static void addSql(){
		String sql="select * from dba where id='11' ";
		String statFrom=sql.substring(0, sql.indexOf("from"));
		String FromEnd=sql.substring(sql.indexOf("from"), sql.length());
		StringBuffer sb=new StringBuffer();
		sb.append(statFrom);
		sb.append(",").append("dba.text").append(" ");
		sb.append(FromEnd);
		System.out.println(sb.toString());
	}
	
	/**
	 * execute sql 
	 * @param sql
	 * @return Boolean
	 */
	public static Boolean exe(String sql){
		 Connection con=con("bbmg","root","1");
		 Statement sm=null;
		 try {
			sm=con.createStatement();
			return sm.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	/**
	 * Create DB link
	 * @param DBname
	 * @param username
	 * @param password
	 * @return
	 */
	static Connection conn = null;
	public static Connection  con(String DBname,String username,String password){
			if(conn!=null)
				return conn;
		 	String driver = "com.mysql.jdbc.Driver";
	        String url = "jdbc:mysql://localhost:3306/" + DBname;
	        try {
	            Class.forName(driver);
	            conn = DriverManager.getConnection(url, username, password);
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return conn;
	}
	
	/**
	 * 查询数据
	 * @param sql
	 * @return
	 */
	public static ResultSet exeQuery(String sql){
		 Connection con=con("bbmg","root","1");
		 PreparedStatement  sm=null;
		 try{
			 sm=con.prepareStatement(sql);
			 return sm.executeQuery(sql);
		 }catch (Exception e) {
			System.out.println(e.getMessage());
		}finally{
			
		}
		 return null;
	}
	
	/**
	 * DataResultSet
	 * @param sql
	 * @param type 
	 * @return HashMap<String,Object>
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("rawtypes")
	public static Object result(String sql, Class type) throws InstantiationException, IllegalAccessException{
		ResultSet rs=exeQuery(sql);
//		HashMap<String ,Object> hm=new HashMap<String,Object>();
		ArrayList<Object> al=new ArrayList<Object>();
		try {
			ResultSetMetaData rmd=rs.getMetaData();
			while(rs.next()){
				Object target=type.newInstance();
				for (int i = 0; i < rmd.getColumnCount(); i++) {
						String label=rmd.getColumnName(i+1);
						if(target instanceof SuperVO){
							((SuperVO)target).setAttributeValue(label.toLowerCase(), rs.getObject(i+1));
						}
	//					hm.put(label, rs.getObject(i+1));
						
					}
				al.add(target);
			}
//			Method[] temps = BeanHelper.getMethods(target, propName);
//			System.out.println(temps);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return al;
	}
	
	/**根据sql返回的数据，封装到Class里头
	 * 
	 * @param sql
	 * @param type
	 * @return Object
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("rawtypes")
	public static Object BeanListPrection(String sql,Class type) throws InstantiationException, IllegalAccessException{
		
		ArrayList hm=(ArrayList) result(sql,type);
//		Iterator<String> it=hm.keySet().iterator();
//		while(it.hasNext()){
//			String key=it.next();
//			Object value=hm.get(key);
//			System.out.println("key="+key+",value="+value+"");
//		}
		return hm;
	}
}
