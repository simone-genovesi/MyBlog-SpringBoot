package it.cgmconsulting.banner.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ImageService {

    protected boolean checkSize(MultipartFile file, long size){
        return (file.getSize() > size || file.isEmpty());
    }

    protected BufferedImage fromMultipartfileToBufferedImage(MultipartFile file){
        BufferedImage bf = null;
        try {
            bf = ImageIO.read(file.getInputStream());
            return bf;
        } catch(IOException e) {
            return null;
        }
    }

    protected boolean checkDimensions(int width, int height, MultipartFile file){
        BufferedImage bf = fromMultipartfileToBufferedImage(file);
        if (bf != null){
            return (bf.getHeight() == height && bf.getWidth() == width);
        }
        return false;
    }

    protected boolean checkExtensions(String[] extensions, MultipartFile file){
        String filename = file.getOriginalFilename();
        if(filename != null) {
            String ext = filename.substring(filename.lastIndexOf(".") + 1);
            return Arrays.stream(extensions).anyMatch(ext::equalsIgnoreCase);
        } else
            return false;
    }

    protected boolean uploadImage(MultipartFile file, String campaignId, String path) {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = campaignId + "." + ext;

        Path p = Paths.get(path+filename);
        try{
            Files.write(p, file.getBytes());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
