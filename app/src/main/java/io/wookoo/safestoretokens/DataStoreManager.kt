package io.wookoo.safestoretokens

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.dataStore
import io.wookoo.protodatastore.user.UserInfoOuterClass.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch


class DataStoreManager(
    private val context: Context,
    userInfoSerializer: UserInfoSerializer
) {
    private val Context.protoDataStore: DataStore<UserInfo> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = userInfoSerializer
    )


    fun getSettings(): Flow<UserInfo> {
        return context.protoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(UserInfo.getDefaultInstance())
                } else {
                    throw exception
                }
            }
    }

    suspend fun saveData(jwt: String) {
        context.protoDataStore.updateData { data ->
            data.toBuilder()
                .setJwt(jwt)
                .build()
        }
    }

    companion object {
        private const val DATA_STORE_FILE_NAME = "user.pb"
    }
}