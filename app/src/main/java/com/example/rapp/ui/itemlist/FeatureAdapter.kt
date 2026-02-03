package com.example.rapp.ui.landing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rapp.R

data class Feature(
    val id: String,
    val title: String,
    val description: String,
    val icon: Int,
    val isEnabled: Boolean = true
)

class FeatureAdapter(
    private val features: List<Feature>,
    private val onFeatureClick: (Feature) -> Unit
) : RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>() {

    inner class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivFeatureIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvFeatureTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvFeatureDescription)
        val disabledOverlay: View = itemView.findViewById(R.id.disabledOverlay)
        val tvComingSoon: TextView = itemView.findViewById(R.id.tvComingSoon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feature_card, parent, false)
        return FeatureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        val feature = features[position]

        holder.ivIcon.setImageResource(feature.icon)
        holder.tvTitle.text = feature.title
        holder.tvDescription.text = feature.description

        if (feature.isEnabled) {
            holder.disabledOverlay.visibility = View.GONE
            holder.tvComingSoon.visibility = View.GONE
            holder.itemView.alpha = 1f
            holder.itemView.setOnClickListener {
                onFeatureClick(feature)
            }
        } else {
            holder.disabledOverlay.visibility = View.VISIBLE
            holder.tvComingSoon.visibility = View.VISIBLE
            holder.itemView.alpha = 0.9f
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = features.size
}