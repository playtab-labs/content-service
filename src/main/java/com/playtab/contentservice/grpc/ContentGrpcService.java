package com.playtab.contentservice.grpc;

import com.playtab.contentservice.config.GrpcIdentityInterceptor;
import com.playtab.contentservice.entity.*;
import com.playtab.contentservice.entity.MdOptionGroup;
import com.playtab.contentservice.entity.MdOptionValue;
import com.playtab.contentservice.exception.GlobalGrpcExceptionHandler;
import com.playtab.contentservice.grpc.proto.v1.*;
import com.playtab.contentservice.service.ContentQueryService;
import com.playtab.contentservice.service.FoodTruckCommandService;
import com.playtab.contentservice.service.MdCommandService;
import com.playtab.contentservice.service.NoticeCommandService;
import com.playtab.contentservice.service.PubCommandService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@GrpcService // gRPC 서비스로 등록
@RequiredArgsConstructor // final 필드 생성자 자동 주입
public class ContentGrpcService extends ContentServiceGrpc.ContentServiceImplBase {

    // 공지 목록 미리보기 본문 최대 길이 (코드 포인트 기준)
    private static final int NOTICE_CONTENT_PREVIEW_MAX_LENGTH = 150;

    // 조회 로직을 담은 service 주입
    private final ContentQueryService contentQueryService;

    private final NoticeCommandService noticeCommandService;
    private final FoodTruckCommandService foodTruckCommandService;
    private final PubCommandService pubCommandService;
    private final MdCommandService mdCommandService;
    private final GlobalGrpcExceptionHandler globalGrpcExceptionHandler;

    @Override
    public void getFoodTrucks(GetFoodTrucksRequest request,
                              StreamObserver<GetFoodTrucksResponse> responseObserver) {
        // 서비스 계층에서 푸드트럭 목록 조회
        List<FoodTruckContent> foodTrucks = contentQueryService.getFoodTrucks(request.getOnlyVisible());

        // proto response builder 생성
        GetFoodTrucksResponse.Builder responseBuilder = GetFoodTrucksResponse.newBuilder();

        for (FoodTruckContent foodTruck : foodTrucks) {
            // locale에 맞는 name 추출, 없으면 빈 문자열
            String name = foodTruck.getName().getOrDefault(request.getLocale(), "");

            // locale에 맞는 shortDescription 추출, 없으면 빈 문자열
            String shortDescription = foodTruck.getShortDescription().getOrDefault(request.getLocale(), "");

            // entity -> proto item 변환
            FoodTruckItem item = FoodTruckItem.newBuilder()
                    .setId(foodTruck.getId())
                    .setName(name)
                    .setThumbnailImageUrl(
                            foodTruck.getContentItem().getThumbnailImageUrl() == null
                                    ? ""
                                    : foodTruck.getContentItem().getThumbnailImageUrl()
                    )
                    .setShortDescription(shortDescription)
                    .setDisplayOrder(foodTruck.getContentItem().getDisplayOrder())
                    .build();

            responseBuilder.addFoodTrucks(item);
        }

        // 응답 전송
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPubs(GetPubsRequest request,
                        StreamObserver<GetPubsResponse> responseObserver) {
        // 서비스 계층에서 주점 목록 조회
        List<PubContent> pubs = contentQueryService.getPubs(request.getOnlyVisible());

        // proto response builder 생성
        GetPubsResponse.Builder responseBuilder = GetPubsResponse.newBuilder();

        for (PubContent pub : pubs) {
            // locale에 맞는 collegeName 추출, 없으면 빈 문자열
            String collegeName = pub.getCollegeName().getOrDefault(request.getLocale(), "");

            // entity -> proto item 변환
            PubItem item = PubItem.newBuilder()
                    .setId(pub.getId())
                    .setCollegeName(collegeName)
                    .setThumbnailImageUrl(
                            pub.getContentItem().getThumbnailImageUrl() == null
                                    ? ""
                                    : pub.getContentItem().getThumbnailImageUrl()
                    )
                    .setIsNameConfirmed(pub.isNameConfirmed())
                    .setDisplayOrder(pub.getContentItem().getDisplayOrder())
                    .build();

            responseBuilder.addPubs(item);
        }

        // 응답 전송
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMdItems(GetMdItemsRequest request,
                           StreamObserver<GetMdItemsResponse> responseObserver) {
        // proto pageRequest -> Spring PageRequest 변환 (size 미전달 시 기본값 20)
        int page = request.getPageRequest().getPage();
        int size = request.getPageRequest().getSize() > 0 ? request.getPageRequest().getSize() : 20;

        PageRequest pageable = PageRequest.of(page, size);

        // 서비스 계층에서 MD 목록 조회
        Page<MdContent> mdPage = contentQueryService.getMdItems(request.getOnlyVisible(), pageable);

        // proto response builder 생성
        GetMdItemsResponse.Builder responseBuilder = GetMdItemsResponse.newBuilder();

        for (MdContent mdContent : mdPage.getContent()) {
            // locale에 맞는 name 추출, 없으면 빈 문자열
            String name = mdContent.getName().getOrDefault(request.getLocale(), "");

            // entity -> proto item 변환
            MdItemSummary item = MdItemSummary.newBuilder()
                    .setId(mdContent.getId())
                    .setName(name)
                    .setThumbnailImageUrl(
                            mdContent.getContentItem().getThumbnailImageUrl() == null
                                    ? ""
                                    : mdContent.getContentItem().getThumbnailImageUrl()
                    )
                    .setPrice(mdContent.getPrice())
                    .setIsSoldOut(mdContent.isSoldOut())
                    .build();

            responseBuilder.addItems(item);
        }

        // pageInfo 구성
        PageInfo pageInfo = PageInfo.newBuilder()
                .setPage(mdPage.getNumber())
                .setSize(mdPage.getSize())
                .setTotalElements(mdPage.getTotalElements())
                .setTotalPages(mdPage.getTotalPages())
                .setHasNext(mdPage.hasNext())
                .build();

        responseBuilder.setPageInfo(pageInfo);

        // 응답 전송
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getNotices(GetNoticesRequest request,
                           StreamObserver<GetNoticesResponse> responseObserver) {
        // proto pageRequest -> Spring PageRequest 변환 (size 미전달 시 기본값 20)
        int page = request.getPageRequest().getPage();
        int size = request.getPageRequest().getSize() > 0 ? request.getPageRequest().getSize() : 20;

        PageRequest pageable = PageRequest.of(page, size);

        // 서비스 계층에서 공지 목록 조회
        Page<Notice> noticePage = contentQueryService.getNotices(request.getOnlyVisible(), pageable);

        // proto response builder 생성
        GetNoticesResponse.Builder responseBuilder = GetNoticesResponse.newBuilder();

        for (Notice notice : noticePage.getContent()) {
            // locale에 맞는 title 추출, 없으면 빈 문자열
            String title = notice.getTitle().getOrDefault(request.getLocale(), "");

            // locale에 맞는 content 추출 후 미리보기 길이로 자르기
            String content = notice.getContent().getOrDefault(request.getLocale(), "");
            String contentPreview = truncateForPreview(content, NOTICE_CONTENT_PREVIEW_MAX_LENGTH);

            // entity -> proto item 변환
            NoticeSummary item = NoticeSummary.newBuilder()
                    .setId(notice.getId())
                    .setTitle(title)
                    .setContentPreview(contentPreview)
                    .setPostedAt(notice.getPostedAt() == null ? "" : notice.getPostedAt().toString())
                    .setIsPinned(notice.isPinned())
                    .setImageUrl(notice.getImageUrl() == null ? "" : notice.getImageUrl())
                    .build();

            responseBuilder.addNotices(item);
        }

        // pageInfo 구성
        PageInfo pageInfo = PageInfo.newBuilder()
                .setPage(noticePage.getNumber())
                .setSize(noticePage.getSize())
                .setTotalElements(noticePage.getTotalElements())
                .setTotalPages(noticePage.getTotalPages())
                .setHasNext(noticePage.hasNext())
                .build();

        responseBuilder.setPageInfo(pageInfo);

        // 응답 전송
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMdItemDetail(GetMdItemDetailRequest request,
                                StreamObserver<GetMdItemDetailResponse> responseObserver) {
        // 서비스 계층에서 MD 상세 조회
        Optional<MdContent> mdContentOptional =
                contentQueryService.getMdItemDetail(request.getMdItemId(), true);

        // 데이터가 없으면 빈 응답 반환
        if (mdContentOptional.isEmpty()) {
            responseObserver.onNext(GetMdItemDetailResponse.newBuilder().build());
            responseObserver.onCompleted();
            return;
        }

        MdContent mdContent = mdContentOptional.get();

        // proto 상세 builder 생성
        MdItemDetail.Builder detailBuilder = MdItemDetail.newBuilder()
                .setId(mdContent.getId())
                .setName(mdContent.getName().getOrDefault(request.getLocale(), ""))
                .setThumbnailImageUrl(
                        mdContent.getContentItem().getThumbnailImageUrl() == null
                                ? ""
                                : mdContent.getContentItem().getThumbnailImageUrl()
                )
                .setDetailImageUrl(mdContent.getDetailImageUrl() == null ? "" : mdContent.getDetailImageUrl())
                .setPrice(mdContent.getPrice())
                .setIsSoldOut(mdContent.isSoldOut())
                .setProductDescription(mdContent.getProductDescription().getOrDefault(request.getLocale(), ""))
                .setDetailDescription(mdContent.getDetailDescription().getOrDefault(request.getLocale(), ""));

        // 서비스 계층에서 옵션 그룹 목록 조회
        List<MdOptionGroup> optionGroups = contentQueryService.getMdOptionGroups(mdContent.getId());

        for (MdOptionGroup optionGroup : optionGroups) {
            // proto 옵션 그룹 builder 생성
            com.playtab.contentservice.grpc.proto.v1.MdOptionGroup.Builder optionGroupBuilder =
                    com.playtab.contentservice.grpc.proto.v1.MdOptionGroup.newBuilder()
                            .setId(optionGroup.getId())
                            .setName(optionGroup.getName().getOrDefault(request.getLocale(), ""))
                            .setDisplayOrder(optionGroup.getDisplayOrder());

            // 서비스 계층에서 옵션 값 목록 조회
            List<MdOptionValue> optionValues = contentQueryService.getMdOptionValues(optionGroup.getId());

            for (MdOptionValue optionValue : optionValues) {
                // entity -> proto 옵션 값 변환
                com.playtab.contentservice.grpc.proto.v1.MdOptionValue value =
                        com.playtab.contentservice.grpc.proto.v1.MdOptionValue.newBuilder()
                                .setId(optionValue.getId())
                                .setValueName(optionValue.getValueName().getOrDefault(request.getLocale(), ""))
                                .setExtraPrice(optionValue.getExtraPrice())
                                .setIsSoldOut(optionValue.isSoldOut())
                                .setDisplayOrder(optionValue.getDisplayOrder())
                                .build();

                optionGroupBuilder.addValues(value);
            }

            detailBuilder.addOptionGroups(optionGroupBuilder.build());
        }

        // 최종 응답 생성
        GetMdItemDetailResponse response = GetMdItemDetailResponse.newBuilder()
                .setItem(detailBuilder.build())
                .build();

        // 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getNoticeDetail(GetNoticeDetailRequest request,
                                StreamObserver<GetNoticeDetailResponse> responseObserver) {
        // 서비스 계층에서 공지 상세 조회
        Optional<Notice> noticeOptional =
                contentQueryService.getNoticeDetail(request.getNoticeId(), true);

        // 데이터가 없으면 빈 응답 반환
        if (noticeOptional.isEmpty()) {
            responseObserver.onNext(GetNoticeDetailResponse.newBuilder().build());
            responseObserver.onCompleted();
            return;
        }

        Notice notice = noticeOptional.get();

        // proto 상세 builder 생성
        NoticeDetail detail = NoticeDetail.newBuilder()
                .setId(notice.getId())
                .setTitle(notice.getTitle().getOrDefault(request.getLocale(), ""))
                .setContent(notice.getContent().getOrDefault(request.getLocale(), ""))
                .setPostedAt(notice.getPostedAt() == null ? "" : notice.getPostedAt().toString())
                .setIsPinned(notice.isPinned())
                .setImageUrl(notice.getImageUrl() == null ? "" : notice.getImageUrl())
                .build();

        // 최종 응답 생성
        GetNoticeDetailResponse response = GetNoticeDetailResponse.newBuilder()
                .setNotice(detail)
                .build();

        // 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // 코드 포인트 기준으로 잘라 surrogate pair(이모지 등) 깨짐을 방지
    private static String truncateForPreview(String text, int maxLength) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (text.codePointCount(0, text.length()) <= maxLength) {
            return text;
        }
        int endIndex = text.offsetByCodePoints(0, maxLength);
        return text.substring(0, endIndex) + "...";
    }

    @Override
    public void adminCreateNotice(AdminCreateNoticeRequest request,
                                  StreamObserver<AdminCreateNoticeResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();

            Notice created = noticeCommandService.create(
                    request.getTitleMap(),
                    request.getContentMap(),
                    request.getPostedAt(),
                    request.getIsPinned(),
                    request.getIsVisible(),
                    request.getImageUrl()
            );

            responseObserver.onNext(AdminCreateNoticeResponse.newBuilder()
                    .setNotice(toAdminNoticeProto(created))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminUpdateNotice(AdminUpdateNoticeRequest request,
                                  StreamObserver<AdminUpdateNoticeResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();

            Notice updated = noticeCommandService.update(
                    request.getId(),
                    request.getTitleMap(),
                    request.getContentMap(),
                    request.getPostedAt(),
                    request.getIsPinned(),
                    request.getIsVisible(),
                    request.getImageUrl()
            );

            responseObserver.onNext(AdminUpdateNoticeResponse.newBuilder()
                    .setNotice(toAdminNoticeProto(updated))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminDeleteNotice(AdminDeleteNoticeRequest request,
                                  StreamObserver<AdminDeleteNoticeResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();

            noticeCommandService.delete(request.getId());

            responseObserver.onNext(AdminDeleteNoticeResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    private static AdminNotice toAdminNoticeProto(Notice notice) {
        return AdminNotice.newBuilder()
                .setId(notice.getId())
                .putAllTitle(notice.getTitle())
                .putAllContent(notice.getContent())
                .setPostedAt(notice.getPostedAt() == null ? "" : notice.getPostedAt().toString())
                .setIsPinned(notice.isPinned())
                .setIsVisible(notice.isVisible())
                .setImageUrl(notice.getImageUrl() == null ? "" : notice.getImageUrl())
                .setCreatedAt(notice.getCreatedAt() == null ? "" : notice.getCreatedAt().toString())
                .setUpdatedAt(notice.getUpdatedAt() == null ? "" : notice.getUpdatedAt().toString())
                .build();
    }

    @Override
    public void adminCreateFoodTruck(AdminCreateFoodTruckRequest request,
                                     StreamObserver<AdminCreateFoodTruckResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            FoodTruckContent created = foodTruckCommandService.create(
                    request.getNameMap(), request.getShortDescriptionMap(),
                    request.getThumbnailImageUrl(), request.getIsVisible(), request.getDisplayOrder());
            responseObserver.onNext(AdminCreateFoodTruckResponse.newBuilder()
                    .setFoodTruck(toAdminFoodTruckProto(created)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminUpdateFoodTruck(AdminUpdateFoodTruckRequest request,
                                     StreamObserver<AdminUpdateFoodTruckResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            FoodTruckContent updated = foodTruckCommandService.update(
                    request.getId(), request.getNameMap(), request.getShortDescriptionMap(),
                    request.getThumbnailImageUrl(), request.getIsVisible(), request.getDisplayOrder());
            responseObserver.onNext(AdminUpdateFoodTruckResponse.newBuilder()
                    .setFoodTruck(toAdminFoodTruckProto(updated)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminDeleteFoodTruck(AdminDeleteFoodTruckRequest request,
                                     StreamObserver<AdminDeleteFoodTruckResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            foodTruckCommandService.delete(request.getId());
            responseObserver.onNext(AdminDeleteFoodTruckResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminCreatePub(AdminCreatePubRequest request,
                               StreamObserver<AdminCreatePubResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            PubContent created = pubCommandService.create(
                    request.getCollegeNameMap(), request.getIsNameConfirmed(),
                    request.getThumbnailImageUrl(), request.getIsVisible(), request.getDisplayOrder());
            responseObserver.onNext(AdminCreatePubResponse.newBuilder()
                    .setPub(toAdminPubProto(created)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminUpdatePub(AdminUpdatePubRequest request,
                               StreamObserver<AdminUpdatePubResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            PubContent updated = pubCommandService.update(
                    request.getId(), request.getCollegeNameMap(), request.getIsNameConfirmed(),
                    request.getThumbnailImageUrl(), request.getIsVisible(), request.getDisplayOrder());
            responseObserver.onNext(AdminUpdatePubResponse.newBuilder()
                    .setPub(toAdminPubProto(updated)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminDeletePub(AdminDeletePubRequest request,
                               StreamObserver<AdminDeletePubResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            pubCommandService.delete(request.getId());
            responseObserver.onNext(AdminDeletePubResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminCreateMdItem(AdminCreateMdItemRequest request,
                                  StreamObserver<AdminCreateMdItemResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            MdContent created = mdCommandService.createItem(
                    request.getNameMap(), request.getPrice(),
                    request.getProductDescriptionMap(), request.getDetailDescriptionMap(),
                    request.getThumbnailImageUrl(), request.getDetailImageUrl(),
                    request.getIsSoldOut(), request.getIsVisible(), request.getDisplayOrder());
            responseObserver.onNext(AdminCreateMdItemResponse.newBuilder()
                    .setMdItem(toAdminMdItemProto(created)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminUpdateMdItem(AdminUpdateMdItemRequest request,
                                  StreamObserver<AdminUpdateMdItemResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            MdContent updated = mdCommandService.updateItem(
                    request.getId(), request.getNameMap(), request.getPrice(),
                    request.getProductDescriptionMap(), request.getDetailDescriptionMap(),
                    request.getThumbnailImageUrl(), request.getDetailImageUrl(),
                    request.getIsSoldOut(), request.getIsVisible(), request.getDisplayOrder());
            responseObserver.onNext(AdminUpdateMdItemResponse.newBuilder()
                    .setMdItem(toAdminMdItemProto(updated)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminDeleteMdItem(AdminDeleteMdItemRequest request,
                                  StreamObserver<AdminDeleteMdItemResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            mdCommandService.deleteItem(request.getId());
            responseObserver.onNext(AdminDeleteMdItemResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminCreateMdOptionGroup(AdminCreateMdOptionGroupRequest request,
                                         StreamObserver<AdminCreateMdOptionGroupResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            MdOptionGroup created = mdCommandService.createOptionGroup(
                    request.getMdItemId(), request.getNameMap(), request.getDisplayOrder());
            responseObserver.onNext(AdminCreateMdOptionGroupResponse.newBuilder()
                    .setOptionGroup(toAdminMdOptionGroupProto(created)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminUpdateMdOptionGroup(AdminUpdateMdOptionGroupRequest request,
                                         StreamObserver<AdminUpdateMdOptionGroupResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            MdOptionGroup updated = mdCommandService.updateOptionGroup(
                    request.getId(), request.getNameMap(), request.getDisplayOrder());
            responseObserver.onNext(AdminUpdateMdOptionGroupResponse.newBuilder()
                    .setOptionGroup(toAdminMdOptionGroupProto(updated)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminDeleteMdOptionGroup(AdminDeleteMdOptionGroupRequest request,
                                         StreamObserver<AdminDeleteMdOptionGroupResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            mdCommandService.deleteOptionGroup(request.getId());
            responseObserver.onNext(AdminDeleteMdOptionGroupResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminCreateMdOptionValue(AdminCreateMdOptionValueRequest request,
                                         StreamObserver<AdminCreateMdOptionValueResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            MdOptionValue created = mdCommandService.createOptionValue(
                    request.getOptionGroupId(), request.getValueNameMap(),
                    request.getExtraPrice(), request.getIsSoldOut(), request.getDisplayOrder());
            responseObserver.onNext(AdminCreateMdOptionValueResponse.newBuilder()
                    .setOptionValue(toAdminMdOptionValueProto(created)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminUpdateMdOptionValue(AdminUpdateMdOptionValueRequest request,
                                         StreamObserver<AdminUpdateMdOptionValueResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            MdOptionValue updated = mdCommandService.updateOptionValue(
                    request.getId(), request.getValueNameMap(),
                    request.getExtraPrice(), request.getIsSoldOut(), request.getDisplayOrder());
            responseObserver.onNext(AdminUpdateMdOptionValueResponse.newBuilder()
                    .setOptionValue(toAdminMdOptionValueProto(updated)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void adminDeleteMdOptionValue(AdminDeleteMdOptionValueRequest request,
                                         StreamObserver<AdminDeleteMdOptionValueResponse> responseObserver) {
        try {
            GrpcIdentityInterceptor.requireAdmin();
            mdCommandService.deleteOptionValue(request.getId());
            responseObserver.onNext(AdminDeleteMdOptionValueResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    private static AdminFoodTruck toAdminFoodTruckProto(FoodTruckContent ft) {
        ContentItem ci = ft.getContentItem();
        return AdminFoodTruck.newBuilder()
                .setId(ft.getId())
                .putAllName(ft.getName())
                .putAllShortDescription(ft.getShortDescription())
                .setThumbnailImageUrl(ci.getThumbnailImageUrl() == null ? "" : ci.getThumbnailImageUrl())
                .setDisplayOrder(ci.getDisplayOrder())
                .setIsVisible(ci.isVisible())
                .setCreatedAt(ft.getCreatedAt() == null ? "" : ft.getCreatedAt().toString())
                .setUpdatedAt(ft.getUpdatedAt() == null ? "" : ft.getUpdatedAt().toString())
                .build();
    }

    private static AdminPub toAdminPubProto(PubContent pub) {
        ContentItem ci = pub.getContentItem();
        return AdminPub.newBuilder()
                .setId(pub.getId())
                .putAllCollegeName(pub.getCollegeName())
                .setIsNameConfirmed(pub.isNameConfirmed())
                .setThumbnailImageUrl(ci.getThumbnailImageUrl() == null ? "" : ci.getThumbnailImageUrl())
                .setDisplayOrder(ci.getDisplayOrder())
                .setIsVisible(ci.isVisible())
                .setCreatedAt(pub.getCreatedAt() == null ? "" : pub.getCreatedAt().toString())
                .setUpdatedAt(pub.getUpdatedAt() == null ? "" : pub.getUpdatedAt().toString())
                .build();
    }

    private static AdminMdItem toAdminMdItemProto(MdContent md) {
        ContentItem ci = md.getContentItem();
        return AdminMdItem.newBuilder()
                .setId(md.getId())
                .putAllName(md.getName())
                .setPrice(md.getPrice())
                .putAllProductDescription(md.getProductDescription())
                .putAllDetailDescription(md.getDetailDescription())
                .setThumbnailImageUrl(ci.getThumbnailImageUrl() == null ? "" : ci.getThumbnailImageUrl())
                .setDetailImageUrl(md.getDetailImageUrl() == null ? "" : md.getDetailImageUrl())
                .setIsSoldOut(md.isSoldOut())
                .setDisplayOrder(ci.getDisplayOrder())
                .setIsVisible(ci.isVisible())
                .setCreatedAt(md.getCreatedAt() == null ? "" : md.getCreatedAt().toString())
                .setUpdatedAt(md.getUpdatedAt() == null ? "" : md.getUpdatedAt().toString())
                .build();
    }

    private static AdminMdOptionGroup toAdminMdOptionGroupProto(MdOptionGroup group) {
        return AdminMdOptionGroup.newBuilder()
                .setId(group.getId())
                .setMdItemId(group.getMdContent().getId())
                .putAllName(group.getName())
                .setDisplayOrder(group.getDisplayOrder())
                .setCreatedAt(group.getCreatedAt() == null ? "" : group.getCreatedAt().toString())
                .setUpdatedAt(group.getUpdatedAt() == null ? "" : group.getUpdatedAt().toString())
                .build();
    }

    private static AdminMdOptionValue toAdminMdOptionValueProto(MdOptionValue value) {
        return AdminMdOptionValue.newBuilder()
                .setId(value.getId())
                .setOptionGroupId(value.getOptionGroup().getId())
                .putAllValueName(value.getValueName())
                .setExtraPrice(value.getExtraPrice())
                .setIsSoldOut(value.isSoldOut())
                .setDisplayOrder(value.getDisplayOrder())
                .setCreatedAt(value.getCreatedAt() == null ? "" : value.getCreatedAt().toString())
                .setUpdatedAt(value.getUpdatedAt() == null ? "" : value.getUpdatedAt().toString())
                .build();
    }
}