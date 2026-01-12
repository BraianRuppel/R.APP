package com.example.rapp.ui.itemlist

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.R
import com.example.rapp.data.model.Group
import com.example.rapp.data.model.GroupWithItems
import com.example.rapp.data.model.Item
import java.util.Collections

class GroupAdapter(
    private var groups: MutableList<GroupWithItems>,
    private val onGroupToggle: (Long, Boolean) -> Unit,
    private val onGroupDelete: (Group) -> Unit,
    private val onItemDelete: (Item) -> Unit,
    private val onItemsReordered: (List<Item>) -> Unit,
    private val onGroupsReordered: (List<Group>) -> Unit,
    private val onItemMovedToGroup: (Long, Long, Int) -> Unit,
    private val onAddItemClick: (Long, String) -> Unit,
    private val onFavoriteToggle: (Item) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    private var itemTouchHelper: ItemTouchHelper? = null

    fun setItemTouchHelper(helper: ItemTouchHelper) {
        this.itemTouchHelper = helper
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerContainer: View = itemView.findViewById(R.id.headerContainer)
        val tvGroupName: TextView = itemView.findViewById(R.id.tvGroupName)
        val tvItemCount: TextView = itemView.findViewById(R.id.tvItemCount)
        val ivExpandIcon: ImageView = itemView.findViewById(R.id.ivExpandIcon)
        val ivDeleteGroup: ImageView = itemView.findViewById(R.id.ivDeleteGroup)
        val rvItems: RecyclerView = itemView.findViewById(R.id.rvItems)
        val itemsContainer: View = itemView.findViewById(R.id.itemsContainer)
        val btnAddItem: View = itemView.findViewById(R.id.btnAddItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val groupWithItems = groups[position]
        val group = groupWithItems.group
        val items = groupWithItems.items.sortedBy { it.order }.toMutableList()

        holder.tvGroupName.text = group.name
        holder.tvItemCount.text = "(${items.size})"

        // Icono de expandir/colapsar
        holder.ivExpandIcon.setImageResource(
            if (group.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
        )

        // Mostrar/ocultar items
        holder.itemsContainer.visibility = if (group.isExpanded) View.VISIBLE else View.GONE

        // Click en header para expandir/colapsar
        holder.tvGroupName.setOnClickListener {
            onGroupToggle(group.id, !group.isExpanded)
        }
        holder.ivExpandIcon.setOnClickListener {
            onGroupToggle(group.id, !group.isExpanded)
        }

        // Eliminar grupo
        holder.ivDeleteGroup.setOnClickListener {
            onGroupDelete(group)
        }

        // Agregar item al grupo
        holder.btnAddItem.setOnClickListener {
            onAddItemClick(group.id, group.name)
        }

        // Drag handle para grupos
        holder.headerContainer.setOnLongClickListener {
            itemTouchHelper?.startDrag(holder)
            true
        }

        // Configurar RecyclerView de items
        val itemAdapter = ItemListAdapter(
            items = items,
            onItemDelete = onItemDelete,
            onItemsReordered = { reorderedItems ->
                onItemsReordered(reorderedItems)
            },
            onFavoriteToggle = onFavoriteToggle
        )

        holder.rvItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvItems.adapter = itemAdapter

        // ItemTouchHelper para los items dentro del grupo
        val itemTouchCallback = ItemTouchCallback(itemAdapter)
        val itemHelper = ItemTouchHelper(itemTouchCallback)
        itemHelper.attachToRecyclerView(holder.rvItems)
        itemAdapter.setItemTouchHelper(itemHelper)
    }

    override fun getItemCount(): Int = groups.size

    fun updateData(newGroups: List<GroupWithItems>) {
        groups = newGroups.toMutableList()
        notifyDataSetChanged()
    }

    fun moveGroup(fromPosition: Int, toPosition: Int) {
        Collections.swap(groups, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun getGroups(): List<Group> = groups.map { it.group }
}