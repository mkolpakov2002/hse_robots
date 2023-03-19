package ru.hse.control_system_v2.encryption

import android.annotation.SuppressLint
import java.security.Key
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 * логика шифрования AES
 */
object Chipper {
    private const val KEY_TYPE = "AES"
    private const val CIPHER_TRANSFORMATION = "AES"
    @Throws(Exception::class)
    fun encrypt(key: ByteArray?, message: ByteArray?): ByteArray {
        // Encrypt byte array using KEY_TYPE algorithm
        val secretKey = SecretKeySpec(key, KEY_TYPE)
        @SuppressLint("GetInstance") val cipher: Cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(message)
    }

    //Здесь используется JCE (расширение криптографии Java) для расшифровки массива байтов.
    //Поскольку шифр симметричный, используется тот же ключ, который использовался для шифрования для расшифровки файла.
    @Throws(Exception::class)
    fun decrypt(key: ByteArray?, message: ByteArray?): ByteArray {
        // Decrypt byte array using KEY_TYPE algorithm
        val secretKey = SecretKeySpec(key, KEY_TYPE)
        @SuppressLint("GetInstance") val cipher: Cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(message)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun generateKey(): Key {
        //Creating a KeyGenerator object
        val keyGen: KeyGenerator = KeyGenerator.getInstance("AES")

        //Creating a SecureRandom object
        val secRandom = SecureRandom()

        //Initializing the KeyGenerator
        keyGen.init(secRandom)

        //Creating/Generating a key
        return keyGen.generateKey()
    }
}