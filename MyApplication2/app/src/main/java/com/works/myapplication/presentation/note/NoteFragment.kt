package com.works.myapplication.presentation.note

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.works.myapplication.R
import com.works.myapplication.databinding.FragmentNoteBinding
import com.works.myapplication.domain.model.Note
import com.works.myapplication.utils.hide
import com.works.myapplication.utils.show
import com.works.myapplication.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(
            onFavoriteClick = { note ->
                viewModel.toggleFavorite(note)
            },
            onEditClick = { note ->
                showAddEditDialog(note)
            },
            onDeleteClick = { note ->
                showDeleteConfirmationDialog(note)
            }
        )
        binding.rvNotes.adapter = adapter
    }

    private fun setupListeners() {
        binding.etSearchNotes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchQueryChanged(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val isFavoritesOnly = checkedIds.contains(R.id.chipFavorites)
            viewModel.onFavoriteFilterChanged(isFavoritesOnly)
        }

        binding.fabAddNote.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.notes)

                    if (state.notes.isEmpty()) {
                        binding.layoutEmptyNotes.show()
                        binding.rvNotes.hide()
                    } else {
                        binding.layoutEmptyNotes.hide()
                        binding.rvNotes.show()
                    }
                }
            }
        }
    }

    private fun showAddEditDialog(note: Note?) {
        val dialog = NoteAddEditDialogFragment.newInstance(note)
        dialog.onSaveListener = { id, title, content, isFavorite ->
            viewModel.saveNote(id, title, content, isFavorite)
            requireContext().showToast(getString(R.string.note_saved_success))
        }
        dialog.show(parentFragmentManager, "NoteAddEditDialog")
    }

    private fun showDeleteConfirmationDialog(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_note_title))
            .setMessage(getString(R.string.delete_note_confirm))
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                viewModel.deleteNote(note)
                requireContext().showToast(getString(R.string.note_deleted_success))
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}