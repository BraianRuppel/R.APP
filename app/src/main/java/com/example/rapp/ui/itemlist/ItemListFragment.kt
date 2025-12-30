package com.example.rapp.ui.itemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.MainRapp
import com.example.rapp.R

class ItemListFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ItemListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cambios clave:
        // - "application" → "requireActivity().application"
        // - "this" (como LifecycleOwner) → "viewLifecycleOwner"
        // - "this" (como Context) → "requireContext()"
        // - "findViewById" → "view.findViewById"

        val app = requireActivity().application as MainRapp
        val factory = ItemListViewModelFactory(app.itemRepository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val input = view.findViewById<EditText>(R.id.inputText)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        adapter = ItemListAdapter(emptyList()) { item ->
            viewModel.removeItem(item)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            val text = input.text.toString()
            if (text.isNotBlank()) {
                viewModel.addItem(text)
                input.text.clear()
            }
        }

        // Importante: usar viewLifecycleOwner en lugar de "this"
        viewModel.items.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }
    }
}
