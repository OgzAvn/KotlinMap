package com.oguzavanoglu.kotlinmaps.view

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oguzavanoglu.kotlinmaps.databinding.RecyclerViewBinding

class PlaceAdapter(val placeList : List<Place>) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    class PlaceHolder(val binding: RecyclerViewBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {

        val binding = RecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaceHolder(binding)
    }

    override fun getItemCount(): Int {

        return placeList.size
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = placeList.get(position).name
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context,MapsActivity::class.java)
            intent.putExtra("selectedPlace",placeList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }
    }
}