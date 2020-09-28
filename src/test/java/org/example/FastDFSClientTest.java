package org.example;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.FileInfo;
import org.example.client.FastDFSClient;
import org.junit.Test;

import java.io.*;

public class FastDFSClientTest {

    // 文件上传
    @Test
    public void testUploadFile() {
        String[] fileids = FastDFSClient.uploadFile(new File("D:/china.jpg"), "china.jpg");
        for (String fileid : fileids) {
            System.out.println("fileid = " + fileid);
        }
    }

    // 获取文件详情
    @Test
    public void testGetFileInfo() {
        FileInfo fileInfo = FastDFSClient.getFileInfo("group1", "M00/00/00/wKgKZl9xMdiAcOLdAADhaCZ_RF0096.jpg");
        System.out.println("fileInfo = " + fileInfo);
    }

    // 获取文件数据
    @Test
    public void testGetMetaData() {
        NameValuePair[] metaDatas = FastDFSClient.getMetaData("group1", "M00/00/00/wKgKZl9xMdiAcOLdAADhaCZ_RF0096.jpg");
        for (NameValuePair metaData : metaDatas) {
            System.out.println(metaData.getName() + "---" + metaData.getValue());
        }
    }

    // 文件下载
    @Test
    public void testDownloadFile() {
        InputStream is = FastDFSClient.downloadFile("group1", "M00/00/00/wKgKZl9xMdiAcOLdAADhaCZ_RF0096.jpg");
        try (FileOutputStream fos = new FileOutputStream("D:/wKgKZl9xMdiAcOLdAADhaCZ_RF0096.jpg")) {
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
                fos.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 文件删除
    @Test
    public void testDeleteFile() {
        int result = FastDFSClient.deleteFile("group1", "M00/00/00/wKgKZl9xMdiAcOLdAADhaCZ_RF0096.jpg");
        System.out.println("result = " + result);
    }

    // 文件替换
    @Test
    public void testModifyFile() {
        String[] fileids = FastDFSClient.modifyFile("group1", "M00/00/00/wKgKZl9xOS2ASdu8AADhaCZ_RF0898.jpg",
                new File("D:/mhw.jpg"), "mhw.jpg");
        for (String fileid : fileids) {
            System.out.println("fileid = " + fileid);
        }
    }

}
