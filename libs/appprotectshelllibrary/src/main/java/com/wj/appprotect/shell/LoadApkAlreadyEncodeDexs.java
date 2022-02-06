package com.wj.appprotect.shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class LoadApkAlreadyEncodeDexs {

    /**
     * 将.apk解压缩成文件夹
     *
     * @param zipFile     需要解压的文件
     * @param descDirPath 解压文件存放的上级目录
     * @return 返回的是解压之后的文件夹的绝对路径
     */
    public String unZipApk(File zipFile, String descDirPath) {
        if (!zipFile.getName().endsWith(".apk")) {
            throw new IllegalArgumentException("The zip file must end with .apk");
        }
        return unZipFile(zipFile, descDirPath);
    }

    /**
     * 解压缩成文件夹
     *
     * @param zipFile     需要解压的文件
     * @param descDirPath 解压文件存放的目录
     * @return 返回的是解压之后的文件夹的绝对路径
     */
    private String unZipFile(File zipFile, String descDirPath) {
        if (!zipFile.exists()) {
            throw new IllegalArgumentException("The zip file not exist !");
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        byte[] buffer = new byte[1024];
        //目标的文件夹,以传入的文件名字来创建文件夹
        String resultFileAbsoluteDir = descDirPath + "/" + getZipFileNameWithoutSuffix(zipFile);
        try {
            ZipFile zip = new ZipFile(zipFile);
            Enumeration zipEntries = zip.entries();
            while (zipEntries.hasMoreElements()) {
                //获取zip文件的名字
                ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
                String zipEntryName = zipEntry.getName();
                //获取解压文件的路径
                inputStream = zip.getInputStream(zipEntry);
                //以文件的名字作为最外层的文件夹
                String descFilePath = resultFileAbsoluteDir + "/" + zipEntryName;
                File descFile = createFile(descFilePath);
                //读文件
                outputStream = new FileOutputStream(descFile);
                int len = inputStream.read(buffer);
                while (len > 0) {
                    outputStream.write(buffer, 0, len);
                    len = inputStream.read(buffer);
                }
                //关闭流
                inputStream.close();
                outputStream.close();
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {

            }

        }
        LogUtils.logV(zipFile.getName() + "finished to 'unzip' in \n" + descDirPath);
        return resultFileAbsoluteDir;
    }

    /**
     * 获取apk的名字
     */
    private String getZipFileNameWithoutSuffix(File zipFile) {
        return zipFile.getName().substring(0, zipFile.getName().lastIndexOf("."));
    }

    /**
     * 创建文件
     */
    private File createFile(String filePath) throws IOException {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
