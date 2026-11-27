package com.works.myapplication.presentation.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.works.myapplication.R
import com.works.myapplication.databinding.ItemNoteBinding
import com.works.myapplication.domain.model.Note
import com.works.myapplication.utils.formatDateTurkish

class NoteAdapter(
    private val onFavoriteClick: (Note) -> Unit,
    private val onEditClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NOTE_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.tvNoteTitle.text = note.title
            binding.tvNoteContent.text = note.content
            binding.tvNoteDate.text = note.updatedAt.formatDateTurkish()

            if (note.isFavorite) {
                binding.ibFavorite.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                binding.ibFavorite.setImageResource(R.drawable.ic_favorite_border)
            }

            binding.ibFavorite.setOnClickListener {
                onFavoriteClick(note)
            }

            binding.ibEdit.setOnClickListener {
                onEditClick(note)
            }

            binding.ibDelete.setOnClickListener {
                onDeleteClick(note)
            }
        }
    }

    companion object {
        private val NOTE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
    }
}