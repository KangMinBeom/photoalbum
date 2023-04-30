package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.service.AlbumService;
import com.squarecross.photoalbum.service.Constants;
import com.squarecross.photoalbum.service.PhotoService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("albums/{albumId}/photos")
public class PhotoController {
    @Autowired
    PhotoService photoService;

    @RequestMapping(value="/{photoId}", method = RequestMethod.GET)
    public ResponseEntity<PhotoDto> getPhotoInfo(@PathVariable("photoId") final long photoId){
        PhotoDto photoDto = photoService.getPhoto(photoId);
        return new ResponseEntity<>(photoDto, HttpStatus.OK);
    }

    @RequestMapping(value="", method = RequestMethod.POST)
    public ResponseEntity<List<PhotoDto>> uploadPhotos(@PathVariable("albumId") final Long albumId,
                                                       @RequestParam(value = "photos",required = false) MultipartFile[] files)  {
        List<PhotoDto> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            PhotoDto photoDto = photoService.savePhoto(file, albumId);
            photos.add(photoDto);
        }
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }

    @RequestMapping(value="/download", method= RequestMethod.GET)
    public void downloadPhotos(@RequestParam("photoIds")Long[] photoIds, HttpServletResponse response){
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;

        try{
            if(photoIds.length == 1){
                File file = photoService.getImageFile(photoIds[0]);
                OutputStream outputStream = response.getOutputStream();
                IOUtils.copy(new FileInputStream(file), outputStream);
                outputStream.close();
            }else{
//                String TEMP_ZIP_PATH = "D:/test_folder";
                OutputStream outputStream = response.getOutputStream();
//                File zipFile = new File(TEMP_ZIP_PATH);
                byte[] buf = new byte[4096];
                try(ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("result.zip"))){
                    List<File> files = photoService.getImageFilelist(photoIds);

                    for(File file : files){
                        try(FileInputStream fileInputStream = new FileInputStream(file)){
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zipOutputStream.putNextEntry(zipEntry);

                            int len;
                            while((len = fileInputStream.read(buf)) > 0){
                                zipOutputStream.write(buf, 0, len);
                            }

                            zipOutputStream.closeEntry();
                        }
                    }
                }
                IOUtils.copy(new FileInputStream("result.zip"), outputStream);
                outputStream.close();

            }
        } catch(FileNotFoundException e){
            throw new RuntimeException("Error");
        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }

    @RequestMapping(value="", method = RequestMethod.GET)
    public ResponseEntity<List<PhotoDto>> getPhotoList(@PathVariable("albumId") final Long albumId,
                @RequestParam(value="keyword", required=false, defaultValue="") final String keyword,
                 @RequestParam(value="sort", required=false, defaultValue = "byDate") final String sort){
        List<PhotoDto> photoDtos = photoService.getPhotoList(albumId ,keyword, sort);
        return new ResponseEntity<>(photoDtos,HttpStatus.OK);
    }

    @RequestMapping(value="", method = RequestMethod.DELETE)
    public ResponseEntity<List<PhotoDto>> deletePhoto(@PathVariable("albumId") final Long albumId,
                                            @RequestBody PhotoDto photodto) throws IOException {
       List<PhotoDto> photodtos= photoService.deletePhoto(albumId,photodto);
        return new ResponseEntity<>(photodtos,HttpStatus.OK);

    }



}
