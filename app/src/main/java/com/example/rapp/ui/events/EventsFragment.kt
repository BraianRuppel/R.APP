package com.example.rapp.ui.events

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.MainRapp
import com.example.rapp.R
import com.example.rapp.data.model.Event
import com.example.rapp.data.model.EventType
import com.example.rapp.util.applyInsetsWithPadding
import com.example.rapp.util.applyBottomMargin
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventsFragment : Fragment() {

    private lateinit var viewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter
    private lateinit var rvEvents: RecyclerView
    private lateinit var emptyState: View
    private lateinit var tabLayout: TabLayout

    private var selectedDate: LocalDate = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.applyInsetsWithPadding()
        view.findViewById<FloatingActionButton>(R.id.fabAddEvent).applyBottomMargin()

        setupViewModel()
        setupViews(view)
        setupTabs(view)
        setupRecyclerView()
        observeData()
    }

    private fun setupViewModel() {
        val app = requireActivity().application as MainRapp
        val factory = EventViewModelFactory(app.eventRepository)
        viewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]
    }

    private fun setupViews(view: View) {
        // Toolbar
        view.findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        rvEvents = view.findViewById(R.id.rvEvents)
        emptyState = view.findViewById(R.id.emptyState)

        // FAB
        view.findViewById<FloatingActionButton>(R.id.fabAddEvent).setOnClickListener {
            showAddEventDialog()
        }
    }

    private fun setupTabs(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText("Todos"))
        tabLayout.addTab(tabLayout.newTab().setText("Próximos"))
        tabLayout.addTab(tabLayout.newTab().setText("Cumpleaños"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterEvents(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(
            onEventClick = { event ->
                showEventDetails(event)
            },
            onEventLongClick = { event ->
                showEventOptions(event)
            }
        )

        rvEvents.layoutManager = LinearLayoutManager(requireContext())
        rvEvents.adapter = eventAdapter
    }

    private fun observeData() {
        viewModel.allEvents.observe(viewLifecycleOwner) { events ->
            filterEvents(tabLayout.selectedTabPosition, events)
        }
    }

    private fun filterEvents(tabPosition: Int, events: List<Event>? = null) {
        val allEvents = events ?: viewModel.allEvents.value ?: emptyList()
        val today = LocalDate.now()

        val filteredEvents = when (tabPosition) {
            0 -> allEvents // Todos
            1 -> allEvents.filter { event ->
                val eventDate = if (event.isRepeatingYearly) getNextOccurrence(event.date) else event.date
                !eventDate.isBefore(today) && eventDate.isBefore(today.plusDays(31))
            }.sortedBy { if (it.isRepeatingYearly) getNextOccurrence(it.date) else it.date }
            2 -> allEvents.filter { it.type == EventType.BIRTHDAY }
            else -> allEvents
        }

        eventAdapter.submitList(filteredEvents)

        emptyState.visibility = if (filteredEvents.isEmpty()) View.VISIBLE else View.GONE
        rvEvents.visibility = if (filteredEvents.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun getNextOccurrence(date: LocalDate): LocalDate {
        val today = LocalDate.now()
        var nextDate = date.withYear(today.year)
        if (nextDate.isBefore(today)) {
            nextDate = nextDate.plusYears(1)
        }
        return nextDate
    }

    private fun showAddEventDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_event, null)

        val etTitle = dialogView.findViewById<EditText>(R.id.etEventTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etEventDescription)
        val etDate = dialogView.findViewById<EditText>(R.id.etEventDate)
        val rgEventType = dialogView.findViewById<RadioGroup>(R.id.rgEventType)
        val switchRepeat = dialogView.findViewById<SwitchMaterial>(R.id.switchRepeatYearly)

        selectedDate = LocalDate.now()
        etDate.setText(selectedDate.format(dateFormatter))

        // Variable para trackear si es cumpleaños
        var isBirthdaySelected = false

        etDate.setOnClickListener {
            showDatePicker(forBirthday = isBirthdaySelected) { date ->
                selectedDate = date
                etDate.setText(date.format(dateFormatter))
            }
        }

        // Auto-activar repetición para cumpleaños y aniversarios
        rgEventType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbBirthday -> {
                    switchRepeat.isChecked = true
                    isBirthdaySelected = true
                    // Limpiar fecha para que seleccione de nuevo
                    etDate.setText("")
                    selectedDate = LocalDate.now().minusYears(25) // Sugerencia inicial
                }
                R.id.rbAnniversary -> {
                    switchRepeat.isChecked = true
                    isBirthdaySelected = false
                }
                R.id.rbOneTime -> {
                    switchRepeat.isChecked = false
                    isBirthdaySelected = false
                }
                R.id.rbHoliday -> {
                    isBirthdaySelected = false
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Nuevo Evento")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val title = etTitle.text.toString()
                val description = etDescription.text.toString()
                val type = when (rgEventType.checkedRadioButtonId) {
                    R.id.rbBirthday -> EventType.BIRTHDAY
                    R.id.rbAnniversary -> EventType.ANNIVERSARY
                    R.id.rbHoliday -> EventType.HOLIDAY
                    else -> EventType.ONE_TIME
                }
                val isRepeating = switchRepeat.isChecked
                val color = when (type) {
                    EventType.BIRTHDAY -> "#E91E63"
                    EventType.ANNIVERSARY -> "#F44336"
                    EventType.HOLIDAY -> "#4CAF50"
                    EventType.ONE_TIME -> "#2196F3"
                }

                if (title.isNotBlank()) {
                    viewModel.addEvent(title, description, selectedDate, type, isRepeating, color)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDatePicker(forBirthday: Boolean = false, onDateSelected: (LocalDate) -> Unit) {
        val constraintsBuilder = CalendarConstraints.Builder()

        if (forBirthday) {
            // Para cumpleaños: permitir solo fechas pasadas
            constraintsBuilder.setValidator(DateValidatorPointBackward.now())
            // Empezar en 1970 para facilitar navegación
            val startMillis = LocalDate.of(1940, 1, 1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            constraintsBuilder.setStart(startMillis)
        }

        val currentMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(if (forBirthday) "Fecha de nacimiento" else "Seleccionar fecha")
            .setSelection(currentMillis)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val date = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            onDateSelected(date)
        }

        picker.show(parentFragmentManager, "datePicker")
    }

    private fun showEventDetails(event: Event) {
        val nextOccurrence = if (event.isRepeatingYearly) getNextOccurrence(event.date) else event.date

        AlertDialog.Builder(requireContext())
            .setTitle(event.title)
            .setMessage(buildString {
                append("📅 ${event.date.format(dateFormatter)}\n")

                // 🆕 Mostrar edad para cumpleaños
                if (event.type == EventType.BIRTHDAY && event.isRepeatingYearly) {
                    val age = nextOccurrence.year - event.date.year
                    append("🎂 Cumple $age años\n")
                } else if (event.type == EventType.ANNIVERSARY && event.isRepeatingYearly) {
                    val years = nextOccurrence.year - event.date.year
                    append("❤️ $years años juntos\n")
                }

                if (event.description.isNotBlank()) {
                    append("\n${event.description}\n")
                }
                append("\nTipo: ${getEventTypeName(event.type)}")
                if (event.isRepeatingYearly) {
                    append("\n🔄 Se repite cada año")
                }
            })
            .setPositiveButton("Cerrar", null)
            .setNegativeButton("Eliminar") { _, _ ->
                showDeleteConfirmation(event)
            }
            .show()
    }

    private fun showEventOptions(event: Event) {
        val options = arrayOf("Ver detalles", "Eliminar")
        AlertDialog.Builder(requireContext())
            .setTitle(event.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEventDetails(event)
                    1 -> showDeleteConfirmation(event)
                }
            }
            .show()
    }

    private fun showDeleteConfirmation(event: Event) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar evento")
            .setMessage("¿Estás seguro de eliminar \"${event.title}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteEvent(event)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun getEventTypeName(type: EventType): String {
        return when (type) {
            EventType.BIRTHDAY -> "🎂 Cumpleaños"
            EventType.ANNIVERSARY -> "❤️ Aniversario"
            EventType.HOLIDAY -> "🎉 Festividad"
            EventType.ONE_TIME -> "📅 Evento único"
        }
    }
}