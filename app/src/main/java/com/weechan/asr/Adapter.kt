package com.weechan.asr

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.weechan.asr.data.SoundSource
import com.weechan.asr.net.*
import com.weechan.asr.utils.AudioRecorder
import com.weechan.asr.utils.MusicPlayer
import com.weechan.asr.utils.other.toast
import java.io.ByteArrayOutputStream
import java.io.File
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.progressDialog
import java.io.ByteArrayInputStream
import java.io.FileOutputStream


class Adaptee(internal var sources: List<SoundSource>) : RecyclerView.Adapter<Adaptee.SoundHolder>() {

    private var checkIndex = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SoundHolder {
        val view: View
        if (i == 1) {
            view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_new, viewGroup, false)
        } else {
            view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_pre, viewGroup, false)
        }

        val holder = SoundHolder(view, i)

        if (holder.play != null) {
            holder.play!!.setOnClickListener { MusicPlayer.play(sources[holder.adapterPosition].wav) }
            holder.record!!.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> AudioRecorder.getInstant().startRecord(object : AudioRecorder.Listener {
                        override fun onDataAvaliable(data: ByteArray) {}

                        override fun onPause() {}

                        override fun onStop() {}
                    })
                    MotionEvent.ACTION_UP , MotionEvent.ACTION_CANCEL -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            val dialog = viewGroup.context.indeterminateProgressDialog(message = "识别中...", title = "请稍等")
                            val req = async {
                                val bytes = AudioRecorder.getInstant().stop()
                                val os = ByteArrayOutputStream()
                                for (aByte in bytes!!) {
                                    os.write(aByte)
                                }

                                val fos = FileOutputStream(Environment.getExternalStorageDirectory().absolutePath+"/output.pcm")
                                bytes.forEach { fos.write(it) }
                                fos.flush();fos.close();

                                val wav = AudioRecorder.convertPcmToWav(os.toByteArray(), 16000, 1, 16)
                                AudioRecorder.convertPcmToWav(Environment.getExternalStorageDirectory().absolutePath+"/output.pcm",
                                        Environment.getExternalStorageDirectory().absolutePath+"/output.wav",16000,1,16)

                                return@async sources[holder.adapterPosition].run {
                                    SoundModel.request(wav, File(phn), File(wrd))
                                }
                            }

                            val resp = req.await()
                            if(resp.code == 200){
                                sources[holder.adapterPosition].sentence.words.filterIndexed { index, word -> resp.data.err.contains(index) }
                                        .forEachIndexed { index,word->
                                            this@Adaptee.notifyItemChanged(index)
                                            word.score = 0
                                        }
                            }else{
                                "服务器解析错误".toast()
                            }

                            dialog.dismiss()

                        }


                    }
                }

                true
            }
        }

        view.setOnClickListener { v ->
            if (checkIndex != holder.adapterPosition) {
                notifyItemChanged(checkIndex, null)
                checkIndex = holder.adapterPosition
                notifyItemChanged(checkIndex, null)
            }
        }

        return holder
    }


    override fun onBindViewHolder(holder: SoundHolder, i: Int) {
        when (getItemViewType(holder.adapterPosition)) {
            TYPE_NEW -> run {
                holder.page?.text = (holder.adapterPosition + 1).toString() + "/" + sources.size
                val ss = SpannableString(sources[i].sentence.content)
                var start = 0;
                sources[i].sentence.words.forEachIndexed { index, word ->
                    ss.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            toast(word.content)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.setUnderlineText(false); // set to false to remove underline
                        }
                    }, start, start + word.content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if(word.score == 0 ){
                        ss.setSpan(ForegroundColorSpan(Color.RED),start, start + word.content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    start += word.content.length + 1
                }
                holder.title.text = ss
                holder.title.setAutoLinkMask(0)
                holder.title.movementMethod = LinkMovementMethod.getInstance()
            }
            TYPE_PRE -> {
                holder.title.text = sources[i].sentence.content
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == checkIndex) 1 else 0
    }

    override fun getItemCount(): Int {
        return sources.size
    }

    inner class SoundHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {

        var title: TextView

        var play: ImageView? = null
        var record: ImageView? = null
        var page: TextView? = null

        init {
            if (type == TYPE_PRE) {
                title = itemView.findViewById(R.id.title)
            } else {
                title = itemView.findViewById(R.id.spannable)
                play = itemView.findViewById(R.id.play)
                record = itemView.findViewById(R.id.record)
                page = itemView.findViewById(R.id.page)
            }
        }
    }

    companion object {
        val TYPE_PRE = 0
        val TYPE_NEW = 1
    }
}
