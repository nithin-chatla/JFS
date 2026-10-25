package com.hostel.complaints.service;

import com.hostel.complaints.dto.NoticeForm;
import com.hostel.complaints.model.Notice;
import com.hostel.complaints.model.User;
import com.hostel.complaints.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> getActiveNotices() {
        return noticeRepository.findByActiveTrueOrderByPublishedAtDesc();
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByPublishedAtDesc();
    }

    public Notice createNotice(NoticeForm form, User warden) {
        Notice notice = new Notice(
                form.getTitle(),
                form.getContent(),
                form.getCategory(),
                warden.getFullName() + " (Hostel Office)"
        );
        return noticeRepository.save(notice);
    }

    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    public void toggleNoticeStatus(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notice not found with ID: " + id));
        notice.setActive(!notice.isActive());
        noticeRepository.save(notice);
    }
}
