package io.wookoo.safestoretokens

import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import io.wookoo.protodatastore.user.UserInfoOuterClass.UserInfo
import org.koin.core.component.KoinComponent
import java.io.InputStream
import java.io.OutputStream


object UserInfoSerializer : Serializer<UserInfo>, KoinComponent {

    private val cryptoManager: CryptoManager = getKoin().get()

    override val defaultValue: UserInfo
        get() = UserInfo.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserInfo {
        val encryptedData: String = input.bufferedReader().use { it.readText() }
        val decryptedData: String = cryptoManager.decryptData(encryptedData)
        return try {
            UserInfo.parseFrom(decryptedData.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserInfo, output: OutputStream) {
        try {
            val serializedData = t.toByteArray().toString(charset("UTF-8"))
            val dataToBeEncrypted = cryptoManager.encryptText(serializedData)
            output.bufferedWriter().use { it.write(dataToBeEncrypted) }
        } catch (e: IOException) {
            e.printStackTrace()
            throw IOException("Error writing UserInfo")
        }
    }

}
