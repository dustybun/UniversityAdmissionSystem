package com.cg.uas.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cg.uas.dto.ScheduleAvailable;

@Repository
public interface ScheduleAvailableRepository extends JpaRepository<ScheduleAvailable, BigInteger> {
	
	@Query("FROM ScheduleAvailable WHERE isDeleted = 'false'")
	public List<ScheduleAvailable> getScheduleList();


}
