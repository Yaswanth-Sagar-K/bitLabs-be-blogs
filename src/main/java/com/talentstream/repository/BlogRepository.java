package com.talentstream.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talentstream.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, UUID> {
	List<Blog> findByIsActiveTrue(); 
	List<Blog> findByIsActiveFalse(); 
    Optional<Blog> findByUrlOrTitle(String url, String title);
}