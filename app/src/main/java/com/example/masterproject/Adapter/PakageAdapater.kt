package com.example.masterproject.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.masterproject.PakageDetailActivity
import com.example.masterproject.PakageModel
import com.example.masterproject.R
import com.example.masterproject.databinding.DisplayPakageBinding
import com.google.firebase.database.ValueEventListener

class PakageAdapter(var context: ValueEventListener, var datalist: ArrayList<PakageModel>) : RecyclerView.Adapter<PakageAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: DisplayPakageBinding) : RecyclerView.ViewHolder(binding.root) {


        // Initialize view references here if needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.display_pakage, parent, false)
        var holder = MyViewHolder(DisplayPakageBinding.bind(v))
        return holder

    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = datalist[position]
        // Bind data to views here using the holder's binding object


        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.binding.imagview)
        holder.binding.txtpakage.text = currentItem.pakage
        holder.binding.txtname.text = currentItem.name
        holder.binding.txtprice.text = currentItem.price.toString()
        holder.binding.btndays.text = currentItem.days.toString()
        holder.binding.txtnotes.text = currentItem.notes

        holder.binding.imgnext.setOnClickListener {
            var i = Intent(holder.itemView.context, PakageDetailActivity::class.java)
            i.putExtra("pakage",currentItem.pakage)
            i.putExtra("name",currentItem.name)
            i.putExtra("Price",currentItem.price)
            i.putExtra("day",currentItem.days)
            i.putExtra("mobile",currentItem.mobile)
            i.putExtra("Notes",currentItem.notes)
            i.putExtra("discription",currentItem.discription)
            holder.itemView.context.startActivity(i)
            }

        holder.itemView.setOnClickListener {
            var i = Intent(holder.itemView.context, PakageDetailActivity::class.java)
            i.putExtra("pakage",currentItem.pakage)
            i.putExtra("name",currentItem.name)
            i.putExtra("Price",currentItem.price)
            i.putExtra("day",currentItem.days)
            i.putExtra("mobile",currentItem.mobile)
            i.putExtra("Notes",currentItem.notes)
            i.putExtra("discription",currentItem.discription)

            holder.itemView.context.startActivity(i)
        }

    }
}

