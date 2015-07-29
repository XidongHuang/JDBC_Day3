package tony.jdbc.DAO;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.junit.Test;

import java.sql.ResultSetMetaData;

import java.sql.PreparedStatement;

public class MetaDataTest {

	
	/**
	 * ResultSetMetaData: 描述结果集的元数据
	 * 可以得到结果集中的基本信息: 结果集中有哪些列，列名，列的别名等
	 */
	
	@Test
	public void testResultSetMetaData(){
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		
		try {
			connection = JDBCTools.getConnection();
			String sql = "SELECT flowId,type,studentName Name FROM testDB.examstudent";
			preparedStatement = connection.prepareStatement(sql);
			
			resultSet = preparedStatement.executeQuery();
			
			
			//1. 得到ResultSetMetaData 对象
			ResultSetMetaData rsmd = resultSet.getMetaData();
			
			//2. 得到列的个数
			int columnCount = rsmd.getColumnCount();
			for(int i = 0;i<columnCount;i++){
				//3. 得到列名
				String columnName = rsmd.getColumnName(1+i);
				
				//4. 得到列的别名
				String columnLabel = rsmd.getColumnLabel(i+1);
				System.out.println(columnName+","+columnLabel);
				
				
			}
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{
			JDBCTools.releaseDB(resultSet, preparedStatement, connection);
			
		}
		
		
		
	}
	
	
	
	
	
	
	/**
	 * DatabaseMetaData 是描述数据库的元数据对象
	 * 可以由Connection 得到
	 * 了解
	 */
	
	@Test
	public void test() {
		Connection connection = null;
		DatabaseMetaData data = null;
		ResultSet resultSet = null;
		
		
		try {
			connection = JDBCTools.getConnection();
			data = connection.getMetaData();
			
			
			//可以得到数据库本身的一些基本信息
			//得到数据库的版本号
			int version = data.getDatabaseMajorVersion();
			System.out.println(version);
			
			//得到链接到数据库的用户名
			String userName = data.getUserName();
			System.out.println(userName);
			
			//得到MySql 中有哪些数据库
			resultSet = data.getCatalogs();
			while(resultSet.next()){
				
				System.out.println(resultSet.getString(1));
				
			}
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			
			JDBCTools.releaseDB(resultSet, null, connection);
		}
		
		

	}

}
