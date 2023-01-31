package ru.hse.control_system_v2.connection;

import android.annotation.SuppressLint;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

// Эта реализация использует JCE (расширение криптографии Java) для шифрования массива байтов

public class Chipper {
    private static final String CIPHER_NAME = "Kuznechik";
    private static final String KEY_TYPE = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES";

    public static byte[] encrypt(byte[] key, byte[] message) throws Exception {

        // Encrypt byte array using Kuznechik algorithm
        SecretKeySpec secretKey = new SecretKeySpec(key, KEY_TYPE);
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //выводим для теста
        //System.out.println(Arrays.toString(cipher.doFinal(xmlData)));
        return cipher.doFinal(message);
    }

    //Здесь используется JCE (расширение криптографии Java) для расшифровки массива байтов.
    //Поскольку шифр симметричный, используется тот же ключ, который использовался для шифрования для расшифровки файла.

    public static byte[] decrypt(byte[] key, byte[] message) throws Exception {
        // Decrypt byte array using Kuznechik algorithm
        SecretKeySpec secretKey = new SecretKeySpec(key, KEY_TYPE);
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(message);
    }

    static Key generateKey() throws NoSuchAlgorithmException {
        //Creating a KeyGenerator object
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");

        //Creating a SecureRandom object
        SecureRandom secRandom = new SecureRandom();

        //Initializing the KeyGenerator
        keyGen.init(secRandom);

        //Creating/Generating a key
        Key key = keyGen.generateKey();
        return key;
    }
}

