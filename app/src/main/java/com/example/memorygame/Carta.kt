package com.example.memorygame

class Carta(imagem:String) {
    val imagem:String = imagem
    var foiVirada: Boolean = false

    fun virar() {
        foiVirada = true
    }
}