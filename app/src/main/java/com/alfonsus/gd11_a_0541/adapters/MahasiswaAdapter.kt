package com.alfonsus.gd11_a_0541.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.alfonsus.gd11_a_0541.AddEditActivity
import com.alfonsus.gd11_a_0541.MainActivity
import com.alfonsus.gd11_a_0541.R
import com.alfonsus.gd11_a_0541.models.Mahasiswa
import java.util.*
import kotlin.collections.ArrayList
class MahasiswaAdapter(private var mahasiswaList:  List<Mahasiswa>, context: Context) : RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>(), Filterable {
    private var filteredMahasiswaList : MutableList<Mahasiswa>
    private val context : Context

    init{
        filteredMahasiswaList = ArrayList(mahasiswaList)
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_mahasiswa, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() : Int {
        return filteredMahasiswaList.size
    }

    fun setMahasiswaList(mahasiswaList: Array<Mahasiswa>){
        this.mahasiswaList = mahasiswaList.toList()
        filteredMahasiswaList = mahasiswaList.toMutableList()
    }

    override fun onBindViewHolder(holder : ViewHolder , position: Int)
    {
        val mahasiswa = filteredMahasiswaList[position]
        holder.tvNama.text = mahasiswa.nama
        holder.tvNPM.text = mahasiswa.npm
        holder.tvFakultas.text = mahasiswa.fakultas
        holder.tvProdi.text = mahasiswa.prodi

        holder.btnDelete.setOnClickListener{
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus data mahasiswa ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus"){_,_ ->
                    if(context is MainActivity) mahasiswa.id?.let { it1 ->
                        context.deleteMahasiswa(
                            it1
                        )
                    }
                }
                .show()
        }
        holder.cvMahasiswa.setOnClickListener {
            val i = Intent(context, AddEditActivity::class.java)
            i.putExtra("id", mahasiswa.id)
            if(context is MainActivity)
                context.startActivityForResult(i, MainActivity.LAUNCH_ADD_ACTIVITY  )
        }
    }

    override fun getFilter(): Filter {
        return object : Filter()
        {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<Mahasiswa> = java.util.ArrayList()
                if(charSequenceString.isEmpty())
                {
                    filtered.addAll(mahasiswaList)
                }else
                {
                    for(mahasiswa in mahasiswaList)
                    {
                        if(mahasiswa.nama.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        ) filtered.add(mahasiswa)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredMahasiswaList.clear()
                filteredMahasiswaList.addAll(filterResults.values as List<Mahasiswa>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)
    {
        var tvNama:TextView
        var tvNPM : TextView
        var tvFakultas : TextView
        var tvProdi : TextView
        var btnDelete : ImageButton
        var cvMahasiswa : CardView

        init {
            tvNama = itemView.findViewById(R.id.tv_nama)
            tvNPM = itemView.findViewById(R.id.tv_npm)
            tvProdi = itemView.findViewById(R.id.tv_prodi)
            tvFakultas = itemView.findViewById(R.id.tv_fakultas)
            btnDelete = itemView.findViewById(R.id.btn_delete)
            cvMahasiswa = itemView.findViewById(R.id.cv_mahasiswa)
        }
    }
}