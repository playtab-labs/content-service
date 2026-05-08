package com.playtab.contentservice.service;

import com.playtab.contentservice.entity.*;
import com.playtab.contentservice.entity.enums.ContentType;
import com.playtab.contentservice.exception.ContentServiceException;
import com.playtab.contentservice.exception.ErrorCode;
import com.playtab.contentservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MdCommandService {

    private final MdContentRepository mdContentRepository;
    private final MdOptionGroupRepository mdOptionGroupRepository;
    private final MdOptionValueRepository mdOptionValueRepository;
    private final ContentItemRepository contentItemRepository;

    public MdContent createItem(Map<String, String> name, int price,
                                Map<String, String> productDescription,
                                Map<String, String> detailDescription,
                                String thumbnailImageUrl, String detailImageUrl,
                                boolean isSoldOut, boolean isVisible, int displayOrder) {
        if (name == null || name.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        ContentItem contentItem = ContentItem.create(ContentType.MD, thumbnailImageUrl, isVisible, displayOrder);
        contentItemRepository.save(contentItem);
        MdContent md = MdContent.create(contentItem, name, price, productDescription,
                detailDescription, detailImageUrl, isSoldOut);
        return mdContentRepository.save(md);
    }

    public MdContent updateItem(long id, Map<String, String> name, int price,
                                Map<String, String> productDescription,
                                Map<String, String> detailDescription,
                                String thumbnailImageUrl, String detailImageUrl,
                                boolean isSoldOut, boolean isVisible, int displayOrder) {
        if (name == null || name.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        MdContent md = mdContentRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.MD_ITEM_NOT_FOUND));
        md.getContentItem().update(thumbnailImageUrl, isVisible, displayOrder);
        md.update(name, price, productDescription, detailDescription, detailImageUrl, isSoldOut);
        return md;
    }

    public void deleteItem(long id) {
        MdContent md = mdContentRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.MD_ITEM_NOT_FOUND));
        ContentItem contentItem = md.getContentItem();
        mdContentRepository.delete(md);
        contentItemRepository.delete(contentItem);
    }

    public MdOptionGroup createOptionGroup(long mdItemId, Map<String, String> name, int displayOrder) {
        if (name == null || name.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        MdContent md = mdContentRepository.findById(mdItemId)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.MD_ITEM_NOT_FOUND));
        MdOptionGroup group = MdOptionGroup.create(md, name, displayOrder);
        return mdOptionGroupRepository.save(group);
    }

    public MdOptionGroup updateOptionGroup(long id, Map<String, String> name, int displayOrder) {
        if (name == null || name.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        MdOptionGroup group = mdOptionGroupRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.MD_OPTION_GROUP_NOT_FOUND));
        group.update(name, displayOrder);
        return group;
    }

    public void deleteOptionGroup(long id) {
        MdOptionGroup group = mdOptionGroupRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.MD_OPTION_GROUP_NOT_FOUND));
        mdOptionGroupRepository.delete(group);
    }

    public MdOptionValue createOptionValue(long optionGroupId, Map<String, String> valueName,
                                           int extraPrice, boolean isSoldOut, int displayOrder) {
        if (valueName == null || valueName.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        MdOptionGroup group = mdOptionGroupRepository.findById(optionGroupId)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.MD_OPTION_GROUP_NOT_FOUND));
        MdOptionValue value = MdOptionValue.create(group, valueName, extraPrice, isSoldOut, displayOrder);
        return mdOptionValueRepository.save(value);
    }

    public MdOptionValue updateOptionValue(long id, Map<String, String> valueName,
                                           int extraPrice, boolean isSoldOut, int displayOrder) {
        if (valueName == null || valueName.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        MdOptionValue value = mdOptionValueRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.MD_OPTION_VALUE_NOT_FOUND));
        value.update(valueName, extraPrice, isSoldOut, displayOrder);
        return value;
    }

    public void deleteOptionValue(long id) {
        MdOptionValue value = mdOptionValueRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.MD_OPTION_VALUE_NOT_FOUND));
        mdOptionValueRepository.delete(value);
    }
}
