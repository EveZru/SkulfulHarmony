package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.databaseinfo.DbCourse;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdapterBibliotecaVerCursosDescargados extends RecyclerView.Adapter<AdapterBibliotecaVerCursosDescargados.CursoViewHolder> {

    private Context context;
    private List<Curso> cursosDescargados;

    public AdapterBibliotecaVerCursosDescargados(Context context) {
        this.context = context;
        cargarCursos();
    }

    private void cargarCursos() {
        DbCourse dbCurso = new DbCourse(context);
        cursosDescargados = dbCurso.getCursosDescargados();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = cursosDescargados.get(position);

        holder.nombreCurso.setText(curso.getTitulo());

        // Formatear la fecha de descarga
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fecha = (curso.getDescripcion() != null) ? curso.getDescripcion() : "Descargado el " + dateFormat.format(curso.getFechaCreacion());
        holder.fechaDescarga.setText(fecha);

        // Cargar imagen con Glide
        Glide.with(context)
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.imagenCurso);
    }

    @Override
    public int getItemCount() {
        return cursosDescargados.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreCurso, fechaDescarga;
        ImageView imagenCurso;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreCurso = itemView.findViewById(R.id.tv_textprincipal);
            fechaDescarga = itemView.findViewById(R.id.tv_textsegundo);
            imagenCurso = itemView.findViewById(R.id.cardImage);
        }
    }
}
