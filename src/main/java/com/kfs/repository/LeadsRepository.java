package com.kfs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kfs.model.lead;


@Repository
public interface LeadsRepository extends JpaRepository<lead,Long> {
	
	@Query(value = "SELECT tenure FROM leads WHERE id = '7889143' limit 1", nativeQuery = true)
    String findByTenure();

}
