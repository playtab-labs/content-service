package com.playtab.contentservice.service;

import com.playtab.contentservice.entity.FoodTruckContent; // 푸드트럭 엔티티 사용
import com.playtab.contentservice.entity.PubContent; // 주점 엔티티 사용
import com.playtab.contentservice.entity.MdContent; // MD 엔티티 사용
import com.playtab.contentservice.entity.Notice; // 공지 엔티티 사용
import com.playtab.contentservice.entity.MdOptionGroup; // MD 옵션 그룹 엔티티 사용
import com.playtab.contentservice.entity.MdOptionValue; // MD 옵션 값 엔티티 사용
import com.playtab.contentservice.repository.FoodTruckContentRepository;
import com.playtab.contentservice.repository.MdContentRepository;
import com.playtab.contentservice.repository.MdOptionGroupRepository;
import com.playtab.contentservice.repository.MdOptionValueRepository;
import com.playtab.contentservice.repository.NoticeRepository;
import com.playtab.contentservice.repository.PubContentRepository;
import java.util.List; // 목록 반환을 위해 import
import java.util.Optional; // 상세 조회 결과를 Optional로 받기 위해 import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page; // 페이징 결과 사용
import org.springframework.data.domain.Pageable; // pageable 사용

@Service // 스프링 서비스 빈으로 등록
@RequiredArgsConstructor // final 필드 생성자 자동 주입
public class ContentQueryService {

    // 푸드트럭 조회용 repository
    private final FoodTruckContentRepository foodTruckContentRepository;

    // 주점 조회용 repository
    private final PubContentRepository pubContentRepository;

    // MD 조회용 repository
    private final MdContentRepository mdContentRepository;

    // MD 옵션 그룹 조회용 repository
    private final MdOptionGroupRepository mdOptionGroupRepository;

    // MD 옵션 값 조회용 repository
    private final MdOptionValueRepository mdOptionValueRepository;

    // 공지 조회용 repository
    private final NoticeRepository noticeRepository;

    // 푸드트럭 목록 조회
    public List<FoodTruckContent> getFoodTrucks(boolean onlyVisible) {
        if (onlyVisible) {
            // 노출 가능한 푸드트럭만 조회
            return foodTruckContentRepository.findAllByContentItemIsVisibleTrueOrderByContentItemDisplayOrderAsc();
        }

        // 전체 푸드트럭 조회
        return foodTruckContentRepository.findAllByOrderByContentItemDisplayOrderAsc();
    }

    // 주점 목록 조회
    public List<PubContent> getPubs(boolean onlyVisible) {
        if (onlyVisible) {
            // 노출 가능한 주점만 조회
            return pubContentRepository.findAllByContentItemIsVisibleTrueOrderByContentItemDisplayOrderAsc();
        }

        // 전체 주점 조회
        return pubContentRepository.findAllByOrderByContentItemDisplayOrderAsc();
    }

    // MD 상품 목록 조회
    public Page<MdContent> getMdItems(boolean onlyVisible, Pageable pageable) {
        if (onlyVisible) {
            // 노출 가능한 MD 상품만 페이징 조회
            return mdContentRepository.findAllByContentItemIsVisibleTrueOrderByContentItemDisplayOrderAsc(pageable);
        }

        // 전체 MD 상품 페이징 조회
        return mdContentRepository.findAllByOrderByContentItemDisplayOrderAsc(pageable);
    }

    // 공지사항 목록 조회
    public Page<Notice> getNotices(boolean onlyVisible, Pageable pageable) {
        if (onlyVisible) {
            // 노출 가능한 공지사항만 고정 공지 우선, 최신순으로 페이징 조회
            return noticeRepository.findAllByIsVisibleTrueOrderByIsPinnedDescPostedAtDesc(pageable);
        }

        // 전체 공지사항을 고정 공지 우선, 최신순으로 페이징 조회
        return noticeRepository.findAllByOrderByIsPinnedDescPostedAtDesc(pageable);
    }

    // MD 상품 상세 조회
    public Optional<MdContent> getMdItemDetail(Long mdItemId, boolean onlyVisible) {
        if (onlyVisible) {
            // 노출 가능한 특정 MD 상품 상세 조회
            return mdContentRepository.findByIdAndContentItemIsVisibleTrue(mdItemId);
        }
        // 전체 범위에서 특정 MD 상품 상세 조회
        return mdContentRepository.findById(mdItemId);
    }

    // 특정 MD 상품의 옵션 그룹 목록 조회
    public List<MdOptionGroup> getMdOptionGroups(Long mdContentId) {
        // displayOrder 오름차순으로 옵션 그룹 조회
        return mdOptionGroupRepository.findAllByMdContentIdOrderByDisplayOrderAsc(mdContentId);
    }

    // 특정 옵션 그룹의 옵션 값 조회
    public List<MdOptionValue> getMdOptionValues(Long optionGroupId) {
        // 오름차순으로 옵션 값 조회
        return mdOptionValueRepository.findAllByOptionGroupIdOrderByDisplayOrderAsc(optionGroupId);
    }

    // 공지사항 상세 조회
    public Optional<Notice> getNoticeDetail(Long noticeId, boolean onlyVisible) {
        if (onlyVisible) {
            // 노출 가능한 특정 공지사항 상세 조회
            return noticeRepository.findByIdAndIsVisibleTrue(noticeId);
        }

        // 전체 범위에서 특정 공지사항 상세 조회
        return noticeRepository.findById(noticeId);
    }
}

