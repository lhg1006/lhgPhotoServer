package com.example.lhgphotoserver.api.photo.act;


import com.example.lhgphotoserver.api.photo.vo.ImagePrevVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photo")
public class PhotoAction {

    @PostMapping(value = "/upload/file")
    public Map<String, Object> AxiosFileTest(HttpServletRequest request,
                                             @RequestParam(value = "file", required = false) MultipartFile files) throws SQLException {
        Map<String, Object> resultMap = new HashMap<>();

        String filepath = "/lhgPhotos";
        String calcPath = calcPath(filepath);
        String savePath = filepath + calcPath;
        String pathDate = calcPath.replaceAll("/", "-");
        String pathDate2 = pathDate.substring(1, pathDate.length() -1);

        List<Object> list = new ArrayList<>();

        String originFileName = files.getOriginalFilename();
        long fileSize = files.getSize();
        System.out.println("originFileName : " + originFileName);
        System.out.println("fileSize : " + fileSize);
        UUID randomUUID = UUID.randomUUID();
        String safeFile = randomUUID + "_" + originFileName;

        try {
            File f1 = new File(savePath + safeFile);
            files.transferTo(f1);

            ImagePrevVO imagePrevVO = new ImagePrevVO();
            imagePrevVO.setImagePath("/api/photo/imagePath/" + pathDate2 + "/" + safeFile);
            imagePrevVO.setImageName(safeFile);
            imagePrevVO.setImageUrl("/api/photo/imagePath" + "/" + pathDate + "/" + safeFile);
            list.add(imagePrevVO);

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        resultMap.put("imageData", list);

        return resultMap;
    }

    private static String calcPath(String uploadPath) {
        Calendar cal = Calendar.getInstance();
        String yearPath = File.separator + cal.get(Calendar.YEAR);
        String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1);
        String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
        makeDir(uploadPath, yearPath, monthPath, datePath);
        return datePath.replaceAll("\\\\", "/") + "/";
    }

    private static void makeDir(String uploadPath, String... paths) {
        if (new File(uploadPath + paths[paths.length - 1]).exists())
            return;

        for (String path : paths) {
            File dirPath = new File(uploadPath + path);

            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
        }
    }

    @GetMapping(value = "/imagePath/{pathDate}/{fileName}",
            produces = {"image/gif", "image/jpeg", "image/png",})
    public byte[] viewImg(@PathVariable String pathDate, @PathVariable String fileName) {
        String replacePathDate = pathDate.replaceAll("-", "/") + "/";

        byte[] data = new byte[0];

        String filePath = "/lhgPhotos/" + replacePathDate + fileName;

        try {
            InputStream inputStream = new FileInputStream(filePath);

            long fileSize = new File(filePath).length();

            data = new byte[(int) fileSize];

            inputStream.read(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
