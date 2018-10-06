package com.dropsnorz.owlplug.auth.dao;

import com.dropsnorz.owlplug.auth.model.UserAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserAccountDAO extends CrudRepository<UserAccount, Long> {

	@Transactional
	@Modifying
	@Query("DELETE FROM UserAccount u WHERE u.accountProvider = NULL")
	public void deleteInvalidAccounts();

}
