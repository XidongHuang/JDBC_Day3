package tony.jdbc.DAO;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TransactionTest {
	
	
	/**
	 * 测试事务的隔离级别
	 * 在JDBC 程序中可以通过 Connection 的setTransactionIsolation
	 * 来设置隔离事务
	 * 
	 */
	
	@Test
	public void testTransactionIsolationUpdate(){
		Connection connection = null;
		
		try {
			connection = JDBCTools.getConnection();
			connection.setAutoCommit(false);
			
			String sql = "UPDATE users SET balance = " + "balance - 500 WHERE id = 1";

			update(connection,sql);
			
			connection.commit();
			
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			
		}
		
		
	}
	
	@Test
	public void testTransactionIsolationRead(){
		String sql = "SELECT balance FROM users WHERE id =1";
		Integer balance = getForValue(sql);
		System.out.println(balance);
	}
	
	
	public <E> E getForValue(String sql, Object... args) {

		//1. 得到结果集: 该结果集应该只有一行，且只有一列
		List<E> list = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			//1. 得到结果集合
			connection = JDBCTools.getConnection();
			System.out.println(connection.getTransactionIsolation());
			
			
//			connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			preparedStatement = connection.prepareStatement(sql);

			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);

			}
			resultSet = preparedStatement.executeQuery();

			if(resultSet.next()){
				return (E)resultSet.getObject(1);
			}
			
			
		
	} catch (Exception e){
		e.printStackTrace();
	} finally {
		JDBCTools.releaseDB(resultSet, preparedStatement, connection);
	}
		//2. 取得结果集的
		
		
		return null;
	

	}
	
	

	/**
	 * Tom 给Jerry 汇款500元
	 * 
	 * 关于事务: 1. 如果多个操作, 每个操作使用的是自己的单独的链接，则无法保证事务 2. 具体步骤: 1) 事务操作开始前，开始事务:
	 * 取消Connection 的默认提交行为 connection.setAutoCommit(false);
	 * 
	 * 2). 如果事务的操作都成功，则提交事务: connection.commit(); 3). 回滚食物: 若出现一场，则在catch
	 * 块中回滚事务。
	 */

	@Test
	public void testTransaction() {

		Connection connection = null;

		try {

			connection = JDBCTools.getConnection();
			// 开始事务:取消默认提交
			connection.setAutoCommit(false);

			String sql = "UPDATE users SET balance = " + "balance - 500 WHERE id = 1";
			update(connection, sql);
			// int i = 10/0;

			sql = "UPDATE users SET balance = " + "balance + 500 WHERE id = 2";
			update(connection, sql);

			// 提交事务
			connection.commit();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} finally {
			JDBCTools.releaseDB(null, null, connection);
		}

		
		/*
		try {

			
			// 开始事务:取消默认提交
			connection.setAutoCommit(false);
			
			//....
			
			// 提交事务
			connection.commit();

		} catch (Exception e) {
			// TODO: handle exception
			

			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} finally {
			JDBCTools.releaseDB(null, null, connection);
		}

		*/
		// DAO dao = new DAO();

		//
		//
		// dao.update(sql);
		//

		//
		// dao.update(sql);
		//
	}

	public void update(Connection connection, String sql, Object... args) {
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(sql);

			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);

			}

			preparedStatement.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
