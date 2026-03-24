package com.playtab.contentservice.grpc;

import com.playtab.contentservice.entity.FoodTruckContent; // 푸드트럭 엔티티 사용
import com.playtab.contentservice.entity.PubContent; // 주점 엔티티 사용
import com.playtab.contentservice.entity.MdContent; // MD 엔티티 사용
import com.playtab.contentservice.entity.Notice; // 공지 엔티티 사용
import com.playtab.contentservice.entity.MdOptionValue; // MD 옵션 값 엔티티 사용
import com.playtab.contentservice.entity.MdOptionGroup; // MD 옵션 그룹 엔티티 사용
import com.playtab.contentservice.grpc.proto.v1.GetNoticesRequest; // proto request
import com.playtab.contentservice.grpc.proto.v1.GetNoticesResponse; // proto response
import com.playtab.contentservice.grpc.proto.v1.NoticeSummary; // proto 응답 item
import com.playtab.contentservice.grpc.proto.v1.GetNoticeDetailRequest; // proto request
import com.playtab.contentservice.grpc.proto.v1.GetNoticeDetailResponse; // proto response
import com.playtab.contentservice.grpc.proto.v1.NoticeDetail; // proto 상세 응답 item
import com.playtab.contentservice.grpc.proto.v1.GetMdItemsRequest; // proto request
import com.playtab.contentservice.grpc.proto.v1.GetMdItemsResponse; // proto response
import com.playtab.contentservice.grpc.proto.v1.MdItemSummary; // proto 응답 item
import com.playtab.contentservice.grpc.proto.v1.GetMdItemDetailRequest; // proto request
import com.playtab.contentservice.grpc.proto.v1.GetMdItemDetailResponse; // proto response
import com.playtab.contentservice.grpc.proto.v1.MdItemDetail; // proto 상세 응답 item
import com.playtab.contentservice.grpc.proto.v1.PageInfo; // 페이지 정보 응답
import com.playtab.contentservice.grpc.proto.v1.GetPubsRequest; // proto request
import com.playtab.contentservice.grpc.proto.v1.GetPubsResponse; // proto response
import com.playtab.contentservice.grpc.proto.v1.PubItem; // proto 응답 Item
import com.playtab.contentservice.grpc.proto.v1.GetFoodTrucksRequest; // proto request
import com.playtab.contentservice.grpc.proto.v1.GetFoodTrucksResponse; // proto response
import com.playtab.contentservice.grpc.proto.v1.FoodTruckItem; // proto 응답 item
import com.playtab.contentservice.grpc.proto.v1.ContentServiceGrpc;
import com.playtab.contentservice.service.ContentQueryService;
import io.grpc.stub.StreamObserver;
import java.util.List; // 목록 처리
import java.util.Optional; // Optional 처리
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page; // 페이지 결과 사용
import org.springframework.data.domain.PageRequest; // pageable 생성

@GrpcService // gRPC 서비스로 등록
@RequiredArgsConstructor // final 필드 생성자 자동 주입
public class ContentGrpcService extends ContentServiceGrpc.ContentServiceImplBase {

    // 조회 로직을 담은 service 주입
    private final ContentQueryService contentQueryService;

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
        // proto pageRequest -> Spring PageRequest 변환
        int page = request.getPageRequest().getPage();
        int size = request.getPageRequest().getSize();

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
        // proto pageRequest -> Spring PageRequest 변환
        int page = request.getPageRequest().getPage();
        int size = request.getPageRequest().getSize();

        PageRequest pageable = PageRequest.of(page, size);

        // 서비스 계층에서 공지 목록 조회
        Page<Notice> noticePage = contentQueryService.getNotices(request.getOnlyVisible(), pageable);

        // proto response builder 생성
        GetNoticesResponse.Builder responseBuilder = GetNoticesResponse.newBuilder();

        for (Notice notice : noticePage.getContent()) {
            // locale에 맞는 title 추출, 없으면 빈 문자열
            String title = notice.getTitle().getOrDefault(request.getLocale(), "");

            // entity -> proto item 변환
            NoticeSummary item = NoticeSummary.newBuilder()
                    .setId(notice.getId())
                    .setTitle(title)
                    .setPostedAt(notice.getPostedAt() == null ? "" : notice.getPostedAt().toString())
                    .setIsPinned(notice.isPinned())
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
                .build();

        // 최종 응답 생성
        GetNoticeDetailResponse response = GetNoticeDetailResponse.newBuilder()
                .setNotice(detail)
                .build();

        // 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}