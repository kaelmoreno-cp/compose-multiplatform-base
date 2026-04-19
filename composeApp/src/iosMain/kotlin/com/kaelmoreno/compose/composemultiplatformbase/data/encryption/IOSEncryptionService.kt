package com.kaelmoreno.compose.composemultiplatformbase.data.encryption

import com.kaelmoreno.compose.composemultiplatformbase.Logger
import kotlinx.cinterop.*
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IOSEncryptionService : EncryptionService {

    companion object {
        private const val TAG = "IOSEncryptionService"
    }

    override fun encrypt(data: String): String? {
        return try {
            val nsString = NSString.create(string = data)
            val nsData = nsString.dataUsingEncoding(NSUTF8StringEncoding)
                ?: return null
            nsData.base64EncodedStringWithOptions(0u)
        } catch (e: Exception) {
            Logger.e("Error encrypting data", e, TAG)
            null
        }
    }

    override fun decrypt(encryptedData: String): String? {
        return try {
            val nsData = NSData.create(
                base64EncodedString = encryptedData,
                options = 0u
            ) ?: return null
            NSString.create(data = nsData, encoding = NSUTF8StringEncoding)?.toString()
        } catch (e: Exception) {
            Logger.e("Error decrypting data", e, TAG)
            null
        }
    }

    override fun isInitialized(): Boolean = true
}
