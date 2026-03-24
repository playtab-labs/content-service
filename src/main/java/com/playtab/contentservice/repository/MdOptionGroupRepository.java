package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.MdOptionGroup; // MdOptionGroup 엔티티 import
import java.util.List; // 옵션 그룹 목록 조회를 위해 import
import org.springframework.data.jpa.repository.JpaRepository;

public interface MdOptionGroupRepository extends JpaRepository<MdOptionGroup, Long> {

    // 특정 MD 상품의 옵션 그룹을 displayOrder 오름차순으로 조회
    List<MdOptionGroup> findAllByMdContentIdOrderByDisplayOrderAsc(Long mdContentId);
}