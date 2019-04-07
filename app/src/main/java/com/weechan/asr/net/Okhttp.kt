
package com.weechan.asr.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


val BASE_URL = "http://172.17.66.67"
val OkClient = OkHttpClient.Builder()
        .build()