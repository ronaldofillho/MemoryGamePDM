package com.example.memorygame

class Jogo {
    private val tabuleiro: Tabuleiro
    private val paresDeImagens: List<String>
    private val maxTentativas: Int
    var tentativasRestantes: Int
    val tempoExibicaoImagens: Long

    constructor(paresDeImagens: List<String>, maxTentativas: Int, tempoExibicaoImagens: Long) {
        this.paresDeImagens = paresDeImagens
        this.maxTentativas = maxTentativas
        this.tempoExibicaoImagens = tempoExibicaoImagens
        tentativasRestantes = maxTentativas
        tabuleiro = Tabuleiro(4,4)
        iniciarJogo()
    }

    private fun iniciarJogo() {
        val imagensEmbaralhadas = (paresDeImagens + paresDeImagens).shuffled()
        for (linha in 0 until tabuleiro.linhas) {
            for (coluna in 0 until tabuleiro.colunas) {
                tabuleiro.cartas[linha][coluna] = Carta(imagensEmbaralhadas[linha * tabuleiro.colunas + coluna])
            }
        }
    }

    fun virarCarta(linha: Int, coluna: Int): Boolean {
        val carta = tabuleiro.getCarta(linha, coluna)
        if (!carta.foiVirada) {
            carta.virar()
            return true
        }
        return false
    }

    fun verificarPar(linha1: Int, coluna1: Int, linha2: Int, coluna2: Int): Boolean {
        val carta1 = tabuleiro.getCarta(linha1, coluna1)
        val carta2 = tabuleiro.getCarta(linha2, coluna2)

        if (!carta1.foiVirada || !carta2.foiVirada) {
            return false
        }
        return carta1.imagem == carta2.imagem
    }

    fun verificarVitoria(): Boolean {
        return tabuleiro.todasCartasViradas() && tentativasRestantes >= 0
    }

    fun diminuirTentativa() {
        tentativasRestantes--
    }

    fun getTabuleiro(): Tabuleiro {
        return tabuleiro
    }
}