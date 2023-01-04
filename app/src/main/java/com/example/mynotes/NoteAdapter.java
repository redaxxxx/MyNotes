package com.example.mynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.data.NoteEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{
    private static final String DATE_FORMAT = "dd/MM/yyy";
    final private ItemClickListener itemClickListener;
    private List<NoteEntity> mNoteEntity;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public NoteAdapter(Context context, ItemClickListener itemClickListener){
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteEntity noteEntity = mNoteEntity.get(position);
        String description = noteEntity.getDescription();
        String modifiedAt = dateFormat.format(noteEntity.getModifiedAt());

        holder.describeTextView.setText(description);
        holder.updateAtTextView.setText(modifiedAt);
    }

    @Override
    public int getItemCount() {
        if (mNoteEntity == null) {
            return 0;
        }
        return mNoteEntity.size();
    }
    public List<NoteEntity> getNotes(){
        return mNoteEntity;
    }

    public void setNotes(List<NoteEntity> notes){
        mNoteEntity = notes;
       notifyDataSetChanged();
    }

    public interface ItemClickListener{
        void onItemClickListener(int itemId);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

         TextView describeTextView, updateAtTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            describeTextView = itemView.findViewById(R.id.description_TV);
            updateAtTextView = itemView.findViewById(R.id.note_update_at);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int element = mNoteEntity.get(getAdapterPosition()).getId();
            itemClickListener.onItemClickListener(element);
        }
    }
}
