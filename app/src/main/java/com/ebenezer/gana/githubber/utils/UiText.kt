package com.ebenezer.gana.githubber.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText{
    class StringResource(
        @StringRes val resId:Int
    ): UiText()

    fun asString(context: Context):String{
        return when(this){
            is StringResource -> context.resources.getString(resId)
        }
    }
}
