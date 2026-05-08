package com.playtab.contentservice.service;

import com.playtab.contentservice.entity.Notice;
import com.playtab.contentservice.exception.ContentServiceException;
import com.playtab.contentservice.exception.ErrorCode;
import com.playtab.contentservice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeCommandService {

    private final NoticeRepository noticeRepository;

    public Notice create(Map<String, String> title, Map<String, String> content,
                         String postedAtStr, boolean isPinned, boolean isVisible,
                         String imageUrl) {
        validateTitle(title);
        validateContent(content);
        LocalDateTime postedAt = parseDateTime("postedAt", postedAtStr);

        Notice notice = Notice.create(title, content, postedAt, isPinned, isVisible,
                emptyToNull(imageUrl));

        return noticeRepository.save(notice);
    }

    public Notice update(long id, Map<String, String> title, Map<String, String> content,
                         String postedAtStr, boolean isPinned, boolean isVisible,
                         String imageUrl) {
        validateTitle(title);
        validateContent(content);
        LocalDateTime postedAt = parseDateTime("postedAt", postedAtStr);

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.NOTICE_NOT_FOUND));

        notice.update(title, content, postedAt, isPinned, isVisible, emptyToNull(imageUrl));
        return noticeRepository.save(notice);
    }

    public void delete(long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.NOTICE_NOT_FOUND));
        noticeRepository.delete(notice);
    }

    // ─── private helpers ───

    private void validateTitle(Map<String, String> title) {
        if (title == null || title.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
    }

    private void validateContent(Map<String, String> content) {
        if (content == null || content.isEmpty()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
    }

    private LocalDateTime parseDateTime(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new ContentServiceException(ErrorCode.INVALID_ARGUMENT);
        }
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
