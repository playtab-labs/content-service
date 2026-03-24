package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.Notice;
import java.util.Optional; // 상세 조회 결과를 Optional로 받기 위해 import
import org.springframework.data.domain.Page; // 페이징 목록 조회를 위해 import
import org.springframework.data.domain.Pageable; // Pageable 사용을 위해 import
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 노출 가능한 공지사항만 고정 공지 우선, 최신순으로 페이징 조회
    Page<Notice> findAllByIsVisibleTrueOrderByIsPinnedDescPostedAtDesc(Pageable pageable);

    // 전체 공지사항을 고정 공지 우선, 최신순으로 페이징 조회
    Page<Notice> findAllByOrderByIsPinnedDescPostedAtDesc(Pageable pageable);

    // 노출 가능한 특정 공지사항 상세 조회
    Optional<Notice> findByIdAndIsVisibleTrue(Long id);
}