package application;

import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		Department obj = new Department(1, "Books");		
		Seller seller = new Seller(21, "Bob", "bob@gmail.com", new Date(), 3000.0, obj);	
		SellerDao sellerDao = DaoFactory.createSellerDao();
		Seller seller2 = sellerDao.findById(4);
		
		
		Department dep = new Department(2, null);
		List<Seller> sel = sellerDao.findAll();
		sel.stream().forEach(System.out::println);
	}

}
