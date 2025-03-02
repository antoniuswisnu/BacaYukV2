package com.nara.bacayuk.ui.feat_urut_abjad

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.Typeface
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnDragListener
import android.view.View.OnTouchListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.ActivityUrutAbjadBinding

class UrutAbjadActivity : AppCompatActivity() {
    private val binding by lazy { ActivityUrutAbjadBinding.inflate(layoutInflater) }
    private var answerList = arrayListOf<String>("A","B", "C", "D","E")
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {

            opt1.setOnTouchListener(ChoiceTouchListener())

            cho1.setOnDragListener(ChoiceDragListener())
            cho2.setOnDragListener(ChoiceDragListener())
            cho3.setOnDragListener(ChoiceDragListener())
            cho4.setOnDragListener(ChoiceDragListener())
            cho5.setOnDragListener(ChoiceDragListener())

            reset.setOnClickListener { reset() }
        }
    }

    private class ChoiceTouchListener : OnTouchListener {
        @SuppressLint("NewApi")
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                /*
                 * Drag details: we only need default behavior
                 * - clip data could be set to pass data as part of drag
                 * - shadow can be tailored
                 */
                //change the text color to teal 200
                val tv = view as TextView
                tv.setTextColor(view.context.getColor(R.color.teal_200))
                //change size of text only dragged
//                tv.textSize = 200f
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0)
                true
            } else {
                false
            }
        }
    }

    private fun reset() {
        binding.apply {
            opt1.visibility = View.VISIBLE

            cho1.text = "A"
            cho2.text = "B"
            cho3.text = "C"
            cho4.text = "D"
            cho5.text = "E"
            cho1.setTag(null)
            cho2.setTag(null)
            cho3.setTag(null)
            cho4.setTag(null)
            cho5.setTag(null)
            cho1.setTypeface(Typeface.DEFAULT)
            cho2.setTypeface(Typeface.DEFAULT)
            cho3.setTypeface(Typeface.DEFAULT)
            cho4.setTypeface(Typeface.DEFAULT)
            cho5.setTypeface(Typeface.DEFAULT)
            cho1.setOnDragListener(ChoiceDragListener())
            cho2.setOnDragListener(ChoiceDragListener())
            cho3.setOnDragListener(ChoiceDragListener())
            cho4.setOnDragListener(ChoiceDragListener())
            cho5.setOnDragListener(ChoiceDragListener())
        }
    }

    inner class ChoiceDragListener : OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    //change the text color to teal200
                }
                DragEvent.ACTION_DRAG_ENTERED -> {

                }
                DragEvent.ACTION_DRAG_EXITED -> {

                }
                DragEvent.ACTION_DROP -> {
                    //handle the dragged view being dropped over a drop view
                    val view = event.localState as View
                    //view dragged item is being dropped on
                    val dropTarget = v as TextView
                    //resize when being dragged
                    //view being dragged and dropped
                    val dropped = view as TextView
                    //checking whether first character of dropTarget equals first character of dropped
                    if (dropTarget.text.toString()[0] == dropped.text.toString()[0]) {
                        //stop displaying the view where it was before it was dragged
                        view.setVisibility(View.INVISIBLE)
                        //update the text in the target view to reflect the data being dropped
                        //todo : change text color
                        dropTarget.text = dropped.text
                        dropTarget.setTextColor(resources.getColor(R.color.purple_200))
                        //add animation
                        val animation = android.view.animation.AnimationUtils.loadAnimation(
                            applicationContext,
                            R.anim.scale
                        )
                        dropTarget.startAnimation(animation)

//                        dropTarget.text = dropTarget.text.toString() + dropped.text.toString()
                        //make it bold to highlight the fact that an item has been dropped
                        dropTarget.setTypeface(Typeface.DEFAULT_BOLD)
                        //if an item has already been dropped here, there will be a tag
                        val tag = dropTarget.tag
                        //if there is already an item here, set it back visible in its original place
                        if (tag != null) {
                            //the tag is the view id already dropped here
                            val existingID = tag as Int
                            //set the original view visible again

                            binding.root.findViewById<View>(existingID).setVisibility(View.VISIBLE)
//                            findViewById<View>(existingID).setVisibility(View.VISIBLE)
                        }
                        //set the tag in the target view being dropped on - to the ID of the view being dropped
                        dropTarget.tag = dropped.id
                        //remove setOnDragListener by setting OnDragListener to null, so that no further drag & dropping on this TextView can be done
                        dropTarget.setOnDragListener(null)
                        Toast.makeText(this@UrutAbjadActivity, "BENAR!", Toast.LENGTH_SHORT).show()
                    } else  //displays message if first character of dropTarget is not equal to first character of dropped
                        Toast.makeText(v.context, "SALAH!", Toast.LENGTH_SHORT).show()

                }
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            return true
        }
    }
}