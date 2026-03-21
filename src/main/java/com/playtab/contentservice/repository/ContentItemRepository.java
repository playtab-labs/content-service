package com.playtab.contentservice.repository;

import com.playtab.contentservice.entity.ContentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentItemRepository extends JpaRepository<ContentItem, Long> {
}