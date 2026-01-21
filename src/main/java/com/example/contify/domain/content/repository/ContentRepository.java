package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> , ContentRepositoryCustom {

}
