package com._9.inspect_pro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._9.inspect_pro.model.Profile;
import com._9.inspect_pro.model.ProfileType;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findByUserId(Long userId);

    List<Profile> findByType(ProfileType type);

    boolean existsByUserIdAndType(Long userId, ProfileType type);

    Optional<Profile> findByUserIdAndId(Long userId, Long profileId);

}
