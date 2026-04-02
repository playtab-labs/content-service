package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.PubContent;
import java.util.List; // 목록 조회를 위해 List import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PubContentRepository extends JpaRepository<PubContent, Long> {

    // 노출 가능한 주점만 displayOrder 오름차순으로 조회
    @Query("SELECT p FROM PubContent p JOIN FETCH p.contentItem ci WHERE ci.isVisible = true ORDER BY ci.displayOrder ASC")
    List<PubContent> findAllByContentItemIsVisibleTrueOrderByContentItemDisplayOrderAsc();

    // 전체 주점을 displayOrder 오름차순으로 조회
    @Query("SELECT p FROM PubContent p JOIN FETCH p.contentItem ci ORDER BY ci.displayOrder ASC")
    List<PubContent> findAllByOrderByContentItemDisplayOrderAsc();
}
