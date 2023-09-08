package com.training.grocery.basket.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import com.training.grocery.basket.dbmodel.Basket;
import com.training.grocery.basket.dbmodel.ProductInfo;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.product.datamodel.DBProduct;
import com.training.grocery.product.hibernate.ProductRepository;


@Service
public class BasketService {
	@Autowired
	BasketRepo repo;
	@Autowired
	ProductRepository prodrepo;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public void create(String userid) {
		Basket userBasket =
				Basket.builder().userid(userid).totalamount(0).totaldiscount(0).products(new ArrayList<>()).build();
		repo.save(userBasket);
	}

	public Basket show(String userid) {
		Basket userBasket = repo.findByUserid(userid);
		return userBasket;
	}

	public void alter(String userid, String productid, Long quantity) {

		Basket userBasket = repo.findByUserid(userid);
		List<ProductInfo> list;
		DBProduct prod = prodrepo.findByProductId(productid);
		System.out.println("IntenalHERE1");
		if (prod == null) {
			logger.error(userid + " " + productid + " product not available in Product table");
			throw new HttpMessageNotReadableException(EnumsForExceptions.Product_Unavailable.toString());
		}
		System.out.println("IntenalHERE2");
		System.out.println(userid);
		list = userBasket.getProducts();
		System.out.println(list.size());
		// Check for Productid in list
		for (ProductInfo product : list) {

			if (product.getProductid().equals(productid)) {

				logger.info(userid + " userBasket and Productid exists in Basket");


				delete(userid, productid);

				// Set product's new details
				product.setAmount(quantity * prod.getPrice());
				product.setDiscount(quantity * prod.getDiscount());
				product.setQuantity(quantity);
				list.add(product);

				// Save new details in userBasket
				saveToBasket(userBasket, list, quantity, prod.getPrice(), prod.getDiscount());
				logger.info(userid + " new details saved in Basket");
				return;
			}
		}

		// Productid is not found in list
		logger.info(userid + " userBasket exists but Productid doesn't");

		// Create ProductInBasket and add to list
		ProductInfo temp = ProductInfo.builder().amount(quantity * prod.getPrice())
				.discount(quantity * prod.getDiscount()).quantity(quantity).productid(productid).build();
		list.add(temp);

		// Save new details in UserBasket
		saveToBasket(userBasket, list, quantity, prod.getPrice(), prod.getDiscount());
		logger.info(userid + " new details saved in basket");
		return;
	}

	public void delete(String userid, String productid) {

		Basket userBasket = repo.findByUserid(userid);
		List<ProductInfo> list = userBasket.getProducts();

		if (list == null || userBasket == null) {
			logger.error(userid + " userBasket doesn't exist or Product list empty");
			throw new HttpMessageNotReadableException(EnumsForExceptions.Basket_Empty.toString());
		}

		for (ProductInfo product : list) {
			if (product.getProductid().equals(productid)) {

				list.remove(product);

				userBasket.setTotalamount(userBasket.getTotalamount() - product.getAmount());
				userBasket.setTotaldiscount(userBasket.getTotaldiscount() - product.getDiscount());
				userBasket.setProducts(list);
				repo.save(userBasket);
				logger.info(userid + " Productid found and deleted");
				return;
			}
		}
		logger.error(userid + " Productid not found in basket");
		throw new HttpMessageNotReadableException(EnumsForExceptions.Product_Not_In_Basket.toString());
	}

	protected void saveToBasket(Basket userBasket, List<ProductInfo> list, long quantity, float DBprice,
			float discount) {
		userBasket.setProducts(list);
		userBasket.setTotalamount(userBasket.getTotalamount() + (quantity * DBprice));
		userBasket.setTotaldiscount(userBasket.getTotaldiscount() + (discount * quantity));
		repo.save(userBasket);
	}

	public void clearBasket(String userid) {
		Basket basket = repo.findByUserid(userid);
		if (basket == null)
			return;
		List<ProductInfo> list = basket.getProducts();
		if (list == null)
			return;
		list.clear();
		basket.setProducts(list);
		basket.setTotalamount(0);
		basket.setTotaldiscount(0);
		logger.info("Basket cleared");
		repo.save(basket);
	}

}
