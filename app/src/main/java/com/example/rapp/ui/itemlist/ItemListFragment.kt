package com.example.rapp.ui.itemlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.MainRapp
import com.example.rapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.rapp.util.applyInsetsWithPadding
import com.example.rapp.util.applyBottomMargin

class ItemListFragment : Fragment() {

    private lateinit var viewModel: ItemListViewModel
    private lateinit var groupAdapter: GroupAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.rvGroups).applyInsetsWithPadding()
        view.findViewById<FloatingActionButton>(R.id.fabAddGroup).applyBottomMargin()

        setupViewModel()
        setupViews(view)
        setupRecyclerView()
        observeData()
    }

    private fun setupViewModel() {
        val app = requireActivity().application as MainRapp
        val factory = ItemListViewModelFactory(
            app.groupRepository,
            app.itemRepository
        )
        viewModel = ViewModelProvider(this, factory)[ItemListViewModel::class.java]
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.rvGroups)
        emptyStateView = view.findViewById(R.id.tvEmptyState)

        // Botón para agregar grupo
        view.findViewById<FloatingActionButton>(R.id.fabAddGroup).setOnClickListener {
            showAddGroupDialog()
        }
    }

    private fun setupRecyclerView() {
        groupAdapter = GroupAdapter(
            groups = mutableListOf(),
            onGroupToggle = { groupId, isExpanded ->
                viewModel.toggleGroupExpanded(groupId, isExpanded)
            },
            onGroupDelete = { group ->
                showDeleteGroupConfirmation(group)
            },
            onItemDelete = { item ->
                showDeleteItemConfirmation(item)
            },
            onItemsReordered = { items ->
                viewModel.updateItemOrders(items)
            },
            onGroupsReordered = { groups ->
                viewModel.updateGroupOrders(groups)
            },
            onItemMovedToGroup = { itemId, newGroupId, newOrder ->
                viewModel.moveItemToGroup(itemId, newGroupId, newOrder)
            },
            onAddItemClick = { groupId, groupName ->
                showAddItemDialog(groupId, groupName)
            },
            onFavoriteToggle = { item ->
                viewModel.toggleFavorite(item)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = groupAdapter

        // ItemTouchHelper para drag & drop de grupos
        val groupTouchCallback = GroupTouchCallback(groupAdapter) { groups ->
            viewModel.updateGroupOrders(groups)
        }
        val groupTouchHelper = ItemTouchHelper(groupTouchCallback)
        groupTouchHelper.attachToRecyclerView(recyclerView)
        groupAdapter.setItemTouchHelper(groupTouchHelper)
    }

    private fun observeData() {
        viewModel.groupsWithItems.observe(viewLifecycleOwner) { groups ->
            groupAdapter.updateData(groups)

            // Mostrar/ocultar estado vacío
            if (groups.isEmpty()) {
                emptyStateView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyStateView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    // ==================== DIÁLOGOS ====================

    private fun showAddGroupDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_group, null)

        val etGroupName = dialogView.findViewById<EditText>(R.id.etGroupName)

        AlertDialog.Builder(requireContext())
            .setTitle("Nuevo Grupo")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                val name = etGroupName.text.toString()
                if (name.isNotBlank()) {
                    viewModel.addGroup(name)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddItemDialog(groupId: Long, groupName: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_item, null)

        val etItemText = dialogView.findViewById<EditText>(R.id.etItemText)

        AlertDialog.Builder(requireContext())
            .setTitle("Nuevo Item en \"$groupName\"")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val text = etItemText.text.toString()
                if (text.isNotBlank()) {
                    viewModel.addItem(text, groupId)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteGroupConfirmation(group: com.example.rapp.data.model.Group) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Grupo")
            .setMessage("¿Estás seguro de eliminar \"${group.name}\" y todos sus items?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteGroup(group)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteItemConfirmation(item: com.example.rapp.data.model.Item) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Item")
            .setMessage("¿Estás seguro de eliminar \"${item.text}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteItem(item)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
