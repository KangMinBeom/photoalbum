package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private PhotoService photoService;

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

//    @Test
//    void testAlbumCreate() throws IOException {
//        AlbumDto albumDto = new AlbumDto();
//
//        albumDto.setAlbumName("앨범테스트");
//
//        AlbumDto newAlbum = albumService.createAlbum(albumDto);
//
//        assertEquals("앨범테스트",albumDto.getAlbumName());
//
//        Files.deleteIfExists(Paths.get(Constants.PATH_PREFIX +"/photos/original/"+ albumDto.getAlbumName()));
//        Files.deleteIfExists(Paths.get(Constants.PATH_PREFIX +"/photos/thumb/"+ albumDto.getAlbumName()));
//    }

    @Test
    void testAlbumRepository() throws InterruptedException {
        Album album1 = new Album();
        Album album2 = new Album();
        album1.setAlbumName("aaaa");
        album2.setAlbumName("aaab");

        albumRepository.save(album1);
        TimeUnit.SECONDS.sleep(1); //시간차를 벌리기위해 두번째 앨범 생성 1초 딜레이
        albumRepository.save(album2);

        //최신순 정렬, 두번째로 생성한 앨범이 먼저 나와야합니다
        List<Album> resDate = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc("aaa");
        assertEquals("aaab", resDate.get(0).getAlbumName()); // 0번째 Index가 두번째 앨범명 aaab 인지 체크
        assertEquals("aaaa", resDate.get(1).getAlbumName()); // 1번째 Index가 첫번째 앨범명 aaaa 인지 체크
        assertEquals(2, resDate.size()); // aaa 이름을 가진 다른 앨범이 없다는 가정하에, 검색 키워드에 해당하는 앨범 필터링 체크

        //앨범명 정렬, aaaa -> aaab 기준으로 나와야합니다
        List<Album> resName = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc("aaa");
        assertEquals("aaaa", resName.get(0).getAlbumName()); // 0번째 Index가 두번째 앨범명 aaaa 인지 체크
        assertEquals("aaab", resName.get(1).getAlbumName()); // 1번째 Index가 두번째 앨범명 aaab 인지 체크
        assertEquals(2, resName.size()); // aaa 이름을 가진 다른 앨범이 없다는 가정하에, 검색 키워드에 해당하는 앨범 필터링 체크
    }

    @Test
    void testChangeAlbumName() throws IOException{
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("변경전");
        AlbumDto res = albumService.createAlbum(albumDto);

        Long albumId =res.getAlbumId();

        AlbumDto updateDto = new AlbumDto();
        updateDto.setAlbumName("변경후");
        albumService.changeName(albumId,updateDto);

        AlbumDto updatedDto = albumService.getAlbum(albumId);

        assertEquals("변경후",updatedDto.getAlbumName());
    }

    @Test
    void testDeleteAlbum() throws IOException{
        Album album = new Album();
        album.setAlbumName("테스트 앨범");
        Album savedAlbum = albumRepository.save(album);
        Long album1 = savedAlbum.getAlbumId();

        Photo photo = new Photo();
        photo.setFileName("테스트 사진");
        Photo savedPhoto = photoRepository.save(photo);
        Long album2 = savedPhoto.getPhotoId();


        albumService.deleteAlbum(album1);

        assertEquals(null,album1);
        assertEquals(null,album2);
    }

    @Test
    void testPhotoInfo() throws IOException{
        Photo photo = new Photo();
        photo.setFileName("테스트 사진");
        Photo savedPhoto = photoRepository.save(photo);
        Long album2 = savedPhoto.getPhotoId();

        PhotoDto photoDto = photoService.getPhoto(album2);
    }

    @Test
    void testPhotoList() throws IOException{
        Album album = new Album();
        album.setAlbumName("테스트 앨범");
        Album savedAlbum = albumRepository.save(album);
        Long album1 = savedAlbum.getAlbumId();

        Photo photo = new Photo();
        photo.setFileName("테스트 사진");
        photo.setAlbum(savedAlbum);
        Photo savedPhoto = photoRepository.save(photo);

        Photo photo1 = new Photo();
        photo1.setFileName("테스트 사진1");
        photo1.setAlbum(savedAlbum);
        Photo savedPhoto1 = photoRepository.save(photo1);

        List<Photo> photos = photoRepository.findByAlbum_AlbumId(album1);
    }

}