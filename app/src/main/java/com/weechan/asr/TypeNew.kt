package com.weechan.asr;

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
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.weechan.asr.Adapter.Companion.TYPE_NEW
import com.weechan.asr.base.BaseViewHolder
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
import android.view.LayoutInflater


class TypeNew(private val adapter: Adapter) : Type<SoundSource>(R.layout.item_new, TYPE_NEW, adapter) {
    private val musicPlayer = com.weechan.asr.utils.MusicPlayer.getInstant()
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewHolderCreated(holder: BaseViewHolder<SoundSource>, viewType: Int) {
        var animator: Animator

        holder.getView<ImageView>(R.id.play).setOnClickListener {
            if (!musicPlayer.isPlaying) {
                val entity = adapter.getVal(holder.adapterPosition)!!.entity
                musicPlayer.play(entity.wav)
            } else {
                musicPlayer.reset();
            }
        }

        holder.getView<ImageView>(R.id.record).setOnTouchListener { v, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN ->

                    AudioRecorder.getInstant().startRecord(object : AudioRecorder.Listener {
                        override fun onDataAvaliable(data: ByteArray) {}
                        override fun onPause() {}
                        override fun onStop() {}
                    })

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        val entity = adapter.getVal(holder.adapterPosition)!!
                        val data = entity.entity
                        val dialog = adapter.context?.indeterminateProgressDialog(message = "识别中...", title = "请稍等").apply { this?.setCancelable(false) }
                        val req = async {
                            SoundModel.stopRecordAndFetch(data.phn, data.wrd)
                        }
                        val resp = req.await()
                        var count = 0;
                        if (resp.code == 200) {
                            Log.e("TAG",resp.data.toString())
                            data.sentence.words.filterIndexed { index, word ->
                                word.regularSpell = resp.data.spell[index]
                                word.yourSepll = word.regularSpell;
                                resp.data.err.contains(index)
                            }.forEachIndexed { index, word ->
                                if (count < resp.data.errSpell.size)
                                    word.yourSepll = resp.data.errSpell.get(count)?:""
                                count++
                                word.score = 0
                            }
                            notifyItemChange(entity)

                        } else {
                            resp.message.toast()
                        }

                        dialog?.dismiss()
                    }
                }
            }

            true
        }

        animator = ObjectAnimator
                .ofFloat(holder.getView(R.id.play), "rotation", 0f, 720f)
                .apply {
                    this.duration = 2000
                    this.repeatMode = RESTART
                    this.repeatCount = INFINITE
                    this.interpolator = LinearInterpolator()
                }

        musicPlayer.addOnStartListener {
            animator.start()
            holder.getView<ImageView>(R.id.play).setImageResource(R.drawable.stop)

        }
        musicPlayer.addOnCompleteListener {
            animator.cancel()
            holder.getView<ImageView>(R.id.play).let {
                it.setImageResource(R.drawable.play)
                it.rotation = 0f;
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<SoundSource>, data: SoundSource) {
        holder.getView<TextView>(R.id.page).text = "${(holder.adapterPosition + 1)}/${adapter.data.size}"

        holder.getView<TextView>(R.id.spannable).apply {
            text = createSpan(data.sentence.content, data.sentence.words)
            autoLinkMask = 0
            movementMethod = LinkMovementMethod.getInstance()
        }
    }


    private fun createSpan(content: String, words: List<Word>): SpannableString {
        val ss = SpannableString(content)
        var start = 0;
        words.forEachIndexed { index, word ->
            ss.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val dialog  = BottomSheetDialog(adapter.context!!)
                    dialog.setContentView(R.layout.word_sheet)
                    dialog.findViewById<View>(R.id.design_bottom_sheet)
                            ?.setBackgroundResource(android.R.color.transparent);
                    val wordTv = dialog.findViewById<TextView>(R.id.word)
                    val spellTv = dialog.findViewById<TextView>(R.id.regular_spell)
                    val yourSpellTv = dialog.findViewById<TextView>(R.id.yourSpell)
                    with(word){
                        wordTv?.text = word.content
                        spellTv?.text = "标准读音：[$regularSpell]"
                        yourSpellTv?.text =  " 你的读音：[$yourSepll]"
                    }
                    dialog.show()

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
