package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.MdOptionValue;
import java.util.List; // 옵션 값 목록 조회를 위해 import
import org.springframework.data.jpa.repository.JpaRepository;

public interface MdOptionValueRepository extends JpaRepository<MdOptionValue, Long> {

    // 특정 옵션 그룹의 옵션 값들을 displayOrder 오름차순으로 조회
    List<MdOptionValue> findAllByOptionGroupIdOrderByDisplayOrderAsc(Long optionGroupId);
}