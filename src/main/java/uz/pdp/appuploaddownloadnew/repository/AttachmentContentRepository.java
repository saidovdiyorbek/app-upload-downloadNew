package uz.pdp.appuploaddownloadnew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.pdp.appuploaddownloadnew.entity.Attachment;
import uz.pdp.appuploaddownloadnew.entity.AttachmentContent;

import java.util.Optional;
@Repository
public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, Integer> {
    Optional<AttachmentContent> findByAttachmentId(Integer attachmentId);
}
