
package com.works.app.pages

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.works.app.R
import com.works.app.adapter.NoteAdapter
import com.works.app.dao.NoteDao
import com.works.app.entity.Note
import com.works.app.utils.AppDatabase
import kotlinx.coroutines.launch

class NotesActivity : AppCompatActivity() {

    private lateinit var n_title: EditText
    private lateinit var n_detail: EditText
    private lateinit var n_SaveBtn: Button
    private lateinit var n_CancelBtn: Button

    private lateinit var rvNotes: RecyclerView
    private lateinit var tvEmpty: TextView

    private lateinit var appDatabase: AppDatabase
    private lateinit var noteDao: NoteDao
    private lateinit var noteAdapter: NoteAdapter

    // Düzenlenen notun ID değeri.
    // null ise yeni kayıt oluşturulur.
    private var editingNoteId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_notes)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        initializeViews()
        initializeDatabase()
        initializeRecyclerView()
        initializeListeners()

        allNote()
    }

    // View bileşenlerini tanımla
    private fun initializeViews() {

        n_title = findViewById(R.id.n_title)
        n_detail = findViewById(R.id.n_detail)

        n_SaveBtn = findViewById(R.id.n_SaveBtn)
        n_CancelBtn = findViewById(R.id.n_CancelBtn)

        rvNotes = findViewById(R.id.rvNotes)
        tvEmpty = findViewById(R.id.tvEmpty)
    }

    // Room bağlantısı
    private fun initializeDatabase() {

        appDatabase = AppDatabase.getDatabase(this)
        noteDao = appDatabase.noteDao()
    }

    // RecyclerView kurulumu
    private fun initializeRecyclerView() {

        noteAdapter = NoteAdapter(

            onEdit = { note ->
                editNote(note)
            },

            onDelete = { note ->
                confirmDelete(note)
            }
        )

        rvNotes.apply {

            layoutManager = LinearLayoutManager(this@NotesActivity)
            adapter = noteAdapter

            setHasFixedSize(true)
        }
    }

    // Buton olayları
    private fun initializeListeners() {

        n_SaveBtn.setOnClickListener {
            noteSave()
        }

        n_CancelBtn.setOnClickListener {
            cancelEdit()
        }
    }

    // Notları Room üzerinden getir
    private fun allNote() {

        lifecycleScope.launch {

            try {

                val notes = noteDao.allNotes()

                // RecyclerView verilerini güncelle
                noteAdapter.submitList(notes)

                // Boş liste kontrolü
                tvEmpty.visibility =
                    if (notes.isEmpty()) {
                        android.view.View.VISIBLE
                    } else {
                        android.view.View.GONE
                    }

            } catch (e: Exception) {

                Toast.makeText(
                    this@NotesActivity,
                    "Notlar yüklenirken hata oluştu: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Yeni not kaydet veya mevcut notu güncelle
    private fun noteSave() {

        val title = n_title.text.toString().trim()
        val detail = n_detail.text.toString().trim()

        if (title.isEmpty()) {

            n_title.error = "Başlık boş olamaz"
            n_title.requestFocus()
            return
        }

        if (detail.isEmpty()) {

            n_detail.error = "Not detayı boş olamaz"
            n_detail.requestFocus()
            return
        }

        lifecycleScope.launch {

            try {

                val currentId = editingNoteId

                if (currentId == null) {

                    // Yeni kayıt
                    val note = Note(
                        id = 0,
                        title = title,
                        detail = detail
                    )

                    noteDao.saveNote(note)

                    Toast.makeText(
                        this@NotesActivity,
                        "Not başarıyla kaydedildi",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    // Mevcut kaydı güncelle
                    val note = Note(
                        id = currentId,
                        title = title,
                        detail = detail
                    )

                    val result = noteDao.updateNote(note)

                    if (result > 0) {

                        Toast.makeText(
                            this@NotesActivity,
                            "Not güncellendi",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            this@NotesActivity,
                            "Not güncellenemedi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                clearForm()
                allNote()

            } catch (e: Exception) {

                Toast.makeText(
                    this@NotesActivity,
                    "İşlem başarısız: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Düzenleme moduna geç
    private fun editNote(note: Note) {

        editingNoteId = note.id

        n_title.setText(note.title)
        n_detail.setText(note.detail)

        n_SaveBtn.text = "Güncelle"
        n_CancelBtn.visibility = android.view.View.VISIBLE

        // Form alanına odaklan
        n_title.requestFocus()

        // Form alanına kaydır
        findViewById<android.view.View>(
            R.id.layoutNoteForm
        ).requestFocus()
    }

    // Silme onayı
    private fun confirmDelete(note: Note) {

        AlertDialog.Builder(this)
            .setTitle("Notu Sil")
            .setMessage(
                "\"${note.title}\" başlıklı notu silmek istediğinize emin misiniz?"
            )
            .setPositiveButton("Sil") { _, _ ->

                deleteNote(note)
            }
            .setNegativeButton("Vazgeç", null)
            .show()
    }

    // Room üzerinden notu sil
    private fun deleteNote(note: Note) {

        lifecycleScope.launch {

            try {

                val result = noteDao.deleteNote(note)

                if (result > 0) {

                    // Silinen kayıt düzenleniyorsa formu temizle
                    if (editingNoteId == note.id) {
                        clearForm()
                    }

                    Toast.makeText(
                        this@NotesActivity,
                        "Not silindi",
                        Toast.LENGTH_SHORT
                    ).show()

                    allNote()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@NotesActivity,
                    "Not silinemedi: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Formu temizle ve yeni kayıt moduna dön
    private fun clearForm() {

        editingNoteId = null

        n_title.setText("")
        n_detail.setText("")

        n_SaveBtn.text = "Notu Kaydet"

        n_CancelBtn.visibility = android.view.View.GONE

        n_title.requestFocus()
    }

    // Düzenlemeyi iptal et
    private fun cancelEdit() {

        clearForm()

        Toast.makeText(
            this,
            "Düzenleme iptal edildi",
            Toast.LENGTH_SHORT
        ).show()
    }
}
