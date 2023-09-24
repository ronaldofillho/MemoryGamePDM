package com.example.memorygame

import android.os.Bundle
import android.os.Handler
import android.content.Intent
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var jogo: Jogo
    private lateinit var gridLayout: GridLayout
    private lateinit var tentativasTextView: TextView
    private var cartaVirada1: Pair<Int, Int>? = null
    private var cartaVirada2: Pair<Int, Int>? = null
    private var bloquearToque: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        tentativasTextView = findViewById(R.id.tentativasTextView)

        // Inicialize o jogo com suas configurações
        jogo = Jogo(
            paresDeImagens = listOf("imagem1", "imagem2", "imagem3", "imagem4", "imagem5", "imagem6", "imagem7", "imagem8"),
            maxTentativas = 10,
            tempoExibicaoImagens = 2000L // 2 segundos
        )

        // Configure o grid com as cartas
        setupTabuleiro()

        // Atualize a interface com o número inicial de tentativas
        atualizarTentativas()

        val reiniciarButton = findViewById<Button>(R.id.reiniciarButton)
        reiniciarButton.setOnClickListener {
            reiniciarJogo()
        }
    }

    private fun reiniciarJogo() {
        // Reinicie o jogo com as configurações originais
        jogo = Jogo(
            paresDeImagens = listOf("imagem1", "imagem2", "imagem3", "imagem4", "imagem5", "imagem6", "imagem7", "imagem8"),
            maxTentativas = 10,
            tempoExibicaoImagens = 2000L
        )

        // Limpe a interface gráfica e configure o tabuleiro
        gridLayout.removeAllViews()
        setupTabuleiro()

        // Redefina as variáveis de estado do jogo
        cartaVirada1 = null
        cartaVirada2 = null
        bloquearToque = false

        // Atualize a interface com o número inicial de tentativas
        atualizarTentativas()

        fun reiniciarJogo(view: View) {
            reiniciarJogo()
        }
    }


    private fun setupTabuleiro() {
        val tabuleiro = jogo.getTabuleiro()

        for (linha in 0 until tabuleiro.linhas) {
            for (coluna in 0 until tabuleiro.colunas) {
                val carta = tabuleiro.getCarta(linha, coluna)
                val imageView = ImageView(this)
                imageView.setImageResource(R.drawable.verso_carta) // Imagem de fundo da carta
                imageView.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                imageView.setOnClickListener { onCartaClicada(linha, coluna) }
                gridLayout.addView(imageView)
            }
        }
    }

    private fun onCartaClicada(linha: Int, coluna: Int) {
        if (bloquearToque) return

        if (jogo.virarCarta(linha, coluna)) {
            // A carta foi virada com sucesso
            atualizarInterfaceCarta(linha, coluna)

            if (cartaVirada1 == null) {
                // Primeira carta virada
                cartaVirada1 = Pair(linha, coluna)
            } else {
                // Segunda carta virada
                cartaVirada2 = Pair(linha, coluna)
                bloquearToque = true

                // Verifica se as duas cartas formam um par
                if (jogo.verificarPar(cartaVirada1!!.first, cartaVirada1!!.second, cartaVirada2!!.first, cartaVirada2!!.second)) {
                    // As cartas formam um par
                    cartaVirada1 = null
                    cartaVirada2 = null
                    bloquearToque = false

                    // Verifica a vitória aqui
                    if (jogo.verificarVitoria()) {
                        val intent = Intent(this, TelaVenceu::class.java)
                        startActivity(intent)
                    }
                } else {
                    // As cartas não formam um par, então as esconde após um breve período
                    val handler = android.os.Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        esconderCartas()
                    }, 1000L) // 1 segundo
                }
            }
        }
    }



    private fun atualizarInterfaceCarta(linha: Int, coluna: Int) {
        val imageView = gridLayout.getChildAt(linha * jogo.getTabuleiro().colunas + coluna) as ImageView
        val carta = jogo.getTabuleiro().getCarta(linha, coluna)

        if (carta.foiVirada) {
            // Se a carta foi virada, exibe a imagem correspondente ao seu conteúdo
            val imagemResource = resources.getIdentifier(carta.imagem, "drawable", packageName)
            imageView.setImageResource(imagemResource)
        } else {
            // Se a carta não foi virada, exibe a imagem do verso das cartas que não formam pares
            imageView.setImageResource(R.drawable.verso_carta2)
        }
    }


    private fun esconderCartas() {
        if (cartaVirada1 != null && cartaVirada2 != null) {
            val linha1 = cartaVirada1!!.first
            val coluna1 = cartaVirada1!!.second
            val linha2 = cartaVirada2!!.first
            val coluna2 = cartaVirada2!!.second

            val carta1 = jogo.getTabuleiro().getCarta(linha1, coluna1)
            val carta2 = jogo.getTabuleiro().getCarta(linha2, coluna2)

            if (!jogo.verificarPar(linha1, coluna1, linha2, coluna2)) {
                val handler = android.os.Handler(Looper.getMainLooper())
                handler.postDelayed({
                    carta1.foiVirada = false
                    carta2.foiVirada = false
                    atualizarInterfaceCarta(linha1, coluna1)
                    atualizarInterfaceCarta(linha2, coluna2)
                    cartaVirada1 = null
                    cartaVirada2 = null
                    bloquearToque = false

                    jogo.diminuirTentativa()
                    atualizarTentativas()

                    if (jogo.verificarVitoria()) {
                        val intent = Intent(this, TelaVenceu::class.java)
                        startActivity(intent)
                    } else if (jogo.tentativasRestantes <= 0) {
                        val intent = Intent(this, TelaPerdeu::class.java)
                        startActivity(intent)
                        // Se o jogador perdeu, reinicie automaticamente após um atraso
                        handler.postDelayed({
                            reiniciarJogo()
                        }, 2000L) // 2 segundos (ajuste conforme necessário)
                    }
                }, 1000L) // 1 segundo
            } else {
                cartaVirada1 = null
                cartaVirada2 = null
                bloquearToque = false
            }
        }
    }

    private fun atualizarTentativas() {
        val tentativas = jogo.tentativasRestantes
        tentativasTextView.text = "Você tem $tentativas tentativas!"
        if (jogo.tentativasRestantes <= 0) {
            tentativasTextView.text = "Você perdeu!"
            bloquearToque = true
        }
    }
}