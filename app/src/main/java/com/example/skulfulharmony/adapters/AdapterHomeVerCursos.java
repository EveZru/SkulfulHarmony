package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.ClasesOriginales;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdapterHomeVerCursos extends RecyclerView.Adapter<AdapterHomeVerCursos.CursoViewHolder> {
    private Context context;
    private List<Curso> listaCursos;

    public AdapterHomeVerCursos(List<Curso> listaCursos, Context context) {
        this.listaCursos = listaCursos;
        this.context = context;
    }

    @Override
    public CursoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);

        // Título del curso
        holder.tvTextPrincipal.setText(curso.getTitulo());

        // Texto secundario opcional (por ejemplo, fecha o descripción)
        if (curso.getFechaCreacion() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.tvTextSegundo.setText("Creado el " + dateFormat.format(curso.getFechaCreacion()));
        } else if (curso.getDescripcion() != null) {
            holder.tvTextSegundo.setText(curso.getDescripcion());
        } else {
            holder.tvTextSegundo.setText("Curso sin descripción");
        }

        // Imagen con Glide
        Glide.with(context)
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.cardImage);

        // Click para abrir actividad de clases
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Ver_cursos.class);//Corregir
            intent.putExtra("idCurso", curso.getId()); // Asegúrate de tener este método
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardImage;
        public TextView tvTextPrincipal;
        public TextView tvTextSegundo;

        public CursoViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage);
            tvTextPrincipal = itemView.findViewById(R.id.tv_textprincipal);
            tvTextSegundo = itemView.findViewById(R.id.tv_textsegundo);

        }
    }
}
