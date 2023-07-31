package application;

import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.dao.impl.SellerDaoJDBC;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Department obj = new Department(1, "Computers");		
		Seller seller = new Seller(13, "Jamal", "jamal@gmail.com", new Date(), 3000.0, obj);	
		SellerDao sellerDao = DaoFactory.createSellerDao();
		seller.setName("Joana");
		sellerDao.deleteById(12);
		sellerDao.findAll().stream().forEach(System.out::println);;
		
	}

}
