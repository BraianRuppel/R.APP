package com.example.rapp.ui.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.util.applyInsetsWithPadding
import com.example.rapp.MainRapp
import com.example.rapp.R

class LandingFragment : Fragment() {

    private lateinit var viewModel: LandingViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.applyInsetsWithPadding()

        setupViewModel()
        setupViews(view)
        observeData()
    }

    private fun setupViewModel() {
        val app = requireActivity().application as MainRapp
        val factory = LandingViewModelFactory(app.itemRepository)
        viewModel = ViewModelProvider(this, factory)[LandingViewModel::class.java]
    }

    private fun setupViews(view: View) {
        // Botón para ir a lista de items
        view.findViewById<Button>(R.id.btnItemList).setOnClickListener {
            findNavController().navigate(R.id.action_landing_to_itemList)
        }

        // RecyclerView de favoritos
        val rvFavorites = view.findViewById<RecyclerView>(R.id.rvFavorites)
        favoriteAdapter = FavoriteAdapter { item ->
            // Al hacer click en un favorito, navegar a la lista
            findNavController().navigate(R.id.action_landing_to_itemList)
        }
        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        rvFavorites.adapter = favoriteAdapter
    }

    private fun observeData() {
        val tvFavoritesTitle = view?.findViewById<TextView>(R.id.tvFavoritesTitle)
        val tvEmptyFavorites = view?.findViewById<TextView>(R.id.tvEmptyFavorites)
        val rvFavorites = view?.findViewById<RecyclerView>(R.id.rvFavorites)

        viewModel.favoriteItems.observe(viewLifecycleOwner) { favorites ->
            favoriteAdapter.submitList(favorites)

            if (favorites.isEmpty()) {
                tvFavoritesTitle?.visibility = View.GONE
                tvEmptyFavorites?.visibility = View.VISIBLE
                rvFavorites?.visibility = View.GONE
            } else {
                tvFavoritesTitle?.visibility = View.VISIBLE
                tvEmptyFavorites?.visibility = View.GONE
                rvFavorites?.visibility = View.VISIBLE
            }
        }
    }
}