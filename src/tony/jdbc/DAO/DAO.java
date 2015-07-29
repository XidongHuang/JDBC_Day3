package tony.jdbc.DAO;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.ObjectInputStream.GetField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListModel;

import org.apache.commons.beanutils.BeanUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DAO {

	// INSERT, UPDATE, DELETE 操作都可以包含在其中
	public void update(String sql, Object... args) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = JDBCTools.getConnection();
			preparedStatement = connection.prepareStatement(sql);

			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);

			}

			preparedStatement.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			JDBCTools.releaseDB(null, preparedStatement, connection);
		}

	}

	// 查询多条记录，返回对应的对象的集合
	public <T> T get(Class<T> clazz, String sql, Object... args) {
		List<T> result = getForList(clazz, sql, args);
		if(result.size()>0){
			return result.get(0);
			
		}
		return null;
	}

	// 查询多条记录，返回对应的对象的集合
	public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {

		List<T> list = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			//1. 得到结果集合
			connection = JDBCTools.getConnection();
			preparedStatement = connection.prepareStatement(sql);

			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);

			}
			resultSet = preparedStatement.executeQuery();

			
			
			//2. 处理结果集，得到Map 的List，其中一个Map 对象
			//就是一条记录. Map 的 key 为resultSet 中列的别名， Map 的value为列的值
			List<Map<String, Object>> values = hadnleResultSetToMapList(resultSet);
			
			
			//3. 把Map 的List 转为 clazz 对应的List
			// 其中Map 的key 即为 clazz 对应的对象的propertyName,
			//而Map 的value 即为 clazz 对应的对象的 propertyValue
			list = transfterMapListToBeanList(clazz, values);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			JDBCTools.releaseDB(resultSet, preparedStatement, connection);

		}

		// 12. 遍历 Map 对象，用反射填充对象的属性值
		// 属性名为 Map 中的key, 属性值Map 中的value

		// 13. 把 Object 对象放入到 list 中。

		return list;
	}

	public <T> List<T> transfterMapListToBeanList(Class<T> clazz, List<Map<String, Object>> values)
			throws InstantiationException, IllegalAccessException {

		List<T> result = new ArrayList<>();

		// 12. 判断 List 中是否为空集。若不为空，则遍历List,得到一个一个的Map 对象，再把一个Map 对象转为一个Class
		// 参数对应的Object 对象
		T bean = null;

		if (values.size() > 0) {
			for (Map<String, Object> m : values) {
				bean = clazz.newInstance();
				for (Map.Entry<String, Object> entry : m.entrySet()) {

					String propertyName = entry.getKey();
					Object propertyValue = entry.getValue();

//					System.out.println("propertyName: " + propertyName);
//					System.out.println("propertyValue: " + propertyValue);

					ReflectionUtil.setFieldValue(bean, propertyName, propertyValue);
				}
				// 13. 把Object 对象放入到 list 中。
				result.add(bean);

			}

		}
		return result;
	}

	/**
	 * 处理结果集，得到 Map 的一List,其中一个Map 对象
	 * 
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */

	public List<Map<String, Object>> hadnleResultSetToMapList(ResultSet resultSet) throws SQLException {
		// 5. 准备一个List<Map<String, Object>>: 键: 存放列的别名， 值: 存放列的值。
		// 其中一个Map 对象对应着一条记录
		List<Map<String, Object>> values = new ArrayList<>();

		List<String> columnLabels = getColumnLabels(resultSet);
		Map<String, Object> map = null;

		// 7. 处理ResultSet ，用While 循环
		while (resultSet.next()) {

			map = new HashMap<>();

			for (String columnName : columnLabels) {

				Object columnValue = resultSet.getObject(columnName);

				map.put(columnName, columnValue);

			}

			// 11. 把填充好的Map 对象放入 5 准备的 List 中
			values.add(map);

		}
		return values;
	}

	/**
	 * 获取结果集 ColumnLabel 对应的 List
	 * 
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */

	private List<String> getColumnLabels(ResultSet rs) throws SQLException {
		List<String> labels = new ArrayList<>();

		ResultSetMetaData rsmd = rs.getMetaData();

		for (int i = 0; i < rsmd.getColumnCount(); i++) {

			labels.add(rsmd.getColumnLabel(i + 1));

		}

		return labels;
	}

	// 返回某条记录的某一个字段的值或一个统计的值(一共有多条记录等)
	public <E> E getForValue(String sql, Object... args) {

		//1. 得到结果集: 该结果集应该只有一行，且只有一列
		List<E> list = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			//1. 得到结果集合
			connection = JDBCTools.getConnection();
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
}
