package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller seller) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(
					"INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId)" + "VALUES (?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, seller.getName());
			ps.setString(2, seller.getEmail());
			ps.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			ps.setDouble(4, seller.getBaseSalary());
			ps.setInt(5, seller.getDepartment().getId());

			int rows = ps.executeUpdate();

			if (rows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					seller.setId(rs.getInt(1));
				}
			} else {
				System.out.println("ERROR: No rows affected!");
			}

		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public void update(Seller seller) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE seller SET "
					+ "Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " + "WHERE Id = ?");
			ps.setString(1, seller.getName());
			ps.setString(2, seller.getEmail());
			ps.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			ps.setDouble(4, seller.getBaseSalary());
			ps.setInt(5, seller.getDepartment().getId());
			ps.setInt(6, seller.getId());
			ps.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeConnection();
			DB.closeStatement(ps);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM seller WHERE id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		}

	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT seller.*, department.Name as depName "
					+ "FROM seller INNER JOIN department ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			Seller seller = new Seller();
			if (rs.next()) {
				Department department = instantiateDepartment(rs);
				seller = instantiateSeller(rs, department);
				return seller;
			}

			return null;

		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department ON seller.DepartmentId = department.Id " + "ORDER BY Id");
			rs = ps.executeQuery();

			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			while (rs.next()) {
				Department dep = map.get(rs.getInt("DepartmentId"));
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(dep.getId(), dep);
				}	
				Seller sel = instantiateSeller(rs, dep);
				list.add(sel);
			}
			return list;

		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name");
			ps.setInt(1, department.getId());
			rs = ps.executeQuery();

			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {
				Department dep = map.get(rs.getInt("DepartmentId"));
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(dep.getId(), dep);
				}

				Seller sel = instantiateSeller(rs, dep);
				list.add(sel);
			}

			return list;

		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}

	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department = new Department();
		department.setId(rs.getInt("DepartmentId"));
		department.setName(rs.getString("DepName"));
		return department;
	}

	private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setDepartment(department);
		return seller;
	}

}
