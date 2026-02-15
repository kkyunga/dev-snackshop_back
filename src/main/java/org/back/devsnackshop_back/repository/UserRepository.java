package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT a FROM UserEntity a WHERE a.name=:name AND a.phoneNumber=:phone")
    Optional<UserEntity> findByNameAndPhone(@Param("name") String name, @Param("phone") String phone);
}
