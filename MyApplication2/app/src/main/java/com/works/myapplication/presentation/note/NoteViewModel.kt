package com.works.myapplication.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.works.myapplication.domain.model.Note
import com.works.myapplication.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isFavoriteOnly = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(_searchQuery, _isFavoriteOnly) { query, favoriteOnly ->
                Pair(query, favoriteOnly)
            }.flatMapLatest { (query, favoriteOnly) ->
                when {
                    query.isNotEmpty() -> noteRepository.searchNotes(query)
                    favoriteOnly -> noteRepository.getFavoriteNotes()
                    else -> noteRepository.getAllNotes()
                }
            }.collect { notes ->
                _uiState.update {
                    it.copy(
                        notes = notes,
                        searchQuery = _searchQuery.value,
                        isFavoriteOnly = _isFavoriteOnly.value
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFavoriteFilterChanged(favoriteOnly: Boolean) {
        _isFavoriteOnly.value = favoriteOnly
    }

    fun saveNote(id: Long, title: String, content: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (id == 0L) {
                val newNote = Note(
                    title = title,
                    content = content,
                    isFavorite = isFavorite,
                    createdAt = now,
                    updatedAt = now
                )
                noteRepository.insertNote(newNote)
            } else {
                val updatedNote = Note(
                    id = id,
                    title = title,
                    content = content,
                    isFavorite = isFavorite,
                    updatedAt = now
                )
                noteRepository.updateNote(updatedNote)
            }
        }
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note.copy(isFavorite = !note.isFavorite, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }
}