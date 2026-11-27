package com.works.myapplication.presentation.note

import com.works.myapplication.domain.model.Note
import com.works.myapplication.domain.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var noteRepository: NoteRepository
    private lateinit var viewModel: NoteViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        noteRepository = mockk(relaxed = true)
        every { noteRepository.getAllNotes() } returns flowOf(
            listOf(
                Note(id = 1, title = "Test 1", content = "Content 1"),
                Note(id = 2, title = "Test 2", content = "Content 2", isFavorite = true)
            )
        )
        viewModel = NoteViewModel(noteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads all notes into uiState`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.notes.size)
    }

    @Test
    fun `saveNote for new note calls insertNote`() = runTest {
        viewModel.saveNote(0L, "New Title", "New Content", false)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { noteRepository.insertNote(any()) }
    }

    @Test
    fun `deleteNote calls deleteNote in repository`() = runTest {
        val note = Note(id = 1, title = "Delete me", content = "Content")
        viewModel.deleteNote(note)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { noteRepository.deleteNote(note) }
    }
}