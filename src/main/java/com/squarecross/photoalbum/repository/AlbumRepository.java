package com.squarecross.photoalbum.repository;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>{
    List<Album> findByAlbumNameContainingOrderByCreatedAtAsc(String keyword);
    List<Album> findByAlbumNameContainingOrderByCreatedAtDesc(String keyword);

    List<Album> findByAlbumNameContainingOrderByAlbumNameAsc(String keyword);

    List<Album> findByAlbumNameContainingOrderByAlbumNameDesc(String keyword);

}
