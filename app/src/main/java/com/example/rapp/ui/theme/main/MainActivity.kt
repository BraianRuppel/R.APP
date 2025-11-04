package com.example.rapp.ui.theme.main

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.MyApp
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.model.Item

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val app = application as MyApp
        val factory = MainViewModelFactory(app.repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val input = findViewById<EditText>(R.id.inputText)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        adapter = MainAdapter(emptyList()) { item ->
            viewModel.removeItem(item)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            val text = input.text.toString()
            if (text.isNotBlank()) {
                viewModel.addItem(text)
                input.text.clear()
            }
        }

        viewModel.items.observe(this) { list ->
            adapter.updateData(list)
        }
    }
}
