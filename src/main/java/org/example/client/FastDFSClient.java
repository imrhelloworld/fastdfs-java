package org.example.client;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.*;

/**
 * FastDFS 分布式文件系统 Java 客户端工具类
 * 具体功能：文件上传、下载、替换、删除、查询文件元数据、查看文件详情
 */
public class FastDFSClient {

    // 获取配置文件地址
    private static final String CONF_FILENAME = Thread.currentThread()
            .getContextClassLoader().getResource("").getPath() + "fdfs_client.conf";
    // Storage 存储服务器客户端
    private static StorageClient storageClient = null;

    static {
        try {
            // 加载配置文件
            ClientGlobal.init(CONF_FILENAME);
            // 初始化 Tracker 客户端
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            // 初始化 Tracker 服务端
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            // 初始化 Storage 服务端
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            // 初始化 Storage 客户端
            storageClient = new StorageClient(trackerServer, storageServer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     *
     * @param inputStream 上传的文件的字节输入流
     * @param fileName    上传的文件的原始名
     * @return
     */
    public static String[] uploadFile(InputStream inputStream, String fileName) {
        try {
            // 准备字节数组
            byte[] fileBuff = null;
            // 文件元数据
            NameValuePair[] metaList = null;
            if (inputStream != null) {
                // 查看文件的长度
                int len = inputStream.available();
                // 初始化元数据数组
                metaList = new NameValuePair[2];
                // 第一组元数据，文件的原始名称
                metaList[0] = new NameValuePair("file_name", fileName);
                // 第二组元数据，文件的长度
                metaList[1] = new NameValuePair("file_length", String.valueOf(len));
                // 创建对应长度的字节数组
                fileBuff = new byte[len];
                // 将输入流中的字节内容，读到字节数组中
                inputStream.read(fileBuff);
            }
            /*
                上传文件。
                参数含义：要上传的文件的内容（使用字节数组传递），上传的文件的类型（扩展名），元数据
             */
            String[] fileids = storageClient.upload_file(fileBuff, getFileExt(fileName), metaList);
            return fileids;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件上传
     *
     * @param file     上传的文件
     * @param fileName 上传的文件的原始名
     * @return
     */
    public static String[] uploadFile(File file, String fileName) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return uploadFile(fis, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件后缀名（不带点）
     *
     * @param fileName
     * @return 如："jpg" or ""
     */
    private static String getFileExt(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1); // 不带最后的点
    }

    /**
     * 获取文件详情
     *
     * @param groupName      组/卷名，默认值：group1
     * @param remoteFileName 文件名，例如："M00/00/00/wKgKZl9tkTCAJAanAADhaCZ_RF0495.jpg"
     * @return 文件详情
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName) {
        try {
            return storageClient.get_file_info(groupName == null ? "group1" : groupName, remoteFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取元数据
     *
     * @param groupName      组/卷名，默认值：group1
     * @param remoteFileName 文件名，例如："M00/00/00/wKgKZl9tkTCAJAanAADhaCZ_RF0495.jpg"
     * @return 文件的元数据数组
     */
    public static NameValuePair[] getMetaData(String groupName, String remoteFileName) {
        try {
            // 根据组名和文件名通过 Storage 客户端获取文件的元数据数组
            return storageClient.get_metadata(groupName == null ? "group1" : groupName, remoteFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param groupName      组/卷名，默认值：group1
     * @param remoteFileName 文件名，例如："M00/00/00/wKgKZl9tkTCAJAanAADhaCZ_RF0495.jpg"
     * @return 文件的字节输入流
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) {
        try {
            // 根据组名和文件名通过 Storage 客户端获取文件的字节数组
            byte[] bytes = storageClient.download_file(groupName == null ? "group1" : groupName, remoteFileName);
            // 返回字节流对象
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件删除
     *
     * @param groupName      组/卷名，默认值：group1
     * @param remoteFileName 文件名，例如："M00/00/00/wKgKZl9tkTCAJAanAADhaCZ_RF0495.jpg"
     * @return 0为成功，非0为失败
     */
    public static int deleteFile(String groupName, String remoteFileName) {
        int result = -1;
        try {
            // 根据组名和文件名通过 Storage 客户端删除文件
            result = storageClient.delete_file(groupName == null ? "group1" : groupName, remoteFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 修改一个已经存在的文件
     *
     * @param oldGroupName 旧组名
     * @param oldFileName  旧文件名
     * @param file         新文件
     * @param fileName     新文件名
     * @return
     */
    public static String[] modifyFile(String oldGroupName, String oldFileName, File file, String fileName) {
        // 先上传
        String[] fileids = uploadFile(file, fileName);
        if (fileids == null) {
            return null;
        }
        // 再删除
        int delResult = deleteFile(oldGroupName, oldFileName);
        if (delResult != 0) {
            return null;
        }
        return fileids;
    }

}
