package com.sohohouse.seven.profile.edit

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.focusAndShowKeyboard
import com.sohohouse.seven.common.extensions.hideKeyboard
import com.sohohouse.seven.common.extensions.nullIfBlank
import com.sohohouse.seven.common.form.FormRowType.*
import com.sohohouse.seven.common.views.TextWatcherAdapter
import com.sohohouse.seven.databinding.ItemEditProfileQuestionBinding

class EditProfileQuestionViewHolder(
    parent: ViewGroup,
    private val listener: EditProfileListener,
    private val binding: ItemEditProfileQuestionBinding =
        ItemEditProfileQuestionBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit_profile_question, parent, false)
        )
) :
    RecyclerView.ViewHolder(binding.root) {

    private val textWatcher = QuestionTextWatcher()
    private val focusChangeListener = QuestionFocusChangeListener()

    init {
        with(binding) {
            answerInput.addTextChangedListener(textWatcher)
            answerInput.onFocusChangeListener = focusChangeListener
            root.setOnClickListener {
                answerInput.focusAndShowKeyboard()
            }
        }
    }

    fun bind(item: EditProfileAdapterItem.Question, changePayload: Any?) {
        when (changePayload) {
            is EditProfileViewModel.RequestFocus -> {
                binding.answerInput.requestFocus()
                return
            }
            is EditProfileViewModel.ClearFocus -> {
                with(binding.answerInput) {
                    clearFocus()
                    hideKeyboard()
                    return
                }
            }
        }

        with(binding) {
            textWatcher.question = item
            focusChangeListener.question = item
            questionLayout.hint = item.question.question
            answerInput.setText(item.question.answer)

            root.setBackgroundResource(
                when (item.rowType) {
                    TOP_ROW -> R.drawable.form_top_row
                    MIDDLE_ROW -> R.drawable.form_middle_row
                    BOTTOM_ROW -> R.drawable.form_bottom_row
                    SINGULAR_ROW -> R.drawable.form_background
                    NONE -> 0
                }
            )
        }
    }

    inner class QuestionTextWatcher : TextWatcherAdapter() {
        var question: EditProfileAdapterItem.Question? = null
        override fun afterTextChanged(s: Editable?) {
            question?.let {
                if (it.question.answer.nullIfBlank() != s?.toString().nullIfBlank()) {
                    listener.onQuestionAnswerChange(it)
                }
                it.question.answer = s?.toString()
            }
        }
    }

    inner class QuestionFocusChangeListener : View.OnFocusChangeListener {
        var question: EditProfileAdapterItem.Question? = null
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            question?.let { if (hasFocus) listener.onUserFocusQuestion(it) }
        }
    }

}
