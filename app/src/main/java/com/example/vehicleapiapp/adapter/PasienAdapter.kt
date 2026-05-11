package com.example.vehicleapiapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleapiapp.R
import com.example.vehicleapiapp.model.Pasien

class PasienAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onEditClick: (Pasien) -> Unit,
    private val onDeleteClick: (Pasien) -> Unit
) : RecyclerView.Adapter<PasienAdapter.PasienViewHolder>() {

    private var listPasien = emptyList<Pasien>()

    fun setData(newList: List<Pasien>) {
        listPasien = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasienViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pasien, parent, false)
        return PasienViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasienViewHolder, position: Int) {
        val currentPasien = listPasien[position]

        holder.tvNama.text = currentPasien.nama
        holder.tvAlamat.text = currentPasien.alamat
        holder.tvNoTelp.text = currentPasien.no_telepon

        // Ubah format kode "L" atau "P" menjadi teks lengkap
        val jenisKelaminLengkap = if (currentPasien.jenis_kelamin == "L") "Laki-laki" else "Perempuan"
        holder.tvKelaminTglLahir.text = "$jenisKelaminLengkap | ${currentPasien.tanggal_lahir}"

        holder.itemView.setOnClickListener { onItemClick(currentPasien.id) }
        holder.btnEdit.setOnClickListener { onEditClick(currentPasien) }
        holder.btnDelete.setOnClickListener { onDeleteClick(currentPasien) }
    }

    override fun getItemCount(): Int = listPasien.size

    class PasienViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaPasien)
        val tvKelaminTglLahir: TextView = itemView.findViewById(R.id.tvKelaminTglLahir)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamatPasien)
        val tvNoTelp: TextView = itemView.findViewById(R.id.tvNoTelepon)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }
}