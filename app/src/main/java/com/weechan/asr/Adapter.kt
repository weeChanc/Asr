package com.weechan.asr

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.weechan.asr.base.BaseAdapter
import com.weechan.asr.base.MultiEntity
import com.weechan.asr.base.Type
import com.weechan.asr.data.SoundSource
import com.weechan.asr.data.Word
import com.weechan.asr.utils.AudioRecorder
import com.weechan.asr.utils.other.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.indeterminateProgressDialog

@SuppressLint("ClickableViewAccessibility")
class Adapter(var sources: List<MultiEntity<SoundSource>>) : BaseAdapter<SoundSource>(sources) {

    val MusicPlayer = com.weechan.asr.utils.MusicPlayer()

    val TYPE_PRE = 1;
    val TYPE_NEW = 2;

    inner class TypePre : Type<SoundSource>(R.layout.item_pre, TYPE_PRE) {
        init {
            var preIndex = 0;

            whenCreate {
                setOnItemClickListener { v, pos, item ->
                    if (pos == preIndex || item == null) return@setOnItemClickListener
                    notifyItemChanged(pos)
                    item.typeId = TYPE_NEW
                    sources[preIndex].typeId =TYPE_PRE;
                    notifyItemChanged(preIndex)
                    notifyItemChanged(pos)
                    preIndex = pos
                    v.post {
                        MusicPlayer.play(item.entity.wav)
                    }

                }

            }

            whenBind { data ->
                R.id.title.toView<TextView>().text = data.sentence.content
            }
        }
    }

    inner class TypeNew : Type<SoundSource>(R.layout.item_new, TYPE_NEW) {
        init {
            whenCreate {
                var animator: Animator
                getView<ImageView>(R.id.play).setOnClickListener {
                    if (!MusicPlayer.isPlaying) {
                        MusicPlayer.play(entity.wav)
                    } else {
                        MusicPlayer.reset();
                    }

                }

                getView<ImageView>(R.id.record).setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> AudioRecorder.getInstant().startRecord(object : AudioRecorder.Listener {
                            override fun onDataAvaliable(data: ByteArray) {}

                            override fun onPause() {}

                            override fun onStop() {}
                        })
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            GlobalScope.launch(Dispatchers.Main) {
                                val dialog = context.indeterminateProgressDialog(message = "识别中...", title = "请稍等")
                                val req = async {
                                    SoundModel.stopRecordAndFetch(entity.phn, entity.wrd)
                                }
                                val resp = req.await()
                                if (resp.code == 200) {
                                    entity.sentence.words.filterIndexed { index, word -> resp.data.err.contains(index) }
                                            .forEachIndexed { index, word ->
                                                adapter.notifyItemChanged(index)
                                                word.score = 0
                                            }
                                } else {
                                    resp.message.toast()
                                }

                                dialog.dismiss()
                            }
                        }
                    }


                    true
                }

                animator = ObjectAnimator
                        .ofFloat(R.id.play.toView<ImageView>(), "rotation", 0f, 720f)
                        .apply {
                            this.duration = 2000
                            this.repeatMode = RESTART
                            this.repeatCount = INFINITE
                            this.interpolator = LinearInterpolator()
                        }

                MusicPlayer.addOnStartListener {
                    animator.start()
                    R.id.play.toView<ImageView>().let {
                        it.setImageResource(R.drawable.stop)
                    }

                }
                MusicPlayer.addOnCompleteListener {
                    animator.cancel()
                    R.id.play.toView<ImageView>().let {
                        it.setImageResource(R.drawable.play)
                        it.rotation = 0f;
                    }
                }
            }

            whenBind { data ->
                R.id.page.toView<TextView>().text = "${(adapterPosition + 1)}/${sources.size}"

                R.id.spannable.toView<TextView>().apply {
                    text = createSpan(data.sentence.content, data.sentence.words)
                    autoLinkMask = 0
                    movementMethod = LinkMovementMethod.getInstance()
                }

            }
        }

        fun createSpan(content: String, words: List<Word>): SpannableString {
            val ss = SpannableString(content)
            var start = 0;
            words.forEachIndexed { index, word ->
                ss.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        toast(word.content)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = false; // set to false to remove underline

                    }
                }, start, start + word.content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (word.score == 0) {
                    ss.setSpan(ForegroundColorSpan(Color.RED), start, start + word.content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                start += word.content.length + 1
            }
            return ss;
        }
    }

    class TypeHead : Type<SoundSource>(R.layout.item_pre, 3) {
        init {
            whenCreate {  }
            whenBind {  }
        }
    }


    init {
        addType(TypeNew())
        addType(TypePre())
        addHeader(TypeHead())
    }

}
