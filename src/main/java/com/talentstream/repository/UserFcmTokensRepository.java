package com.talentstream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.UserFcmTokens;

@Repository
public interface UserFcmTokensRepository extends JpaRepository<UserFcmTokens, Long> {
    
	 List<UserFcmTokens> findByApplicant_IdAndIsTokenActiveTrue(Long applicantId);
	 List<UserFcmTokens> findByApplicant_Id(Long applicantId);
	 List<UserFcmTokens> findByIsTokenActiveTrue();
}