package com.example.app14.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app14.R;
import com.example.app14.models.Estudiante;

import java.util.List;

public class EstudianteAdapter extends RecyclerView.Adapter<EstudianteAdapter.EstudianteViewHolder> {

    public interface OnEstudianteActionListener {
        void onSelect(Estudiante estudiante);

        void onEdit(Estudiante estudiante);

        void onDelete(Estudiante estudiante);
    }

    private final List<Estudiante> estudiantes;
    private final OnEstudianteActionListener listener;

    public EstudianteAdapter(List<Estudiante> estudiantes, OnEstudianteActionListener listener) {
        this.estudiantes = estudiantes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EstudianteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estudiante, parent, false);
        return new EstudianteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstudianteViewHolder holder, int position) {
        Estudiante estudiante = estudiantes.get(position);

        holder.tvNombre.setText(holder.itemView.getContext().getString(R.string.label_nombre, estudiante.getNombre()));
        holder.tvEmail.setText(holder.itemView.getContext().getString(R.string.label_email, estudiante.getEmail()));
        holder.tvEdad.setText(holder.itemView.getContext().getString(R.string.label_edad, estudiante.getEdad()));

        holder.cardEstudiante.setOnClickListener(v -> listener.onSelect(estudiante));
        holder.btnEditar.setOnClickListener(v -> listener.onEdit(estudiante));
        holder.btnEliminar.setOnClickListener(v -> listener.onDelete(estudiante));
    }

    @Override
    public int getItemCount() {
        return estudiantes.size();
    }

    static class EstudianteViewHolder extends RecyclerView.ViewHolder {
        CardView cardEstudiante;
        TextView tvNombre;
        TextView tvEmail;
        TextView tvEdad;
        Button btnEditar;
        Button btnEliminar;

        public EstudianteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEstudiante = itemView.findViewById(R.id.cardEstudiante);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvEdad = itemView.findViewById(R.id.tvEdad);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
