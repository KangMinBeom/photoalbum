package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.mapper.PhotoMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import javax.swing.text.html.Option;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private AlbumRepository albumRepository;

    private final String original_path = Constants.PATH_PREFIX + "/photos/original";
    private final String thumb_path = Constants.PATH_PREFIX + "/photos/thumb";

    public PhotoDto getPhoto(Long photoId){
        Optional<Photo> res = photoRepository.findById(photoId);
        if(res.isPresent()){
            PhotoDto photoDto = PhotoMapper.convertToDto(res.get());
            return photoDto;

        }else{
            throw new EntityNotFoundException(String.format("사진 정보로 %d가 조회되지 않습니다.", photoId));
        }
    }

    private String getNextFileName(String fileName, Long albumId){
        String fileNameNoExt = StringUtils.stripFilenameExtension(fileName);
        String ext = StringUtils.getFilenameExtension(fileName);
        if(ext.equals("png") && ext.equals("jpeg") && ext.equals("bmp") && ext.equals("gif")){
            Optional<Photo> res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);

            int count = 2;
            while(res.isPresent()){
                fileName = String.format("%s (%d).%s", fileNameNoExt, count, ext);
                res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);
                count++;
            }
        }else{
            throw new RuntimeException("옳바르지 않은 확장자입니다.");
        }


        return fileName;
    }
    public PhotoDto savePhoto(MultipartFile file, Long albumId){
        Optional<Album> res = albumRepository.findById(albumId);
        if(res.isEmpty()){
            throw new EntityNotFoundException("앨범이 존재하지 않습니다");
        }
        String fileName = file.getOriginalFilename();
        int fileSize = (int)file.getSize();
        fileName = getNextFileName(fileName, albumId);
        saveFile(file, albumId, fileName);

        Photo photo = new Photo();
        photo.setOriginalUrl("/photos/original/" + albumId + "/" + fileName);
        photo.setThumbUrl("/photos/thumb/" + albumId + "/" + fileName);
        photo.setFileName(fileName);
        photo.setFileSize(fileSize);
        photo.setAlbum(res.get());
        Photo createdPhoto = photoRepository.save(photo);
        return PhotoMapper.convertToDto(createdPhoto);
    }

    private void saveFile(MultipartFile file, Long AlbumId, String fileName) {
        try {
            String filePath = AlbumId + "/" + fileName;
            Files.copy(file.getInputStream(), Paths.get(original_path + "/" + filePath));

            BufferedImage thumbImg = Scalr.resize(ImageIO.read(file.getInputStream()), Constants.THUMB_SIZE, Constants.THUMB_SIZE);
            File thumbFile = new File(thumb_path + "/" + filePath);
            String ext = StringUtils.getFilenameExtension(fileName);
            if (ext == null) {
                throw new IllegalArgumentException("No Extention");
            }
            ImageIO.write(thumbImg, ext, thumbFile);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public File getImageFile(Long photoId){
        Optional<Photo> res = photoRepository.findById(photoId);
        if(res.isEmpty()){
            throw new EntityNotFoundException(String.format("사진을 ID %d를 찾을 수 없습니다", photoId));
        }
        return new File(Constants.PATH_PREFIX + res.get().getOriginalUrl());
    }

    public List<File> getImageFilelist(Long[] photoIds){
        List<File> files = new ArrayList<>();

        for(Long photoId : photoIds){
            Optional<Photo> res = photoRepository.findById(photoId);

            if(res.isEmpty()){
                throw new EntityNotFoundException(String.format("사진을 ID %d를 찾을 수 없습니다", photoId));
            }

            files.add(new File(Constants.PATH_PREFIX + res.get().getOriginalUrl()));
        }
        return files;
    }

    public List<PhotoDto> getPhotoList(Long albumId, String keyword, String sort){
        Optional<Album> res = albumRepository.findById(albumId);
        if(res.isEmpty()){
            throw new NoSuchElementException(String.format("Album ID '%d'가 존재하지 않습니다.",albumId));
        }else{
            List<Photo> photos = photoRepository.findByAlbum_AlbumId(res.get().getAlbumId());
            if(Objects.equals(sort,"byName")){
                photos = photoRepository.findByFileNameContainingOrderByFileNameAsc(keyword);
            }else if(Objects.equals(sort,"byDate")) {
                photos = photoRepository.findByFileNameContainingOrderByUploadedAtDesc(keyword);
            }
            List<PhotoDto>photoDtos = PhotoMapper.convertToDtoList(photos);
            return photoDtos;
        }
    }
}
