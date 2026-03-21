package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}