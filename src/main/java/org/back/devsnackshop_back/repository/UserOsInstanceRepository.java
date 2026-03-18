package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOsInstanceRepository extends JpaRepository<UserOsInstanceEntity, Long> {
    List<UserOsInstanceEntity> findByUser(UserEntity userEntity);
    Optional<UserOsInstanceEntity> findByIpAddressAndPortNumber(String ipAddress, Long portNumber);

    Optional<UserOsInstanceEntity> findByUserAndId(UserEntity user, Long id);

    void deleteById(Long id);

    // UserOsInstanceRepository에 추가
    @Query("SELECT u FROM UserOsInstanceEntity u " +
            "JOIN FETCH u.user " +
            "JOIN FETCH u.cloud " +
            "JOIN FETCH u.os " +
            "WHERE u.id = :id")
    Optional<UserOsInstanceEntity> findByIdWithAll(@Param("id") Long id);
}
