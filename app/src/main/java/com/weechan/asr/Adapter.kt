package com.weechan.asr

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
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
import com.weechan.asr.utils.other.O
import com.weechan.asr.utils.other.toast
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.File


class Adaptee(internal var records: List<SoundSource>) : RecyclerView.Adapter<Adaptee.SoundHolder>() {

    private var checkIndex = 0

    //    public void addWavesInActivePos(List<Short> waves){
    //        sources.get(checkIndex).setWaves(waves);
    //        notifyItemChanged(checkIndex);
    //    }

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
            holder.play!!.setOnClickListener { MusicPlayer.play(records[holder.adapterPosition].wav) }

            holder.record!!.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> AudioRecorder.getInstant().startRecord(object : AudioRecorder.Listener {
                        override fun onDataAvaliable(data: ByteArray) {}

                        override fun onPause() {}

                        override fun onStop() {}
                    })
                    ACTION_UP -> {
                        val bytes = AudioRecorder.getInstant().stop()
                        toast(bytes.size)
                        val os = ByteArrayOutputStream()
                        for (aByte in bytes!!) {
                            os.write(aByte)
                        }
                        val wav = AudioRecorder.convertPcmToWav(os.toByteArray(), 16000, 2, 16)
                        val builder = MultipartBody.Builder()
                        builder.addFormDataPart("wav", "a.wav",
                                RequestBody.create(MediaType.parse("audio/*"), wav))
                        builder.addFormDataPart("phn", "b.phn",
                                RequestBody.create(MediaType.parse("plain/text"), File(records[holder.adapterPosition].phn)))
                        builder.addFormDataPart("wrd", "c.wrd",
                                RequestBody.create(MediaType.parse("plain/text"), File(records[holder.adapterPosition].wrd)))
                        builder.addFormDataPart("stamp",System.currentTimeMillis().toString())
                        val req = Request.Builder()

                                .url(BASE_URL+"/calculate")
                                .post(builder.build())
                        OkClient.newCall(req.build()).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                toast("failed")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                println(response.body()!!.string())
//                                val resp = Gson().fromJson(response.body()!!.string(), MResposne::class.java)
//                                O.mainThreadHandler.post {
//                                    val errs = resp.data.err
//                                    records[holder.adapterPosition].sentence.words.filterIndexed { index, _ ->
//                                        errs.contains(index)
//                                    }.forEach {
//                                        it.score = 0;
//                                    }
//                                    this@Adaptee.notifyItemChanged(holder.adapterPosition)
//                                }

                            }
                        })
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
                holder.page?.text = (holder.adapterPosition + 1).toString() + "/" + records.size
                val ss = SpannableString(records[i].sentence.content)
                var start = 0;
                records[i].sentence.words.forEachIndexed { index, word ->
                    ss.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            toast(word.content)
                        }
                    }, start, start + word.content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    start += word.content.length + 1
                }
                holder.title.text = ss
                holder.title.setMovementMethod(LinkMovementMethod.getInstance())
            }
            TYPE_PRE -> {
                holder.title.text = records[i].sentence.content
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == checkIndex) 1 else 0
    }

    override fun getItemCount(): Int {
        return records.size
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
