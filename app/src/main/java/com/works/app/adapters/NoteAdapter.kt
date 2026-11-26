
package com.works.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.works.app.R
import com.works.app.entity.Note

class NoteAdapter(
    private val onEdit: (Note) -> Unit,
    private val onDelete: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)

        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView =
            itemView.findViewById(R.id.tvNoteTitle)

        private val tvDetail: TextView =
            itemView.findViewById(R.id.tvNoteDetail)

        private val btnEdit: Button =
            itemView.findViewById(R.id.btnEdit)

        private val btnDelete: Button =
            itemView.findViewById(R.id.btnDelete)

        fun bind(note: Note) {

            tvTitle.text = note.title
            tvDetail.text = note.detail

            btnEdit.setOnClickListener {
                onEdit(note)
            }

            btnDelete.setOnClickListener {
                onDelete(note)
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean {
            return oldItem == newItem
        }
    }
}
