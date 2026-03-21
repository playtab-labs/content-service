package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.FoodTruckContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTruckContentRepository extends JpaRepository<FoodTruckContent, Long> {
}