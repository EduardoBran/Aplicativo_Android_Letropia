package com.luizeduardobrandao.letropia.view.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.luizeduardobrandao.letropia.R

class SpinnerAdapter(
    context: Context,
    items: List<String>
): ArrayAdapter<String>(
    context,
    R.layout.item_spinner,
    items
){

    // sobrescreva o getFilter() para sempre retornar a lista completa:
    private val unfiltered = items.toList()

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = unfiltered
                count = unfiltered.size
            }
        }
        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            @Suppress("UNCHECKED_CAST")
            addAll(results.values as List<String>)
            notifyDataSetChanged()
        }
    }
}