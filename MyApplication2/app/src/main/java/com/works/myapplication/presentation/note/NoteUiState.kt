package com.works.myapplication.presentation.note

import com.works.myapplication.domain.model.Note

data class NoteUiState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val isFavoriteOnly: Boolean = false,
    val isLoading: Boolean = false
)