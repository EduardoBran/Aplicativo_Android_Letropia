package com.luizeduardobrandao.letropia.view.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.luizeduardobrandao.letropia.R


// * Adapter para exibir um EditText para cada letra de uma palavra.
// * @param length Número de letras (itens) a exibir.
// * @param onLettersChanged Callback acionado sempre que o usuário digita ou apaga uma letra,
// * informando a lista completa de caracteres (pode conter null para campos vazios).
class LetterAdapter(
    private val length: Int,
    private val onLettersChanged: (List<Char?>) -> Unit,
    private val onLastLetterEntered: () -> Unit    // callback para fechar teclado
): RecyclerView.Adapter<LetterAdapter.LetterVH>() {

    // Lista interna que mantém o valor atual de cada letra (null se o campo estiver vazio)
    private val letters = MutableList<Char?>(length) { null }

    private var recyclerView: RecyclerView? = null

    // Sobrescreve para capturar o RecyclerView “de verdade”
    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        super.onAttachedToRecyclerView(rv)
        recyclerView = rv
    }
    override fun onDetachedFromRecyclerView(rv: RecyclerView) {
        super.onDetachedFromRecyclerView(rv)
        recyclerView = null
    }

    // ViewHolder que encapsula um único EditText para digitação de uma letra.
    inner class LetterVH(private val editText: EditText): RecyclerView.ViewHolder(editText){

        // Referência ao TextWatcher atual, para removê-lo antes de adicionar um novo
        private var watcher: TextWatcher? = null


        // * Configura o EditText para a posição especificada:
        // * - Remove o TextWatcher antigo, se existir.
        // * - Define o texto de acordo com o valor em `letters[pos]`.
        // * - Cria e adiciona um novo TextWatcher para escutar alterações.
        fun bind(pos: Int){

            // Remove o listener anterior para evitar chamadas duplicadas
            watcher?.let { editText.removeTextChangedListener(it) }

            // Preenche o campo com a letra armazenada ou vazio
            editText.setText(letters[pos]?.toString() ?: "")

            // Cria um TextWatcher para capturar mudanças no texto
            watcher = object: TextWatcher{

                // Chamado antes de uma mudança ser aplicada no texto.
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // vazio pois não precisamos agir antes da mudança
                }


                // Chamado à medida que o texto está mudando.
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // vazio pois processamos a mudança somente em afterTextChanged
                }


                // Chamado após qualquer mudança no texto do EditText.
                // Atualiza a lista interna, dispara callback e move o foco se necessário.
                override fun afterTextChanged(s: Editable?) {

                    // Armazena o primeiro caractere ou null
                    letters[pos] = s?.firstOrNull()

                    // Notifica sobre a mudança de todas as letras
                    onLettersChanged(letters)

                    // Se digitou exatamente 1 caractere e não estamos no último item,
                    // avança o foco para o próximo EditText
                    if (s?.length == 1) {
                        if (pos < length - 1) {
                            // post garante que o próximo ViewHolder já esteja criado
                            recyclerView?.post {
                                val vh = recyclerView
                                    ?.findViewHolderForAdapterPosition(pos + 1)
                                        as? LetterVH
                                vh?.editText?.requestFocus()
                            }
                        } else {
                            // último dígito: notifica a Activity para fechar o teclado
                            onLastLetterEntered()
                        }
                    }
                }
            }

            // Adiciona o TextWatcher configurado ao EditText
            editText.addTextChangedListener(watcher)
        }
    }

    // Infla o layout do item (um EditText simples)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterVH {
        val editText = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_letter, parent, false) as EditText
        return LetterVH(editText)
    }

    // Quantidade de EditText preenchido
    override fun getItemCount(): Int {
        return length
    }

    // Vincula os dados e listeners ao ViewHolder desta posição
    override fun onBindViewHolder(holder: LetterVH, position: Int) {
        holder.bind(position)
    }

    // Limpa todas as letras de todos os campos:
    //   - Reseta a lista interna para nulls
    //   - Notifica o RecyclerView para atualizar todos os itens
    fun setLetters(newLetters: List<Char?>) {
        for (i in letters.indices) {
            letters[i] = newLetters.getOrNull(i)
        }
        notifyDataSetChanged()
    }
}