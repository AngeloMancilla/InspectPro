package com._9.inspect_pro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com._9.inspect_pro.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profiles WHERE u.id = :id")
    Optional<User> findByIdWithProfiles(@Param("id") Long id);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profiles WHERE u.email = :email")
    Optional<User> findByEmailWithProfiles(@Param("email") String email);

}
