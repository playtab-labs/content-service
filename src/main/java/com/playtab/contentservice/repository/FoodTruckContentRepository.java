package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.FoodTruckContent;
import java.util.List; // 목록 조회를 위해 List import
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTruckContentRepository extends JpaRepository<FoodTruckContent, Long> {

    // 노출 가능한 푸드트럭만 displayOrder 오름차순으로 조회
    List<FoodTruckContent> findAllByContentItemIsVisibleTrueOrderByContentItemDisplayOrderAsc();

    // 전체 푸드트럭을 displayOrder 오름차순으로 조회
    List<FoodTruckContent> findAllByOrderByContentItemDisplayOrderAsc();
}