package tony.jdbc.DAO;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DAOTest {

	DAO dao = new DAO();

	@Test
	public void testUpdate() {
		String sql = "INSERT INTO testDB.examstudent(flowId, type, idCard, examCard, studentName,location,grade)"
				+ " VALUES(?,?,?,?,?,?,?)";
		dao.update(sql, 15, 6, "3452", "8567sf", "ABCD", "HK", 75);

	}

	@Test
	public void testGet() {
		String sql = "SELECT flowId, type, idCard, examCard, studentName,location,grade FROM testDB.examstudent "
				+ "WHERE flowId = ?";
		Student student = dao.get(Student.class, sql, 3);

		System.out.println(student);

	}

	@Test
	public void testGetForList() {
		String sql = "SELECT flowId, type, idCard, examCard, studentName,location,grade FROM testDB.examstudent ";

		List<Student> students = dao.getForList(Student.class, sql);

		System.out.println(students);

	}

	@Test
	public void testGetForValue() {
		String sql = "SELECT examCard FROM testDB.examstudent "+
					"WHERE flowId =?";
		String examCard = dao.getForValue(sql, 15);
		System.out.println(examCard);
		
		sql = "SELECT max(grade) FROM testDB.examstudent";
		int grade = dao.getForValue(sql);
		System.out.println(grade);
	}

}
