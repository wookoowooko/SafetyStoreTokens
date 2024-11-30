package io.wookoo.safestoretokens

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::DataStoreManager)
    singleOf(::FakeApiRepo)
    singleOf(::CryptoManager)
    single {
        UserInfoSerializer
    }
}