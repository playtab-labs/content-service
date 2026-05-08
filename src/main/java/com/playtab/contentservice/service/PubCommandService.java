package com.playtab.contentservice.service;

import com.playtab.contentservice.entity.ContentItem;
import com.playtab.contentservice.entity.PubContent;
import com.playtab.contentservice.entity.enums.ContentType;
import com.playtab.contentservice.exception.ContentServiceException;
import com.playtab.contentservice.exception.ErrorCode;
import com.playtab.contentservice.repository.ContentItemRepository;
import com.playtab.contentservice.repository.PubContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PubCommandService {

    private final PubContentRepository pubContentRepository;
    private final ContentItemRepository contentItemRepository;

    public PubContent create(Map<String, String> collegeName, boolean isNameConfirmed,
                             String thumbnailImageUrl, boolean isVisible, int displayOrder) {
        if (collegeName == null || collegeName.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        ContentItem contentItem = ContentItem.create(ContentType.PUB, thumbnailImageUrl, isVisible, displayOrder);
        contentItemRepository.save(contentItem);
        PubContent pub = PubContent.create(contentItem, collegeName, isNameConfirmed);
        return pubContentRepository.save(pub);
    }

    public PubContent update(long id, Map<String, String> collegeName, boolean isNameConfirmed,
                             String thumbnailImageUrl, boolean isVisible, int displayOrder) {
        if (collegeName == null || collegeName.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        PubContent pub = pubContentRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.PUB_NOT_FOUND));
        pub.getContentItem().update(thumbnailImageUrl, isVisible, displayOrder);
        pub.update(collegeName, isNameConfirmed);
        return pub;
    }

    public void delete(long id) {
        PubContent pub = pubContentRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.PUB_NOT_FOUND));
        ContentItem contentItem = pub.getContentItem();
        pubContentRepository.delete(pub);
        contentItemRepository.delete(contentItem);
    }
}
