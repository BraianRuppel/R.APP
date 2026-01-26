package com.example.rapp.ui.itemlist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.R
import com.example.rapp.data.model.Item
import java.time.format.DateTimeFormatter
import java.util.Collections
import java.util.Locale

class ItemListAdapter(
    private var items: MutableList<Item>,
    private val onItemDelete: (Item) -> Unit,
    private val onItemsReordered: (List<Item>) -> Unit,
    private val onFavoriteToggle: (Item) -> Unit,
    private val onDateClick: (Item) -> Unit,
    private val onCompletedToggle: (Item) -> Unit
) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

    private var itemTouchHelper: ItemTouchHelper? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es"))

    fun setItemTouchHelper(helper: ItemTouchHelper) {
        this.itemTouchHelper = helper
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbCompleted: CheckBox = itemView.findViewById(R.id.cbCompleted)
        val tvItemText: TextView = itemView.findViewById(R.id.tvItemText)
        val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        val ivCalendar: ImageView = itemView.findViewById(R.id.ivCalendar)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.cbCompleted.isChecked = item.isCompleted
        holder.cbCompleted.setOnClickListener {
            onCompletedToggle(item)
        }

        holder.tvItemText.text = item.text
        if (item.isCompleted) {
            holder.tvItemText.paintFlags = holder.tvItemText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvItemText.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
        } else {
            holder.tvItemText.paintFlags = holder.tvItemText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvItemText.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        }

        if (item.dueDate != null) {
            holder.tvDueDate.visibility = View.VISIBLE
            holder.tvDueDate.text = item.dueDate.format(dateFormatter)
            holder.ivCalendar.setImageResource(R.drawable.ic_calendar_filled)
            holder.ivCalendar.setColorFilter(ContextCompat.getColor(context, R.color.primary))
        } else {
            holder.tvDueDate.visibility = View.GONE
            holder.ivCalendar.setImageResource(R.drawable.ic_calendar_outline)
            holder.ivCalendar.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary))
        }

        holder.ivCalendar.setOnClickListener {
            onDateClick(item)
        }
        holder.tvDueDate.setOnClickListener {
            onDateClick(item)
        }

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

        holder.itemView.setOnLongClickListener {
            itemTouchHelper?.startDrag(holder)
            true
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
