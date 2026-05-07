package com.mkarshnas6.karenstudio.worldskill.utils.encrypte

import java.io.File
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class CryptoManager(private val keyManager: KeyManager) {
    companion object {
        const val KEY_ALIAS = "secureKeyWorldSkills"
        const val IV_SIZE = 12
    }

    fun encryptFile(file: File, outputFile: File) {
        val secretKey = keyManager.getOrCreateKey(KEY_ALIAS)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val originalData = file.readBytes()
        val encryptData = cipher.doFinal(originalData)
        FileOutputStream(outputFile).use { outputStream ->
            outputStream.write(iv)
            outputStream.write(encryptData)
        }
    }

    fun decryptFile(encryptedFile: File, outputFile: File) {
        val secureKey = keyManager.getOrCreateKey(KEY_ALIAS)

        val encryptedData = encryptedFile.readBytes()

        val iv = encryptedData.copyOfRange(0, IV_SIZE)
        val cipherText = encryptedData.copyOfRange(IV_SIZE, encryptedData.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secureKey, spec)

        val decryptData = cipher.doFinal(cipherText)
        outputFile.writeBytes(decryptData)
    }

    fun encryptText(plainText: String): ByteArray {
        val secretKey = keyManager.getOrCreateKey(KEY_ALIAS)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv
        val encrypt = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        return iv + encrypt
    }

    fun decryptText(encryptText: ByteArray): String {
        val secretKey = keyManager.getOrCreateKey(KEY_ALIAS)

        val iv = encryptText.copyOfRange(0, IV_SIZE)
        val cipherText = encryptText.copyOfRange(IV_SIZE, encryptText.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decryptData = cipher.doFinal(cipherText)
        return String(decryptData, Charsets.UTF_8)
    }

}