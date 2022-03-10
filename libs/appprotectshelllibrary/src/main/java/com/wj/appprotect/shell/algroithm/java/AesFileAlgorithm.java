package com.wj.appprotect.shell.algroithm.java;

import com.wj.appprotect.shell.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * Created by wenjing.liu on 2022/1/21 in J1.
 * <p>
 * 使用AES对文件进行加密和解密
 *
 * @author wenjing.liu
 */
public class AesFileAlgorithm extends AbstractAesAlgorithm {

    public String getPassword() {
        return "1234567890123456";
    }

    /**
     * 解密
     *
     * @return 返回解密之后的文件
     */
    public File decrypt(File encodeFile, File decryptFile) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(encodeFile);
            outputStream = new FileOutputStream(decryptFile);

            CipherOutputStream cipherOutputStream =
                    new CipherOutputStream(outputStream, getAesCipher(Cipher.DECRYPT_MODE));
            byte[] cache = new byte[1024];
            int lineByte;
            while ((lineByte = inputStream.read(cache)) > 0) {
                cipherOutputStream.write(cache, 0, lineByte);
            }
            cipherOutputStream.close();
        } catch (Exception e) {

        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
            }
        }
        return decryptFile;
    }

    /**
     * 获取到文件内容
     */
    public byte[] getFileContent(File file) {
        if (file == null) {
            return new byte[1];
        }
        BufferedReader bufferReader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            bufferReader = new BufferedReader(new FileReader(file));
            String content;
            while ((content = bufferReader.readLine()) != null) {
                buffer.append(content);
                buffer.append("\n");
            }
            LogUtils.logV("" + buffer.toString());
            return buffer.toString().getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferReader.close();
            } catch (Exception e) {
            }

        }
        return buffer.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 获取到文件的后缀名
     */
    private String getFileSuffix(String sourceFilePath) {
        // suffix
        return sourceFilePath.substring(sourceFilePath.lastIndexOf("."));
    }
}