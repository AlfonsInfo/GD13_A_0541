package com.alfonsus.gd11_a_0541

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.alfonsus.gd11_a_0541.api.MahasiswaApi
import com.alfonsus.gd11_a_0541.models.Mahasiswa
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class AddEditActivity : AppCompatActivity() {
    companion object{
        private val FAKULTAS_LIST = arrayOf("FTI", "FT", "FTB", "FBE", "FISIP", "FH")
        private val PRODI_LIST = arrayOf(
            "Informatika",
            "Arsitektur",
            "Biologi",
            "Manajemen",
            "Ilmu Komunikasi",
            "Ilmu Hukum"
        )
        private  var etNama : EditText? = null
        private  var etNPM : EditText? = null
        private var edFakultas : AutoCompleteTextView? = null
        private var edProdi : AutoCompleteTextView? = null
        private var layoutLoading : LinearLayout? = null
        private var queue: RequestQueue? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        queue = Volley.newRequestQueue(this)
        etNama = findViewById(R.id.et_nama)
        etNPM = findViewById(R.id.et_npm)
        edFakultas = findViewById(R.id.ed_fakultas)
        edProdi = findViewById(R.id.ed_prodi)
        layoutLoading = findViewById(R.id.layout_loading)

        setExposedDropDownMenu()

        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener { finish() }
        val btnSave = findViewById<Button>(R.id.btn_save)
        val tvTitle = findViewById<TextView>(R.id.tv_tittle)
        val id = intent.getLongExtra("id", -1)
        if(id ==-1L)
        {
            tvTitle.setText("Tambah Mahasiswa")
            btnSave.setOnClickListener { createMahasiswa() }
        } else
        {
            tvTitle.setText("Edit mahasiswa")
            getMahasiswaById(id)

            btnSave.setOnClickListener { updateMahasiswa(id) }

        }
    }
    fun setExposedDropDownMenu() {
        val adapterFakultas: ArrayAdapter<String> = ArrayAdapter<String>(this,
            R.layout.item_list, FAKULTAS_LIST)
        edFakultas!!.setAdapter(adapterFakultas)

        val adapterProdi: ArrayAdapter<String> = ArrayAdapter<String>(this,
            R.layout.item_list, PRODI_LIST)
        edProdi!!.setAdapter(adapterProdi)
    }

    private fun getMahasiswaById(id: Long) {
        //Fungsi untuk menampilkan data mahasiswa berdasarkan id
        setLoading(true)
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, MahasiswaApi.GET_BY_ID_URL + id, Response.Listener { response ->
                val gson = Gson()
                val mahasiswa = gson.fromJson(response, Mahasiswa::class.java)

                etNama!!.setText(mahasiswa.nama)
                etNPM!!.setText(mahasiswa.npm)
                edFakultas!!.setText(mahasiswa.fakultas)
                edProdi!!.setText(mahasiswa.prodi)
                setExposedDropDownMenu()

                Toast.makeText(this@AddEditActivity, "Data berhasil diambil!", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }, Response.ErrorListener { error ->
                setLoading(false)
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@AddEditActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@AddEditActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }
        queue!!.add(stringRequest)
    }

    private fun createMahasiswa() {
        // Fungsi untuk menambah data mahasiswa.
        setLoading(true)

        if(etNama!!.text.toString().isEmpty())
        {
            Toast.makeText(this@AddEditActivity, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }else if(etNPM!!.text.toString().isEmpty())
        {
            Toast.makeText(this@AddEditActivity, "NPM tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }else if(edFakultas!!.text.toString().isEmpty())
        {
            Toast.makeText(this@AddEditActivity, "Fakultas tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }else if(edProdi!!.text.toString().isEmpty())
        {
            Toast.makeText(this@AddEditActivity, "Prodi tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }else {
            val mahasiswa = Mahasiswa(
                etNama!!.text.toString(),
                etNPM!!.text.toString(),
                edFakultas!!.text.toString(),
                edProdi!!.text.toString()
            )
            val stringRequest: StringRequest =
                object :
                    StringRequest(Method.POST, MahasiswaApi.ADD_URL, Response.Listener { response ->
                        val gson = Gson()
                        var mahasiswa = gson.fromJson(response, Mahasiswa::class.java)

                        if (mahasiswa != null)
                            Toast.makeText(
                                this@AddEditActivity,
                                "Data Berhasil Ditambah",
                                Toast.LENGTH_SHORT
                            ).show()

                        val returnIntent = Intent()
                        setResult(RESULT_OK, returnIntent)
                        finish()

                        setLoading(false)
                    }, Response.ErrorListener { error ->
                        setLoading(false)
                        try {
                            val responseBody =
                                String(error.networkResponse.data, StandardCharsets.UTF_8)
                            val errors = JSONObject(responseBody)
                            Toast.makeText(
                                this@AddEditActivity,
                                errors.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@AddEditActivity, e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Accept"] = "application/json"
                        return headers
                    }

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray {
                        val gson = Gson()
                        val requestBody = gson.toJson(mahasiswa)
                        return requestBody.toByteArray(StandardCharsets.UTF_8)
                    }

                    override fun getBodyContentType(): String {
                        return "application/json"
                    }
                }

            // Menambahkan request ke request queue
            queue!!.add(stringRequest)
        }
        setLoading(false)
    }


    private fun updateMahasiswa(id: Long) {
        // Fungsi untuk engubah data mahasiswa.
        setLoading(true)

        val mahasiswa = Mahasiswa(
            etNama!!.text.toString(),
            etNPM!!.text.toString(),
            edFakultas!!.text.toString(),
            edProdi!!.text.toString(),
        )

        val stringRequest: StringRequest = object :
            StringRequest(Method.PUT, MahasiswaApi.UPDATE_URL + id, Response.Listener { response ->
                val gson = Gson()
                var mahasiswa = gson.fromJson(response, Mahasiswa::class.java)

                if (mahasiswa != null)
                    Toast.makeText(this@AddEditActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

                setLoading(false)
            }, Response.ErrorListener { error ->
                setLoading(false)
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@AddEditActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e:Exception) {
                    Toast.makeText(this@AddEditActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val gson = Gson()
                val requestBody = gson.toJson(mahasiswa)
                return requestBody.toByteArray(StandardCharsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        queue!!.add(stringRequest)
    }

    //Fungsi ini digunakan menampilkan layout loading
    private fun setLoading(isLoading: Boolean) {
        if(isLoading) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            layoutLoading!!.visibility = View.VISIBLE
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            layoutLoading!!.visibility = View.INVISIBLE
        }
    }
}
