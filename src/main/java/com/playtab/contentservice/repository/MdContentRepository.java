package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.MdContent; // MdContent 엔티티 import
import java.util.Optional; // 상세 조회 결과를 Optional로 받기 위해 import
import org.springframework.data.domain.Page; // 페이징 목록 조회를 위해 import
import org.springframework.data.domain.Pageable; // Pageable 사용을 위해 import
import org.springframework.data.jpa.repository.JpaRepository;

public interface MdContentRepository extends JpaRepository<MdContent, Long> {

    // 노출 가능한 MD 상품만 displayOrder 오름차순으로 페이징 조회
    Page<MdContent> findAllByContentItemIsVisibleTrueOrderByContentItemDisplayOrderAsc(Pageable pageable);

    // 전체 MD 상품을 displayOrder 오름차순으로 페이징 조회
    Page<MdContent> findAllByOrderByContentItemDisplayOrderAsc(Pageable pageable);

    // 노출 가능한 특정 MD 상품 상세 조회
    Optional<MdContent> findByIdAndContentItemIsVisibleTrue(Long id);
}