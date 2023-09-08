package com.training.grocery.user.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.training.grocery.user.datamodel.DBUser;


@Repository
public interface DBuserRepository extends JpaRepository<DBUser, Long>, JpaSpecificationExecutor<DBUser> {
	// @Query("select * from user_table where email=?1")
	// public DAOUser getUserByUserEmail(String email);
	DBUser findByUserid(String userid);

	DBUser findByEmail(String email);

	DBUser findByPhone(String phone);
}
