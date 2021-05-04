package com.magda.mamasbiz.main.utils

import com.magda.mamasbiz.main.data.entity.Products

sealed class Results <out T :Any> {
    data class Success <out T: Any>(val data: T) :Results<T> ()
    data class Error (val error: String) :Results<Nothing> ()
}