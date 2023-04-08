package com.squarecross.photoalbum.mapper;

import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;

public class PhotoMapper {

    public static PhotoDto convertToDto(Photo photo){
        PhotoDto photoDto = new PhotoDto();
        photoDto.setPhotoId(photo.getPhotoId());
        photoDto.setFileName(photo.getFileName());
        photoDto.setOriginalUrl(photo.getOriginalUrl());
        photoDto.setUploadedAt(photo.getUploadedAt());
        photoDto.setFileSize(photo.getFileSize());
        return photoDto;


    }
}
