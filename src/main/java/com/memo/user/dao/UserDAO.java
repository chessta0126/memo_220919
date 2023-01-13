package com.memo.user.dao;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface UserDAO {
	public boolean existLoginId(String loginId);
	public void insertUser(
			@RequestParam("loginId") String loginId
			,@RequestParam("hashedPassword") String hashedPassword
			,@RequestParam("name") String name
			,@RequestParam("email") String email);
}
