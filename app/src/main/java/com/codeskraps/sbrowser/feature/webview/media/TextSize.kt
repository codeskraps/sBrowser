package com.codeskraps.sbrowser.feature.webview.media

enum class TextSize(val size: Int, val ui: String) {
    Tiny(50, "Tiny"),
    Small(75, "Small"),
    Normal(100, "Normal"),
    Large(150, "Large"),
    Huge(200, "Huge");

    companion object {

        fun displayArray() = arrayOf(Tiny.ui, Small.ui, Normal.ui, Large.ui, Huge.ui)

        fun parse(size: Int) = when (size) {
            Tiny.size -> Tiny
            Small.size -> Small
            Large.size -> Large
            Huge.size -> Huge
            else -> Normal
        }

        fun parse(name: String) = when (name) {
            Tiny.ui -> Tiny
            Small.ui -> Small
            Large.ui -> Large
            Huge.ui -> Huge
            else -> Normal
        }
    }
}