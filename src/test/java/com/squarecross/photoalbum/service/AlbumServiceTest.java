package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import com.squarecross.photoalbum.service.Constants;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumServiceTest {


    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AlbumService albumService;

//    @Test
//    void getAlbum() {
//        Album album = new Album();
//        album.setAlbumName("테스트");
//        Album savedAlbum = albumRepository.save(album);
//
//        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
//        assertEquals("테스트",resAlbum.getAlbumName());
//    }
//
//    @Test
//    void testPhotoCount(){
//        Album album = new Album();
//        album.setAlbumName("테스트");
//        Album savedAlbum = albumRepository.save(album);
//
//        Photo photo = new Photo();
//        photo.setFile_name("앨범1");
//        photo.setAlbum(savedAlbum);
//        photoRepository.save(photo);
//
//        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
//        assertEquals("테스트",resAlbum.getAlbumName());
//
//    }

    @Test
    void testAlbumCreate() throws IOException {
        AlbumDto albumDto = new AlbumDto();

        albumDto.setAlbumName("앨범테스트");

        AlbumDto newAlbum = albumService.createAlbum(albumDto);

        assertEquals("앨범테스트",albumDto.getAlbumName());

        Files.deleteIfExists(Paths.get(Constants.PATH_PREFIX +"/photos/original/"+ albumDto.getAlbumName()));
        Files.deleteIfExists(Paths.get(Constants.PATH_PREFIX +"/photos/thumb/"+ albumDto.getAlbumName()));
    }
}