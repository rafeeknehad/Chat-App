package com.example.myapplication.extensions

import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_profile.view.*

fun TextInputLayout.onDataChange() {
    this.editText?.doOnTextChanged { text, _, _, _ ->
        if (text?.isNotEmpty() == true) {
            this.error = null
            this.isErrorEnabled = false
        }
    }
}

fun FloatingActionButton.setOnClickListener(textView: TextView, editText: View) {
    this.setOnClickListener {
        when (this) {
            profileFragmentFab1 -> {
                Log.d("rafeek", "setOnClickListener: button 1")
                textView.visibility = View.GONE
                editText.visibility = View.VISIBLE
                this.visibility = View.GONE
            }
            profileFragmentFab2 -> {
                Log.d("rafeek", "setOnClickListener: button 2")
                textView.visibility = View.GONE
                editText.visibility = View.VISIBLE
                this.visibility = View.GONE
            }
            profileFragmentFab3 -> {
                Log.d("rafeek", "setOnClickListener: button 2")
                textView.visibility = View.GONE
                editText.visibility = View.VISIBLE
                this.visibility = View.GONE
            }
            profileFragmentFab4 -> {
                Log.d("rafeek", "setOnClickListener: button 2")
                textView.visibility = View.GONE
                editText.visibility = View.VISIBLE
                this.visibility = View.GONE
            }
        }
    }
}

fun EditText.setOnEditorAction(textView: TextView) {
    Log.d("rafeek", "setOnEditorAction: e7na hena")
    setOnEditorActionListener { _, i, _ ->
        if (i == EditorInfo.IME_ACTION_DONE) {
            when (this) {
                profileFragmentEditTextDisplayName -> {
                    val view = this.parent.parent as LinearLayout
                    val fab = view.findViewById<FloatingActionButton>(R.id.profileFragmentFab1)
                    textView.visibility = View.VISIBLE
                    textView.text = this.text.toString()
                    this.visibility = View.GONE
                    fab.visibility = View.VISIBLE

                    Log.d("rafeek", "setOnEditorAction: e7na jena 33")
                    return@setOnEditorActionListener true
                }
                profileFragmentEditTextNumber -> {
                    val view = this.parent.parent as LinearLayout
                    val fab = view.findViewById<FloatingActionButton>(R.id.profileFragmentFab3)
                    this.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                    textView.text = this.text.toString()
                    fab.visibility = View.VISIBLE
                    Log.d("rafeek", "setOnEditorAction: e7na jena 33")
                    return@setOnEditorActionListener true
                }
                profileFragmentEditEmail -> {
                    Log.d("rafeek", "setOnEditorAction: e7na jena 33")
                    val view = this.parent.parent as LinearLayout
                    val fab = view.findViewById<FloatingActionButton>(R.id.profileFragmentFab2)
                    textView.text = this.text.toString()
                    this.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    return@setOnEditorActionListener true
                }
                profileFragmentEditTextPassword -> {
                    val view = this.parent.parent.parent as LinearLayout
                    val fab = view.findViewById<FloatingActionButton>(R.id.profileFragmentFab4)
                    val parent: TextInputLayout = this.parent.parent as TextInputLayout
                    textView.text = this.text.toString()
                    parent.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    return@setOnEditorActionListener true
                }

            }
        }
        false
    }
}
