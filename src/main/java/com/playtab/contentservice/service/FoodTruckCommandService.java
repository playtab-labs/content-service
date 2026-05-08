package com.playtab.contentservice.service;

import com.playtab.contentservice.entity.ContentItem;
import com.playtab.contentservice.entity.FoodTruckContent;
import com.playtab.contentservice.entity.enums.ContentType;
import com.playtab.contentservice.exception.ContentServiceException;
import com.playtab.contentservice.exception.ErrorCode;
import com.playtab.contentservice.repository.ContentItemRepository;
import com.playtab.contentservice.repository.FoodTruckContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodTruckCommandService {

    private final FoodTruckContentRepository foodTruckContentRepository;
    private final ContentItemRepository contentItemRepository;

    public FoodTruckContent create(Map<String, String> name, Map<String, String> shortDescription,
                                   String thumbnailImageUrl, boolean isVisible, int displayOrder) {
        if (name == null || name.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        ContentItem contentItem = ContentItem.create(ContentType.FOOD_TRUCK, thumbnailImageUrl, isVisible, displayOrder);
        contentItemRepository.save(contentItem);
        FoodTruckContent foodTruck = FoodTruckContent.create(contentItem, name, shortDescription);
        return foodTruckContentRepository.save(foodTruck);
    }

    public FoodTruckContent update(long id, Map<String, String> name, Map<String, String> shortDescription,
                                   String thumbnailImageUrl, boolean isVisible, int displayOrder) {
        if (name == null || name.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        FoodTruckContent foodTruck = foodTruckContentRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.FOOD_TRUCK_NOT_FOUND));
        foodTruck.getContentItem().update(thumbnailImageUrl, isVisible, displayOrder);
        foodTruck.update(name, shortDescription);
        return foodTruck;
    }

    public void delete(long id) {
        FoodTruckContent foodTruck = foodTruckContentRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.FOOD_TRUCK_NOT_FOUND));
        ContentItem contentItem = foodTruck.getContentItem();
        foodTruckContentRepository.delete(foodTruck);
        contentItemRepository.delete(contentItem);
    }
}
