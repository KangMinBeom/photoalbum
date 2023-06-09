package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    int countByAlbum_AlbumId(Long AlbumId);

    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(Long AlbumId);

    List<Photo> findByFileNameContainingOrderByUploadedAtDesc(String keyword);

    List<Photo> findByFileNameContainingOrderByFileNameAsc(String keyword);

    List<Photo> findByAlbum_AlbumId(Long AlbumId);

    Optional<Photo> findByFileNameAndAlbum_AlbumId(String photoName, Long albumId);
}
