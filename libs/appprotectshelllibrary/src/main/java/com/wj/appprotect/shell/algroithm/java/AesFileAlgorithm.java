package com.wj.appprotect.shell.algroithm.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;

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
        byte[] encryptByte = getFileContent(encodeFile);
        Cipher aesCipher = getAesCipher(Cipher.DECRYPT_MODE);
        try {
            FileOutputStream fos = new FileOutputStream(decryptFile);
            byte[] base64 = Base64.decode(encryptByte);
            byte[] decryptBytes = aesCipher.doFinal(base64); //DesAlgorithm.decrypt(base64,"123456");//
            fos.write(decryptBytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptFile;
    }
//    public File decrypt(File encodeFile, File decryptFile) {
//        FileInputStream inputStream = null;
//        FileOutputStream outputStream = null;
//        File decryptDirectory = new File(decryptFile.getParent());
//        if (decryptDirectory.exists()) {
//            decryptDirectory.delete();
//        } else {
//            decryptDirectory.mkdirs();
//        }
//        try {
//            // decryptFile = new File(decryptDirectory, encodeFile.getName().substring(3));//File.createTempFile(encodeFile.getName(), getFileSuffix(encodeFile.getPath()), decryptDirectory);
//            // LogUtils.logV("encodeFile = " + encodeFile + " \n" + getFileSuffix(encodeFile.path))
//            LogUtils.logV("decryptFile = " + decryptFile);
//
//            // SystemPrint.outPrintln(getFileSuffix(encodeFile.path))
//            inputStream = new FileInputStream(encodeFile);
//            outputStream = new FileOutputStream(decryptFile);
//
//            CipherOutputStream cipherOutputStream =
//                    new CipherOutputStream(outputStream, getAesCipher(Cipher.DECRYPT_MODE));
//            byte[] cache = new byte[1024];
//            int lineByte = inputStream.read(cache);
//            while (inputStream.read(cache) > 0) {
//                cipherOutputStream.write(cache, 0, lineByte);
//                //lineByte = inputStream.read(cache);
//            }
//            cipherOutputStream.close();
//
//        } catch (Exception e) {
//
//        } finally {
//            try {
//                inputStream.close();
//                outputStream.close();
//            } catch (Exception e) {
//            }
//
//        }
//        return decryptFile;
//    }

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