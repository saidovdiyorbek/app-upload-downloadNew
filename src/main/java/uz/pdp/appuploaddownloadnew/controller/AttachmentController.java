package uz.pdp.appuploaddownloadnew.controller;

import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pdp.appuploaddownloadnew.entity.Attachment;
import uz.pdp.appuploaddownloadnew.entity.AttachmentContent;
import uz.pdp.appuploaddownloadnew.repository.AttachmentContentRepository;
import uz.pdp.appuploaddownloadnew.repository.AttachmentRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {
    private static final String uploadDirectory = "yuklanganlar";
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    @PostMapping
    public String upload(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());

        if (file != null){
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();

            Attachment attachment = new Attachment();
            attachment.setFileOriginalName(originalFilename);
            attachment.setSize(size);
            attachment.setContenetType(contentType);
            Attachment savedAttachment = attachmentRepository.save(attachment);

            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setBytes(file.getBytes());
            attachmentContent.setAttachment(savedAttachment);
            attachmentContentRepository.save(attachmentContent);
            return "Attachment saved";
        }
        return "Something wrong";
    }

    @PostMapping("uploadToSystem")
    public String uploadToSystem(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());

        if (file != null){
            String originalFilename = file.getOriginalFilename();

            Attachment attachment = new Attachment();

            attachment.setFileOriginalName(originalFilename);
            attachment.setSize(file.getSize());
            attachment.setContenetType(file.getContentType());

            String[] split = originalFilename.split("\\.");

            String name = UUID.randomUUID().toString()+"."+split[split.length-1];
            attachment.setName(name);
            attachmentRepository.save(attachment);

            Path path = Paths.get(uploadDirectory+"/"+name);
            Files.copy(file.getInputStream(), path);
            return "File saved";
        }
        return "File not saved";
    }

    @GetMapping("/download/{attachmentId}")
    public void getFile(@PathVariable Integer attachmentId, HttpServletResponse response) throws IOException {
         Optional<Attachment> optionalAttachment = attachmentRepository.findById(attachmentId);
        if (optionalAttachment.isPresent()){
            Attachment attachment = optionalAttachment.get();
          Optional<AttachmentContent> optionalAttachmentContent = attachmentContentRepository.findByAttachmentId(attachmentId);
            if (optionalAttachmentContent.isPresent()){
                AttachmentContent attachmentContent = optionalAttachmentContent.get();

                response.setHeader("Content-Disposition", "attachment; filename=\""+attachment.getFileOriginalName()+"\"");
                response.setContentType(attachment.getContenetType());
                FileCopyUtils.copy(attachmentContent.getBytes(), response.getOutputStream());
            }
        }
    }

    @GetMapping("/downloadFromSystem/{id}")
    public void getFileFromSystem(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()){
            Attachment attachment = optionalAttachment.get();
            Optional<AttachmentContent> optionalAttachmentContent = attachmentContentRepository.findByAttachmentId(id);
            if (optionalAttachmentContent.isPresent()){
                AttachmentContent attachmentContent = optionalAttachmentContent.get();

                response.setHeader("Content:Disposition", "attachment; filename=\"" +attachment.getFileOriginalName()+"\"");
                response.setContentType(attachment.getContenetType());
                FileInputStream fileInputStream = new FileInputStream(uploadDirectory+"/"+attachment.getName());
                FileCopyUtils.copy(fileInputStream, response.getOutputStream());
            }
        }
    }
}
