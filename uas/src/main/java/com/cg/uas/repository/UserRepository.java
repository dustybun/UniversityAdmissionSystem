package com.cg.uas.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.uas.dto.User;

@Repository

public interface UserRepository extends JpaRepository<User, BigInteger> {

	

}
