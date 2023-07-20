package com.zyq.bloggy.util;

import com.zyq.bloggy.exception.ServiceException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class FileUtil {
    private static final String FILE_DIRECTOR = "D:/project/Bloggy/static/";
    private static final String IMAGE_DIRECTORY = FILE_DIRECTOR + "/image/";
    private static final String VIDEO_DIRECTORY = FILE_DIRECTOR + "/video/";
    private static final String IMAGE_LOCATION = "http://localhost:8080/image/";
    private static final String VIDEO_LOCATION = "http://localhost:8080/video/";

    private static String[] IMAGE_TYPE = new String[]{
            ".jpg", ".png", ".JPEG", ".jfif", ".webp"
    };
    private static String[] VIDEO_TYPE = new String[]{
            "mp4"
    };

    public static String save(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String type = originalFileName.substring(originalFileName.lastIndexOf("."));
        String dir;
        String secondDir = getDir();
        File file;
        String fileName = DigestUtils.md5Hex(originalFileName) + System.currentTimeMillis() + type;
        String path;
        Boolean flag = false;
        for (int i = 0; i < IMAGE_TYPE.length; i++) {
            if (type.equals(IMAGE_TYPE[i])) {
                flag = true;
                break;
            }
        }
        if (flag) {
            dir = IMAGE_DIRECTORY + secondDir;
            file = new File(dir, fileName);
            path = IMAGE_LOCATION + secondDir + "/" + fileName;
        } else {
            for (int i = 0; i < VIDEO_TYPE.length; i++) {
                if (type.equals(VIDEO_TYPE[i])) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                dir = VIDEO_DIRECTORY + secondDir;
                file = new File(dir, fileName);
                path = VIDEO_LOCATION + secondDir + "/" + fileName;
            } else {
                throw new ServiceException("文件上传失败");
            }
        }
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        try {
            file.createNewFile();
            multipartFile.transferTo(file);
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDefaultAvatar() {
        return "localhost:8080/default/Logo.jpg";
    }

    public static String getDefaultCover() {
        return "localhost:8080/default/cover.jpg";
    }

    private static String getDir() {
        return new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
    }

    public static boolean isBlank(MultipartFile multipartFile) {
        return Objects.isNull(multipartFile) || multipartFile.isEmpty();
    }
}
