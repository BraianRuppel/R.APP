package com.example.rapp.ui.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.rapp.util.applyInsetsWithPadding
import com.example.rapp.MainRapp
import com.example.rapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class LandingFragment : Fragment() {

    private lateinit var viewModel: LandingViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    // Calendario
    private lateinit var weekPagerAdapter: WeekPagerAdapter
    private lateinit var viewPagerCalendar: ViewPager2
    private lateinit var tvMonthYear: TextView
    private lateinit var ivExpandCollapse: ImageView

    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))

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
        setupCalendar(view)
        setupViews(view)
        observeData()
    }

    private fun setupViewModel() {
        val app = requireActivity().application as MainRapp
        val factory = LandingViewModelFactory(app.itemRepository)
        viewModel = ViewModelProvider(this, factory)[LandingViewModel::class.java]
    }

    private fun setupCalendar(view: View) {
        val calendarView = view.findViewById<View>(R.id.calendarView)

        viewPagerCalendar = calendarView.findViewById(R.id.viewPagerCalendar)
        tvMonthYear = calendarView.findViewById(R.id.tvMonthYear)
        ivExpandCollapse = calendarView.findViewById(R.id.ivExpandCollapse)
        val ivPrevious = calendarView.findViewById<ImageView>(R.id.ivPrevious)
        val ivNext = calendarView.findViewById<ImageView>(R.id.ivNext)

        // Configurar adapter
        weekPagerAdapter = WeekPagerAdapter { day ->
            Toast.makeText(requireContext(), "Seleccionado: ${day.date}", Toast.LENGTH_SHORT).show()
            // TODO: Manejar selección de día
        }

        viewPagerCalendar.adapter = weekPagerAdapter
        viewPagerCalendar.setCurrentItem(WeekPagerAdapter.START_POSITION, false)

        // Actualizar título cuando cambia la página
        viewPagerCalendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateMonthYearTitle(position)
            }
        })

        // Actualizar título inicial
        updateMonthYearTitle(WeekPagerAdapter.START_POSITION)

        // Botones de navegación
        ivPrevious.setOnClickListener {
            viewPagerCalendar.currentItem = viewPagerCalendar.currentItem - 1
        }

        ivNext.setOnClickListener {
            viewPagerCalendar.currentItem = viewPagerCalendar.currentItem + 1
        }

        // Botón expandir/colapsar
        ivExpandCollapse.setOnClickListener {
            toggleCalendarView()
        }
    }

    private fun toggleCalendarView() {
        val isCurrentlyMonthView = weekPagerAdapter.isMonthView()
        weekPagerAdapter.setMonthView(!isCurrentlyMonthView)

        // Cambiar icono
        ivExpandCollapse.setImageResource(
            if (!isCurrentlyMonthView) R.drawable.ic_expand_less
            else R.drawable.ic_expand_more
        )

        // Ajustar altura del ViewPager
        val params = viewPagerCalendar.layoutParams
        params.height = if (!isCurrentlyMonthView) {
            // Vista mensual: 6 filas * 44dp
            (44 * 6 * resources.displayMetrics.density).toInt()
        } else {
            // Vista semanal: 1 fila * 44dp
            (44 * resources.displayMetrics.density).toInt()
        }
        viewPagerCalendar.layoutParams = params
    }

    private fun updateMonthYearTitle(position: Int) {
        val date = weekPagerAdapter.getDateForPosition(position)
        tvMonthYear.text = date.format(monthYearFormatter).replaceFirstChar { it.uppercase() }
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