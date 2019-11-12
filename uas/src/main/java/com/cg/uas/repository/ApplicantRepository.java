package com.cg.uas.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cg.uas.dto.Applicant;
import com.cg.uas.dto.ScheduleAvailable;
import com.cg.uas.dto.User;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, BigInteger>{
	@Query("FROM Applicant WHERE schedule = :schedule AND	 applicant_applied_status = :status")
		public List<Applicant> findByScheduleAndApplicantStatus(@Param("schedule")ScheduleAvailable schedule,@Param("status") int status);

//		@Query("FROM Applicant WHERE applicant = :applicantObject")
		//public List<Applicant> findByUser(@Param("userObject")User user);

	//public List<Applicant> findByScheduleAndApplicantStatus(@Param("schedule")ScheduleAvailable schedule,@Param("status") int status);

	

}
