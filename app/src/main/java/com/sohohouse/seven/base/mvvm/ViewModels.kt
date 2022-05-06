package com.sohohouse.seven.base.mvvm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner

inline fun <reified T : ViewModel> Fragment.fragmentViewModel(
    crossinline creator: (SavedStateHandle) -> T
): Lazy<T> {
    return createViewModelLazy(
        viewModelClass = T::class,
        storeProducer = { viewModelStore },
        factoryProducer = {
            createAbstractSavedStateViewModelFactory(
                arguments = arguments ?: Bundle(),
                creator = creator
            )
        })
}

inline fun <reified T : ViewModel> SavedStateRegistryOwner.createAbstractSavedStateViewModelFactory(
    arguments: Bundle,
    crossinline creator: (SavedStateHandle) -> T
): ViewModelProvider.Factory {
    return object : AbstractSavedStateViewModelFactory(this, arguments) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T = creator(handle) as T
    }
}

inline fun <reified T : ViewModel> AppCompatActivity.activityViewModel(
    crossinline creator: () -> T
): Lazy<T> {
    return viewModels(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return creator() as T
                }

            }
        })
}