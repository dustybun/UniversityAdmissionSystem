package com.cg.uas.service;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.uas.dto.Applicant;
import com.cg.uas.dto.Program;
import com.cg.uas.dto.ScheduleAvailable;
import com.cg.uas.dto.User;
import com.cg.uas.exception.UserErrorMessage;
import com.cg.uas.exception.ValidationException;
import com.cg.uas.repository.ApplicantRepository;
import com.cg.uas.repository.ProgramRepository;
import com.cg.uas.repository.ScheduleAvailableRepository;
import com.cg.uas.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private ScheduleAvailableRepository scheduleAvailableRepository;

	@Autowired
	private ProgramRepository programRepository;

	@Autowired
	private ApplicantRepository applicantRepository;

	@Autowired
	private UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	public UserServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ScheduleAvailable addSchedule(ScheduleAvailable schedule) {
		// TODO Auto-generated method stub
		return scheduleAvailableRepository.save(schedule);

	}

	@Override
	public boolean removeSchedule(BigInteger scheduleId) throws ValidationException {
		Optional<ScheduleAvailable> schedule = scheduleAvailableRepository.findById(scheduleId);
		if (!schedule.isPresent()) {
			throw new ValidationException(UserErrorMessage.userErrorInvalidScheduleId);
		}
		ScheduleAvailable scheduleAvailable = schedule.get();
		scheduleAvailable.setDeleted(true);
		 logger.info("Schedule Deleted. Audit Details: ScheduleID:"+scheduleAvailable.getScheduleId()+" Modified on:"+scheduleAvailable.getLastModifiedDate()+". Modified by: "+scheduleAvailable.getLastModifiedBy());
		return true;

	}

	public List<ScheduleAvailable> getScheduleList() {
		return scheduleAvailableRepository.getScheduleList();
	}

	public Program addProgram(BigInteger scheduleId, Program program) {
		System.out.println(program);
		ScheduleAvailable schedule = scheduleAvailableRepository.findById(scheduleId).get();
		System.out.println(program);
		schedule.getListOfPrograms().add(program);
		return programRepository.save(program);
	}

	@Override
	public ScheduleAvailable findSchedule(BigInteger scheduleId) {
		logger.info("Returning Schedule object of the schedule id..");
		return scheduleAvailableRepository.findById(scheduleId).get();
	}

	@Override
	public boolean removeProgram(BigInteger removeScheduleId, BigInteger removeProgramId) throws ValidationException {
		// TODO Auto-generated method stub
		ScheduleAvailable schedule = scheduleAvailableRepository.findById(removeScheduleId).get();
		if (schedule == null) {
			throw new ValidationException("Schedule is not present");
		}
		Program program = programRepository.findById(removeProgramId).get();
		if (program == null) {
			throw new ValidationException("program not found");
		}
		program.setDeleted(true);
		schedule.getListOfPrograms().remove(program);
		 logger.info("Program Deleted. AUDIT TRAIL=> ProgramID:"+program.getProgramId()+" Modified by:"+program.getLastModifiedBy()+"Modified on: "+program.getLastModifiedDate());
		return true;
	}

	@Override
	public Program findProgram(BigInteger programId) {
		return programRepository.findById(programId).get();
	}

	@Override
	public List<Program> getListOfPrograms(BigInteger scheduleId) {
		ScheduleAvailable schedule = scheduleAvailableRepository.findById(scheduleId).get();
		List<Program> programList = schedule.getListOfPrograms();
		return programList;
	}

	public Applicant addApplicant(Applicant applicant) {
		logger.info("Calling repository save method to save the Applicant in service layer..");
		return applicantRepository.save(applicant);
	}

	@Override
	public List<Applicant> getScheduleApplicantList(BigInteger scheduleId) throws ValidationException {
		Optional<ScheduleAvailable> schedule = scheduleAvailableRepository.findById(scheduleId);
		if (schedule.isPresent()) {
			return applicantRepository.findByScheduleAndApplicantStatus(schedule.get(), 0);
		} else {
			throw new ValidationException(UserErrorMessage.userErrorInvalidScheduleId);
		}
	}

//	@Override
//	public List<Applicant> getApplicantList(BigInteger applicantId) {
//		Applicant applicant = applicantRepository.findById(applicantId).get();
//		List<Applicant> applicantList = applicantRepository.findByApplicant(applicant);
//		return applicantList;
//	}

//	@Override
//	public List<Applicant> getScheduleApplicantList(BigInteger scheduleId) throws ValidationException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public List<Applicant> getApplicants() {
		// TODO Auto-generated method stub
		return applicantRepository.findAll();
	}

	@Override
	public List<Applicant> getApplicantList(BigInteger applicantId) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigInteger validateScheduleId(String scheduleId, List<ScheduleAvailable> scheduleList)
			throws ValidationException {

		if (scheduleId.matches("^[0-9]+")) {

			for (Iterator<ScheduleAvailable> iterator = scheduleList.iterator(); iterator.hasNext();) {
				ScheduleAvailable scheduleAvailable = iterator.next();
				if (scheduleAvailable.getScheduleId().compareTo(new BigInteger(scheduleId)) == 0) {
					logger.info("Schedule Id is proper... returning BigInteger value of the entered Id..");
					return new BigInteger(scheduleId);
				}
			}
		}
		logger.error("Invalid Schedule Id... throwing ValidationException in UserService.validateScheduleId");
		throw new ValidationException(UserErrorMessage.userErrorInvalidScheduleId);
	}

	public BigInteger validateProgramId(String programId, List<Program> programList) throws ValidationException {

		if (programId.matches("^[0-9]+")) {
			Iterator<Program> programIterator = programList.iterator();
			while (programIterator.hasNext()) {
				Program program = programIterator.next();
				if (program.getProgramId().compareTo(new BigInteger(programId)) == 0) {
					logger.info("Program Id is proper... returning BigInteger value of the entered Id..");
					return program.getProgramId();
				}
			}
		}
		logger.error("Invalid Program Id... throwing ValidationException in UserService.validateProgramId");
		throw new ValidationException(UserErrorMessage.userErrorInvalidProgramId);
	}

	public BigInteger validateApplicantId(String applicantId, List<Applicant> listOfApplicant)
			throws ValidationException {
		if (applicantId.matches("^[0-9]+")) {
			Applicant applicant = null;
			Iterator<Applicant> applicantListIterator = listOfApplicant.iterator();
			while (applicantListIterator.hasNext()) {
				applicant = applicantListIterator.next();
				if ((applicant.getApplicantId().compareTo(new BigInteger(applicantId)) == 0)
						&& (applicant.getApplicantAppliedStatus() == 0)) {
					logger.info("Correct applicant id... returning BigInteger value of the applicant Id");
					return new BigInteger(applicantId);
				}
			}
		}
		logger.error("Invalid Applicant Id.... throwing ValidationException");
		throw new ValidationException(UserErrorMessage.userErrorInvalidApplicantId);
	}

	public BigInteger validateUserId(String userId) throws ValidationException {
		if (userId.matches("^[0-9]+")) {
			List<User> listOfUser = userRepository.findAll();
			Iterator<User> userIterator = listOfUser.iterator();
			while (userIterator.hasNext()) {
				User user = userIterator.next();
				if (user.getUserId().compareTo(new BigInteger(userId)) == 0) {
					return new BigInteger(userId);
				}

			}
		}
		throw new ValidationException(UserErrorMessage.userErrorInvalidUserId);
	}

	@Override
	public boolean approveApplicant(BigInteger applicantId) throws ValidationException {
		logger.info("Getting the applicant object corresponding to the applicant Id...");
		Optional<Applicant> applicant = applicantRepository.findById(applicantId);
		if (applicant.isPresent()) {
			logger.info("Applicant object found.. setting status to 1 (Approved)");
			Applicant applicantObject = applicant.get();
			applicantObject.setApplicantAppliedStatus(1);
			 logger.info("Applicant status UPDATED. AUDIT TRAIL=> Applicant Id:"+applicantObject.getApplicantId()+" Modified on:"+applicantObject.getLastModifiedDate()+" Modified by:"+applicantObject.getLastModifiedBy());
			// logger.info("Sending confirmation email of approval to user..");
			// sendEmail(applicantObject);
		} else {
			logger.info("Manipulated applicant id... throwing ValidationException in service approve applicant");
			throw new ValidationException(UserErrorMessage.userErrorInvalidApplicantId);
		}
		return true;
	}

//	@Override
//	public boolean approveApplicant(Applicant applicant) throws ValidationException {
//		//logger.info("Getting the applicant object corresponding to the applicant Id...");
//		//Optional<Applicant> applicant = applicantRepository.findById(applicantId);
//		if (applicant!=null) {
//			//logger.info("Applicant object found.. setting status to 1 (Approved)");
////			Applicant applicantObject= applicant.get();
////			applicantObject.setApplicantAppliedStatus(1);
//			//logger.info("Applicant status UPDATED. AUDIT TRAIL=> Applicant Id: "+applicantObject.getApplicantId()+" Modified on: "+applicantObject.getLastModifiedDate()+" Modified by: "+applicantObject.getLastModifiedBy());
//			//logger.info("Sending confirmation email of approval to user..");
//			//sendEmail(applicantObject);
//			applicant.setApplicantAppliedStatus(1);
//			applicantRepository.save(applicant);
//		} else {
//			//logger.info("Manipulated applicant id... throwing ValidationException in service approve applicant");
//			throw new ValidationException(UserErrorMessage.userErrorInvalidApplicantId);
//		}
//		return true;
//	}
//	

	public boolean rejectApplicant(BigInteger applicantId) {
		Optional<Applicant> applicant = applicantRepository.findById(applicantId);
		if (applicant.isPresent()) {
			Applicant applicantObject = applicant.get();
			applicantObject.setApplicantAppliedStatus(2);

			// SimpleMailMessage msg = new SimpleMailMessage();
			// msg.setTo(applicantObject.getUser().getUserEmail());
			// msg.setSubject("Update on your applicant ID:
			// "+applicantObject.getApplicantId());
			// msg.setText("Sorry but your applicant application was rejected. For more
			// information, contact us by reverting on this email.");
			// javaMailSender.send(msg);

			 logger.info("applicant status UPDATED(Rejected). AUDIT TRAIL=> applicant Id:"+applicantObject.getApplicantId()+" Modified on:"+applicantObject.getLastModifiedDate()+" Modified by:"+applicantObject.getLastModifiedBy());
			return true;
		} else {
			return false;
		}
	}

}
