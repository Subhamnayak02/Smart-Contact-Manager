package com.smart.rep;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Contact;

public interface ContactRepo extends JpaRepository<Contact, Integer>{
     
	@Query("from Contact as c where c.user.id =:userId")
	// cuurent page no of page
	public Page<Contact> findContactbyId(@Param("userId") int id ,Pageable pageable);
	
	
	
}
