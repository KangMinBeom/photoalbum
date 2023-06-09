package com.squarecross.photoalbum.mapper;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.dto.PhotoDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhotoMapper {

    public static PhotoDto convertToDto(Photo photo){
        PhotoDto photoDto = new PhotoDto();
        photoDto.setPhotoId(photo.getPhotoId());
        photoDto.setFileName(photo.getFileName());
        photoDto.setOriginalUrl(photo.getOriginalUrl());
        photoDto.setThumbUrl(photo.getThumbUrl());
        photoDto.setUploadedAt(photo.getUploadedAt());
        photoDto.setFileSize(photo.getFileSize());
        photoDto.setAlbumId(photo.getAlbum().getAlbumId());
        return photoDto;
    }

    public static PhotoDto convertToDto1(Photo photo){
        PhotoDto photoDto = new PhotoDto();
        photoDto.setPhotoId(photo.getPhotoId());
        photoDto.setFileName(photo.getFileName());
        photoDto.setUploadedAt(photo.getUploadedAt());
        photoDto.setFileSize(photo.getFileSize());
        return photoDto;
    }

    public static Photo convertToModel (PhotoDto PhotoDto) {
        Photo photo = new Photo();
        photo.setPhotoId(PhotoDto.getAlbumId());
        photo.setFileName(PhotoDto.getFileName());
        photo.setThumbUrl(PhotoDto.getThumbUrl());
        photo.setUploadedAt(PhotoDto.getUploadedAt());
        return photo;
    }
    public static List<PhotoDto> convertToDtoList(List<Photo> photos) {
        return photos.stream().map(PhotoMapper::convertToDto).collect(Collectors.toList());
    }
    public static List<PhotoDto> convertToDtoList1(List<Photo> photos) {
        return photos.stream().map(PhotoMapper::convertToDto1).collect(Collectors.toList());
    }
}
