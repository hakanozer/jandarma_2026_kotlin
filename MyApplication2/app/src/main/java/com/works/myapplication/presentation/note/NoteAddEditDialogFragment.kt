package com.works.myapplication.presentation.note

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.works.myapplication.R
import com.works.myapplication.databinding.DialogAddEditNoteBinding
import com.works.myapplication.domain.model.Note

class NoteAddEditDialogFragment : DialogFragment() {

    private var _binding: DialogAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private var noteToEdit: Note? = null
    var onSaveListener: ((id: Long, title: String, content: String, isFavorite: Boolean) -> Unit)? = null

    companion object {
        private const val ARG_NOTE_ID = "arg_note_id"
        private const val ARG_NOTE_TITLE = "arg_note_title"
        private const val ARG_NOTE_CONTENT = "arg_note_content"
        private const val ARG_NOTE_FAVORITE = "arg_note_favorite"

        fun newInstance(note: Note? = null): NoteAddEditDialogFragment {
            val fragment = NoteAddEditDialogFragment()
            if (note != null) {
                val args = Bundle().apply {
                    putLong(ARG_NOTE_ID, note.id)
                    putString(ARG_NOTE_TITLE, note.title)
                    putString(ARG_NOTE_CONTENT, note.content)
                    putBoolean(ARG_NOTE_FAVORITE, note.isFavorite)
                }
                fragment.arguments = args
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            val id = args.getLong(ARG_NOTE_ID, 0L)
            if (id != 0L) {
                noteToEdit = Note(
                    id = id,
                    title = args.getString(ARG_NOTE_TITLE, ""),
                    content = args.getString(ARG_NOTE_CONTENT, ""),
                    isFavorite = args.getBoolean(ARG_NOTE_FAVORITE, false)
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditNoteBinding.inflate(layoutInflater)

        setupFields()

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            validateAndSave()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return dialog
    }

    private fun setupFields() {
        val note = noteToEdit
        if (note != null) {
            binding.tvDialogTitle.text = getString(R.string.edit_note)
            binding.etNoteTitle.setText(note.title)
            binding.etNoteContent.setText(note.content)
            binding.switchFavorite.isChecked = note.isFavorite
        } else {
            binding.tvDialogTitle.text = getString(R.string.add_note)
        }
    }

    private fun validateAndSave() {
        val title = binding.etNoteTitle.text?.toString()?.trim().orEmpty()
        val content = binding.etNoteContent.text?.toString()?.trim().orEmpty()
        val isFavorite = binding.switchFavorite.isChecked

        if (title.isEmpty()) {
            binding.tilNoteTitle.error = getString(R.string.error_note_title_empty)
            return
        } else {
            binding.tilNoteTitle.error = null
        }

        if (content.isEmpty()) {
            binding.tilNoteContent.error = getString(R.string.error_note_content_empty)
            return
        } else {
            binding.tilNoteContent.error = null
        }

        onSaveListener?.invoke(
            noteToEdit?.id ?: 0L,
            title,
            content,
            isFavorite
        )
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}