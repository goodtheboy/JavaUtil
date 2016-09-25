package test.socks.http;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class ExcelExportTest {
	static Connection dbconn = null;
	public static void main(String[] args){
        try {
            File exlFile = new File("c:/write_test.xls");
            WritableWorkbook writableWorkbook = Workbook
                    .createWorkbook(exlFile);
 
            WritableSheet writableSheet = writableWorkbook.createSheet(
                    "Sheet1", 0);
            //Create Cells with contents of different data types. 
            //Also specify the Cell coordinates in the constructor
            Label label = new Label(0, 0, "Test ");
            DateTime date = new DateTime(1, 0, new Date());
 
            //Add the created Cells to the sheet
            writableSheet.addCell(label);
            writableSheet.addCell(date);
 
            //Write and close the workbook
            writableWorkbook.write();
            writableWorkbook.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }
	
	@Test
	public void test(){
		String sql="select * from sdb_b2c_members_error";//.sdb_b2c_orders
		String[] field=getMateDataField(dblink(sql));
		ArrayList<String[]> al=getMeteDataValue(dblink(sql));
		File file=new File("c:/write_test.xls");
		WritableWorkbook wtwb=null;
		WritableSheet wts=null;
		try {
			wtwb=Workbook.createWorkbook(file);
			wts=wtwb.createSheet("test", 0);
			for (int i = 0; i < field.length; i++) {
				Label label=new Label(i, 0, field[i]);
				wts.addCell(label);
			}
			for (int j = 0; j < al.size(); j++) {
				for (int k = 0; k < al.get(j).length; k++) {
										//列，行
					Label label=new Label(k,j+1,al.get(j)[k]);
					wts.addCell(label);
				}
			}
			wtwb.write();
			wtwb.close();
		} catch (IOException e) {
			System.out.println("Create Workbook Exception:"+e.getMessage());
		} catch (RowsExceededException e) {
			System.out.println("Write Cell Data Exception:"+e.getMessage());
		} catch (WriteException e) {
			System.out.println("Write Table Sheet Exception:"+e.getMessage());
		}
	}

/**
 * 根据数据库查询语句将数据导出到Excel表格中
 * 1:建立数据库连接
 * 2：获取数据集中查询出来的字段名称
 * 3:遍历数据集，根据名称获取对应该行的字段值
 * 4：写入Excel表格中
 */
	private Connection conn = null;
	public ResultSet dblink(String sql){
		Statement sm=null;
		ResultSet rs=null;
		if(conn==null)
			conn=con("xhsd","root","1");
		try{
			sm=conn.createStatement();
			rs=sm.executeQuery(sql);
		}catch (Exception e) {
			System.out.println("Create Db Link Exception:"+e.getMessage());
		}finally{
			
		}
		return rs;
	}
	
	
	public String[] getMateDataField(ResultSet rs){
		ResultSetMetaData rmd=null;
		String[] field=null;
		try {
			rmd=rs.getMetaData();
			field=new String[rmd.getColumnCount()];
			for (int i = 0; i < rmd.getColumnCount(); i++) {
				field[i]=rmd.getColumnLabel(i+1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return field;
	}
	
	public ArrayList<String[]> getMeteDataValue(ResultSet rs){
		String[] field=getMateDataField(rs);
		ArrayList<String[]> al=new ArrayList<String[]>();
		try{
			while(rs.next()){
				String[] value=new String[field.length];
				for (int i = 0; i < field.length; i++) {
					value[i]=rs.getString(field[i]);
				}
				al.add(value);
			}
		}catch (SQLException e) {
			System.out.println("ResultSet Exception:"+e.getMessage());
		}
		return al;
	}
	
	
	/**
	 * Create DB link
	 * @param DBname
	 * @param username
	 * @param password
	 * @return
	 */

	public static Connection  con(String DBname,String username,String password){
			if(dbconn!=null)
				return dbconn;
		 	String driver = "com.mysql.jdbc.Driver";
	        String url = "jdbc:mysql://localhost:3306/" + DBname;
	        try {
	            Class.forName(driver);
	            dbconn = DriverManager.getConnection(url, username, password);
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return dbconn;
	}
}
