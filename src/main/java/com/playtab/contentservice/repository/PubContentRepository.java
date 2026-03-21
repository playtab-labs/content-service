package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.PubContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PubContentRepository extends JpaRepository<PubContent, Long> {
}