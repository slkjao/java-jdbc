package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department department) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO department (Name) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, department.getName());
			int rows = ps.executeUpdate();
			if (rows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				boolean b = rs.next();
				if (rs.next()) {
					department.setId(rs.getInt(1));
					DB.closeResultSet(rs);
				} else {
					System.out.println("ERRO: No rows affected!");
				}
			}
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeConnection();
		}
	}

	@Override
	public void update(Department department) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");
			ps.setString(1, department.getName());
			ps.setInt(2, department.getId());
			ps.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		}

	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM department WHERE Id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			Department department = new Department();
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				return dep;
			}
			return null;

		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}

	}

	@Override
	public List<Department> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM department");
			rs = ps.executeQuery();
			List<Department> lista = new ArrayList<>();
			while (rs.next()) {
				Department department = instantiateDepartment(rs);
				lista.add(department);
			}
			return lista;

		} catch (Exception e) {
			throw new DbException(e.getMessage());
		}

	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department = new Department();
		department.setId(rs.getInt("Id"));
		department.setName(rs.getString("Name"));
		return department;
	}

}
