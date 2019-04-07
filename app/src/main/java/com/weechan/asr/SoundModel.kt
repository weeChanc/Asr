package com.weechan.asr

import android.util.Log
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.weechan.asr.data.SoundSource
import com.weechan.asr.net.BASE_URL
import com.weechan.asr.net.MResposne
import com.weechan.asr.utils.other.IOUtils
import cz.msebera.android.httpclient.Header
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class SoundModel {

    companion object {
        val client = AsyncHttpClient(80)

        suspend fun request(wav: ByteArray, phn: File, wrd: File): MResposne {
            return suspendCoroutine<MResposne> {
                val params = RequestParams()
                params.put("wav", ByteArrayInputStream(wav), "wav")
                params.put("phn", phn)
                params.put("wrd", wrd)
                params.put("stamp", System.currentTimeMillis())
                client.post(BASE_URL + "/calculate", params, object : AsyncHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                        val json = String(responseBody!!).toString();
                        Log.e("TAG",json)
                        val mr = Gson().fromJson(json, MResposne::class.java)
                        it.resume(mr);
                    }
                    override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                        it.resumeWithException(RuntimeException(error?.message))
                    }
                })
            }
        }
    }

    lateinit var sources: List<SoundSource>

    fun init() {
        IOUtils.extraFile(App.app.assets.open("model.zip"), File(App.app.filesDir, "model"))
        IOUtils.extraFile(App.app.assets.open("testdata.zip"), File(App.app.filesDir, "testdata"))
        File(App.app.filesDir, "sound-asr").mkdir()
        sources = SoundSource.readAllSoundSource(File(App.app.filesDir, "testdata").absolutePath)
    }
}
