package com.squarecross.photoalbum.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumServiceTest {


    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AlbumService albumService;

    @Test
    void getAlbum() {
        Album album = new Album();
        album.setAlbumName("hi");
        Album savedAlbum = albumRepository.save(album);

        Album resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
        assertEquals("hi",resAlbum.getAlbumName());
    }
}