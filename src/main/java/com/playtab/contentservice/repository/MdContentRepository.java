package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.MdContent; // MdContent 엔티티 import
import java.util.Optional; // 상세 조회 결과를 Optional로 받기 위해 import
import org.springframework.data.domain.Page; // 페이징 목록 조회를 위해 import
import org.springframework.data.domain.Pageable; // Pageable 사용을 위해 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface MdContentRepository extends JpaRepository<MdContent, Long> {

    // 노출 가능한 MD 상품만 displayOrder 오름차순으로 페이징 조회
    @Query("SELECT mc FROM MdContent mc JOIN FETCH mc.contentItem ci WHERE ci.isVisible = true ORDER BY ci.displayOrder ASC")
    Page<MdContent> findAllByContentItemIsVisibleTrueOrderByContentItemDisplayOrderAsc(Pageable pageable);

    // 전체 MD 상품을 displayOrder 오름차순으로 페이징 조회
    @Query("SELECT mc FROM MdContent mc JOIN FETCH mc.contentItem ci ORDER BY ci.displayOrder ASC")
    Page<MdContent> findAllByOrderByContentItemDisplayOrderAsc(Pageable pageable);

    // 노출 가능한 특정 MD 상품 상세 조회
    @Query("SELECT mc FROM MdContent mc JOIN FETCH mc.contentItem ci WHERE mc.id = :id AND ci.isVisible = true")
    Optional<MdContent> findByIdAndContentItemIsVisibleTrue(@Param("id") Long id);

    // ID로 MD 상품 상세 조회 (contentItem 즉시 로딩)
    @NonNull
    @Query("SELECT mc FROM MdContent mc JOIN FETCH mc.contentItem WHERE mc.id = :id")
    Optional<MdContent> findById(@NonNull @Param("id") Long id);
}
