package com.training.grocery.product.hibernate;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.training.grocery.product.datamodel.DBProduct;

@Repository
public interface ProductRepository extends JpaRepository<DBProduct, Long>, JpaSpecificationExecutor<DBProduct> {
	List<DBProduct> findByName(String name);

	List<DBProduct> findByNameAndCategory(String name, String category);

	DBProduct findByProductId(String productId);
}
