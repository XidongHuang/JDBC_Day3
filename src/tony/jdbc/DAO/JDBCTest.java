package tony.jdbc.DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

import java.sql.Blob;

import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.print.attribute.standard.OutputDeviceAssigned;

public class JDBCTest {
	
	
	/**
	 * 读取blob 数据:
	 * 1. 使用 getBlob 方法读取到 Blob 对象
	 * 2. 调用 Blob 的getBinaryStream() 方法得到输入流。再使用IO 操作即可。
	 */
	
	
	@Test
	public void readBlob(){
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		
		try {
			
			connection = JDBCTools.getConnection();
			String sql = "SELECT type,idCard,examCard, studentname,location,grade,picutre FROM testDB.examstudent"
					+ " WHERE flowID = 20";
			preparedStatement = connection.prepareStatement(sql);

			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()){
				int type = resultSet.getInt(1);
				String id = resultSet.getString(2);
				String exId = resultSet.getString(3);
				String name = resultSet.getString(4);
				String location = resultSet.getString(5);
				String grade = resultSet.getString(6);
				
				
				
				System.out.println(type + ", " + id+", "+exId+", "+name+", "+location+", "+grade );
			
				Blob picture = resultSet.getBlob(7);
				
				InputStream in  = picture.getBinaryStream();
				
				OutputStream out = new FileOutputStream("John.jpg");
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = in.read(buffer))!= -1){
					out.write(buffer, 0, len);
					
				}
				
				out.close();
				in.close();
			
			}
			
		
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			JDBCTools.releaseDB(resultSet, preparedStatement, connection);
			
		}
		
		
	}
	
	
	
	/**
	 * 插入BLOB 类型的数据必须使用 PreparedStatement: 因为BLOB 类型
	 * 的数据时无法用字符串拼写
	 * 
	 * 调用 setBlob(int index, InputStream inputStream)
	 * 
	 */
	@Test
	public void testInsertBlob(){
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		
		
		try {
			
			connection = JDBCTools.getConnection();
			String sql = "INSERT INTO testDB.examstudent(type,idCard,examCard, studentname,location,grade,picutre)"+
						" VALUES(?,?,?,?,?,?,?)";
			preparedStatement = connection.prepareStatement(sql);
			
			
			//使用重载的 prepareStatement(sql,flag) 来生成PreparedStatemnt 对象
			preparedStatement.setInt(1, 4);
			preparedStatement.setString(2, "75442");
			preparedStatement.setString(3, "435adf");
			preparedStatement.setString(4, "Yuko");
			preparedStatement.setString(5, "ZJ");
			preparedStatement.setInt(6, 79);
//			preparedStatement.setBlob(7, inputStream);
			
			InputStream inputStream = new FileInputStream("testBlob.jpg");
			preparedStatement.setBlob(7, inputStream);
			preparedStatement.executeUpdate();
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			JDBCTools.releaseDB(null, preparedStatement, connection);
			
		}
	}
	
	

	/**
	 * 取得数据库自动生成的主键
	 * 
	 */
	
	@Test
	public void testGetKeyValue(){
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		
		
		try {
			
			connection = JDBCTools.getConnection();
			String sql = "INSERT INTO testDB.examstudent(examCard, studentname,location)"+
						" VALUES(?,?,?)";
//			preparedStatement = connection.prepareStatement(sql);
			preparedStatement = connection.prepareStatement(sql, 
					Statement.RETURN_GENERATED_KEYS);
			
			
			
			//使用重载的 prepareStatement(sql,flag) 来生成PreparedStatemnt 对象
			preparedStatement.setString(1, "8yh");
			preparedStatement.setString(2, "DaMing");
			preparedStatement.setString(3, "CQ");
			preparedStatement.executeUpdate();
			
			//通过 .getGeneratedKeys() 获取包含了新生成的主键的ResultSet 对象
			//在ResultSet 中只有一列 GENERATED_KEY, 用于存放新生成的主键值. 
			ResultSet rs = preparedStatement.getGeneratedKeys();
			
			if(rs.next()){
				System.out.println(rs.getObject(1));
			}
			
			
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i = 0;i < rsmd.getColumnCount();i++){
				
				System.out.println(rsmd.getColumnName(i+1));
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			JDBCTools.releaseDB(null, preparedStatement, connection);
			
		}
		
	}
	
	
}
