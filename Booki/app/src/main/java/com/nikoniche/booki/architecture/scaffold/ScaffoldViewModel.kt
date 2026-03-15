package com.nikoniche.booki.architecture.scaffold

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ScaffoldViewModel: ViewModel() {
    var menuOpened: MutableState<Boolean> = mutableStateOf(false)
}