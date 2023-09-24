package com.example.memorygame

class Tabuleiro(val linhas: Int, val colunas: Int) {
    val cartas: Array<Array<Carta>> = Array(linhas) { Array(colunas) { Carta("") } }
    fun getCarta(linha: Int, coluna: Int): Carta {
        return cartas[linha][coluna]
    }

    fun todasCartasViradas(): Boolean {
        for (linha in 0 until linhas) {
            for (coluna in 0 until colunas) {
                if (!cartas[linha][coluna].foiVirada) {
                    return false
                }
            }
        }
        return true
    }
}