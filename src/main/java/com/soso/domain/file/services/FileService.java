package com.soso.domain.file.services;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.soso.domain.file.dao.FileDAO;
import com.soso.domain.file.dto.FileSaveDto;


@Service
public class FileService {

    @Autowired
    private FileDAO fileDao; 

    @Autowired
    private Storage storage; 

    
    private final String bucketName = "study_jcr";

    
    public boolean deleteFromGcs(String sysName) {
        if (sysName == null || sysName.isEmpty()) return false;
        try {
            
            return storage.delete(bucketName, sysName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public String updateFile(MultipartFile file, Integer userSeq, String category, String oldSysName) throws IOException {
        
        if (oldSysName != null && !oldSysName.isEmpty()) {
            deleteFromGcs(oldSysName);
            fileDao.deleteFile(oldSysName);
        }

        
        return uploadToGcsAndGetUrl(file, userSeq, category);
    }

    
    public String uploadToGcsAndGetUrl(MultipartFile file, Integer userSeq, String category) throws IOException {
        if (file == null || file.isEmpty()) {
            return "";
        }

        
        String oriname = file.getOriginalFilename();
        String ext = "";
        if (oriname != null && oriname.contains(".")) {
            ext = oriname.substring(oriname.lastIndexOf("."));
        }

        
        
        String sysName = category.toLowerCase() + "/" + UUID.randomUUID().toString() + ext;

        
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, sysName)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getBytes());

        
        FileSaveDto fileSaveDto = new FileSaveDto();
        fileSaveDto.setUserSeq(userSeq);
        fileSaveDto.setBoardSeq(null); 
        fileSaveDto.setFileCategory(category);
        fileSaveDto.setOriname(oriname);
        fileSaveDto.setSysname(sysName);
        fileSaveDto.setFileSize(file.getSize());
        fileSaveDto.setFileType(file.getContentType());

        fileDao.insertFile(fileSaveDto);

        
        return "https://storage.googleapis.com/" + bucketName + "/" + sysName;
    }
    
    
    public String uploadToGcsAndGetUrlWithBoardSeq(MultipartFile file, Integer userSeq, String category, Integer boardSeq) throws IOException {
        if (file == null || file.isEmpty()) {
            return "";
        }

        
        String oriname = file.getOriginalFilename();
        String ext = "";
        if (oriname != null && oriname.contains(".")) {
            ext = oriname.substring(oriname.lastIndexOf("."));
        }

        
        String sysName = category.toLowerCase() + "/" + UUID.randomUUID().toString() + ext;

        
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, sysName)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getBytes());

        
        FileSaveDto fileSaveDto = new FileSaveDto();
        fileSaveDto.setUserSeq(userSeq);
        fileSaveDto.setBoardSeq(boardSeq); 
        fileSaveDto.setFileCategory(category);
        fileSaveDto.setOriname(oriname);
        fileSaveDto.setSysname(sysName);
        fileSaveDto.setFileSize(file.getSize());
        fileSaveDto.setFileType(file.getContentType());

        fileDao.insertFile(fileSaveDto);

        
        return "https://storage.googleapis.com/" + bucketName + "/" + sysName;
    }

    
    public String updateFileWithBoardSeq(MultipartFile file, Integer userSeq, String category, String oldSysName, Integer boardSeq) throws IOException {
        
        if (oldSysName != null && !oldSysName.isEmpty()) {
            deleteFromGcs(oldSysName);
            fileDao.deleteFile(oldSysName);
        }

        
        return uploadToGcsAndGetUrlWithBoardSeq(file, userSeq, category, boardSeq);
    }
}
