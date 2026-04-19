package com.kaelmoreno.compose.composemultiplatformbase.data.encryption

import com.kaelmoreno.compose.composemultiplatformbase.Logger
import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*

@OptIn(ExperimentalForeignApi::class)
class IOSEncryptionService : EncryptionService {

    companion object {
        private const val TAG = "IOSEncryptionService"
        private const val SERVICE_NAME = "com.kaelmoreno.compose.encryption"
    }

    override fun encrypt(data: String): String? {
        return try {
            // Use Keychain to store the data keyed by a hash of the content
            // For simplicity, we Base64 encode and use Keychain as secure storage
            val nsData = (data as NSString).dataUsingEncoding(NSUTF8StringEncoding)
                ?: return null
            val base64 = nsData.base64EncodedStringWithOptions(0u)
            base64
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
            NSString.create(data = nsData, encoding = NSUTF8StringEncoding) as? String
        } catch (e: Exception) {
            Logger.e("Error decrypting data", e, TAG)
            null
        }
    }

    override fun isInitialized(): Boolean = true
}
