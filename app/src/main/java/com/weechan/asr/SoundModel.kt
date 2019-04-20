package com.weechan.asr

import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.weechan.asr.data.SoundSource
import com.weechan.asr.net.BASE_URL
import com.weechan.asr.net.MResposne
import com.weechan.asr.net.Resp
import com.weechan.asr.utils.AudioRecorder
import com.weechan.asr.utils.other.IOUtils
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.async
import org.jetbrains.anko.custom.async
import java.io.*
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class SoundModel {

    companion object {
        val client = AsyncHttpClient(80).apply { setTimeout(20000) }

        suspend fun stopRecordAndFetch(phn: String, wrd: String): MResposne {
            val bytes = AudioRecorder.getInstant().stop()
            val os = ByteArrayOutputStream()
            for (aByte in bytes!!) {
                os.write(aByte)
            }
            val fos = FileOutputStream(Environment.getExternalStorageDirectory().absolutePath + "/output.pcm")
            bytes.forEach { fos.write(it) }
            fos.flush();fos.close();
            val wav = AudioRecorder.convertPcmToWav(os.toByteArray(), 16000, 1, 16)
            return SoundModel.request(wav, File(phn), File(wrd))
        }

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
                        Log.e("TAG", json)
                        val mr = Gson().fromJson(json, MResposne::class.java)
                        it.resume(mr);
                    }

                    override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                        it.resume(MResposne(-1, error.toString(), Resp(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())))
                    }
                })
            }
        }
    }

    lateinit var sources: List<SoundSource>

    fun init() {
        IOUtils.extraFile(App.app.assets.open("testdata.zip"), File(App.app.filesDir, "testdata"))
        File(App.app.filesDir, "sound-asr").mkdir()
        sources = SoundSource.readAllSoundSource(File(App.app.filesDir, "testdata").absolutePath)
    }
}
