package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.entity.LikeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeLogRepository extends JpaRepository<LikeLog, Long> {
}
