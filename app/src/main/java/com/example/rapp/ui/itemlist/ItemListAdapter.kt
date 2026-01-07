package com.example.rapp.ui.itemlist

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.R
import com.example.rapp.data.model.Item
import java.util.Collections

class ItemListAdapter(
    private var items: MutableList<Item>,
    private val onItemDelete: (Item) -> Unit,
    private val onItemsReordered: (List<Item>) -> Unit,
    private val onFavoriteToggle: (Item) -> Unit
) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

    private var itemTouchHelper: ItemTouchHelper? = null

    fun setItemTouchHelper(helper: ItemTouchHelper) {
        this.itemTouchHelper = helper
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemText: TextView = itemView.findViewById(R.id.tvItemText)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val ivDragHandle: ImageView = itemView.findViewById(R.id.ivDragHandle)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.tvItemText.text = item.text

        holder.ivFavorite.setImageResource(
            if (item.isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )

        holder.ivFavorite.setOnClickListener {
            onFavoriteToggle(item)
        }

        holder.ivDelete.setOnClickListener {
            onItemDelete(item)
        }

        holder.ivDragHandle.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                itemTouchHelper?.startDrag(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int = items.size

    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onDragComplete() {
        // Actualizar el orden de los items
        val reorderedItems = items.mapIndexed { index, item ->
            item.copy(order = index)
        }
        onItemsReordered(reorderedItems)
    }

    fun getItems(): List<Item> = items.toList()
}
