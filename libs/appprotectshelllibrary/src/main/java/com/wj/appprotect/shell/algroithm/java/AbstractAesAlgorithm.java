package com.wj.appprotect.shell.algroithm.java;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wenjing.liu on 2022/1/20 in J1.
 * <p>
 * AES加密
 *
 * @author wenjing.liu
 */
abstract class AbstractAesAlgorithm {
    private String AES = "AES";
    private int KET_SIZE = 128;
    //private val PASSWORD = "123456"
    Charset CHARSET = StandardCharsets.UTF_8;

    abstract String getPassword();

    /**
     * 获取一个密码器
     */
    public Cipher getAesCipher(int opmode) {
        try {
            //1.获取一个密码器
            Cipher cipher = Cipher.getInstance(AES);
            //2.初始化密码器
            cipher.init(opmode, new SecretKeySpec(getPassword().getBytes(), AES));
            //cipher.init(opmode, getAesSecretKey());
            return cipher;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 对密钥进行处理
     * 根据传入的seed构建密钥生成器
     *
     * @return
     */
    private SecretKeySpec getAesSecretKey() throws NoSuchAlgorithmException {
        //1.构建密钥生成器
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
        //2.初始化生成器
        //kgen.init(KET_SIZE, SecureRandom(PASSWORD.toByteArray(CHARSET)))
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(getPassword().getBytes(CHARSET));
        kgen.init(KET_SIZE, secureRandom);
        //3.产生原始对称密钥
        SecretKey originalKey = kgen.generateKey();
        //4.获取原始对称密钥字节数组
        byte[] key = originalKey.getEncoded();
        //5.根据字节数组生成AES密钥
        return new SecretKeySpec(key, AES);
    }
}