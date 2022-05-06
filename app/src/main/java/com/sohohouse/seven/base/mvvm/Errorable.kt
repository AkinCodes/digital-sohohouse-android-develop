package com.sohohouse.seven.base.mvvm

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.sohohouse.seven.common.views.ReloadableErrorStateView

interface Errorable {

    interface ViewModel {

        val error: LiveData<String>

        fun showError(message: String = "")

    }

    interface View {

        val viewModel: ViewModel

        val errorStateView: ReloadableErrorStateView?
            get() = null

        fun observeErrorState(owner: LifecycleOwner, onReloadClicked: () -> Unit = {}) {
            viewModel.error.observe(owner) {
                errorStateView?.visibility = VISIBLE
                errorStateView?.reloadClicks {
                    errorStateView?.visibility = GONE
                    onReloadClicked()
                }
            }
        }
    }

    class ViewModelImpl : ViewModel {

        private val _error = LiveEvent<String>()

        override val error: LiveData<String>
            get() = _error

        override fun showError(message: String) {
            _error.postValue(message)
        }

    }
}