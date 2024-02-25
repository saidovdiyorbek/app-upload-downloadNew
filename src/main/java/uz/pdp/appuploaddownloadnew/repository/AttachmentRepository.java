package uz.pdp.appuploaddownloadnew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appuploaddownloadnew.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}
