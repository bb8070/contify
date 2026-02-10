package com.example.contify.domain.follow.repository;

import com.example.contify.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f.followerId from Follow f where f.authorId = :authorId")
    List<Long> findFollowerIdsByAuthorId(@Param("authorId") Long authorId);

    boolean existsByAuthorIdAndFollowerId(Long authorId, Long followerId);

    long countByAuthorId(Long authorId);

}
