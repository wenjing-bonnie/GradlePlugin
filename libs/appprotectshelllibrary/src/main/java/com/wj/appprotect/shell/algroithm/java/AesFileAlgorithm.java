package com.wj.appprotect.shell.algroithm.java;

import com.wj.appprotect.shell.LogUtils;

import java.io.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
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
        return "123456";
    }

    /**
     * 对文件进行 AES 加密
     *
     * @return 返回的是加密之后的文件
     */
    public File encrypt(File sourceFile) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        File encryptFile = null;

        //新建临时加密文件
        try {
            encryptFile = File.createTempFile(sourceFile.getName(), getFileSuffix(sourceFile.getPath()));
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(encryptFile);

            CipherInputStream cipherInputStream =
                    new CipherInputStream(inputStream, getAesCipher(Cipher.ENCRYPT_MODE));
            byte[] cache = new byte[1024];
            int lineByte = cipherInputStream.read(cache);
            while (lineByte > 0) {
                outputStream.write(cache, 0, lineByte);
                lineByte = cipherInputStream.read(cache);
            }
            cipherInputStream.close();

        } catch (Exception e) {

        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {

            }
        }
        return encryptFile;
    }

    /**
     * 解密
     *
     * @return 返回解密之后的文件
     */
    public File decrypt(File encodeFile, File decryptFile) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        File decryptDirectory = new File(decryptFile.getParent());
        if (decryptDirectory.exists()) {
            decryptDirectory.delete();
        } else {
            decryptDirectory.mkdirs();
        }
        try {
            // decryptFile = new File(decryptDirectory, encodeFile.getName().substring(3));//File.createTempFile(encodeFile.getName(), getFileSuffix(encodeFile.getPath()), decryptDirectory);
            // LogUtils.logV("encodeFile = " + encodeFile + " \n" + getFileSuffix(encodeFile.path))
            LogUtils.logV("decryptFile = " + decryptFile);

            // SystemPrint.outPrintln(getFileSuffix(encodeFile.path))
            inputStream = new FileInputStream(encodeFile);
            outputStream = new FileOutputStream(decryptFile);

            CipherOutputStream cipherOutputStream =
                    new CipherOutputStream(outputStream, getAesCipher(Cipher.DECRYPT_MODE));
            byte[] cache = new byte[1024];
            int lineByte = inputStream.read(cache);
            while (lineByte > 0) {
                cipherOutputStream.write(cache, 0, lineByte);
                lineByte = inputStream.read(cache);
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
    public String getFileContent(File file) {
        if (file == null) {
            return "";
        }
        BufferedReader bufferReader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            bufferReader = new BufferedReader(new FileReader(file));
            String content = bufferReader.readLine();
            while (content != null) {
                buffer.append(content);
                buffer.append("\n");
                content = bufferReader.readLine();
            }
            return buffer.toString();
        } catch (Exception e) {

        } finally {
            try {
                bufferReader.close();
            } catch (Exception e) {
            }

        }
        return buffer.toString();
    }

    /**
     * 获取到文件的后缀名
     */
    private String getFileSuffix(String sourceFilePath) {
        // suffix
        return sourceFilePath.substring(sourceFilePath.lastIndexOf("."));
    }
}