package com.works.app.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.works.app.entity.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun saveNote(note: Note) : Long

    @Delete
    suspend fun deleteNote(note: Note) : Int

    @Update
    suspend fun updateNote(note: Note) : Int

    @Query("select * from notes")
    suspend fun allNotes() : List<Note>

}