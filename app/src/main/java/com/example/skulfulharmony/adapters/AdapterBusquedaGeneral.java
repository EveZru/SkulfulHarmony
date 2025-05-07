package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.List;

public class AdapterBusquedaGeneral extends RecyclerView.Adapter<AdapterBusquedaGeneral.ResultadoViewHolder> {

    private Context context;
    private List<Object> listaResultados;

    public AdapterBusquedaGeneral(List<Object> listaResultados, Context context) {
        this.listaResultados = listaResultados;
        this.context = context;
    }

    @Override
    public ResultadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new ResultadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultadoViewHolder holder, int position) {
        Object item = listaResultados.get(position);

        if (item instanceof Curso) {
            Curso curso = (Curso) item;
            holder.tvTitulo.setText(curso.getTitulo());
            holder.tvDetalle.setText("Publicado por: " + (curso.getCreador() != null ? curso.getCreador() : "Autor desconocido"));
            Glide.with(context)
                    .load(curso.getImagen())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.logo_sh)
                    .into(holder.imagen);
            holder.card.setOnClickListener(v -> {
                Intent intent = new Intent(context, Ver_cursos.class);
                intent.putExtra("idCurso", curso.getId());
                context.startActivity(intent);
            });

        } else if (item instanceof Clase) {
            Clase clase = (Clase) item;
            holder.tvTitulo.setText(clase.getTitulo());
            holder.tvDetalle.setText("Curso: " + clase.getNombreCurso());
            holder.imagen.setImageResource(R.drawable.logo_sh); // Usa imagen default o modifica según tus datos

        } else if (item instanceof Usuario) {
            Usuario usuario = (Usuario) item;
            holder.tvTitulo.setText(usuario.getNombre());
            holder.tvDetalle.setText(usuario.getCorreo());
            Glide.with(context)
                    .load(usuario.getImagen())
                    .placeholder(R.drawable.logo_sh)
                    .error(R.drawable.logo_sh)
                    .into(holder.imagen);
        }
    }

    @Override
    public int getItemCount() {
        return listaResultados.size();
    }

    public static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDetalle;
        ImageView imagen;
        CardView card;

        public ResultadoViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_textprincipal);
            tvDetalle = itemView.findViewById(R.id.tv_textsegundo);
            imagen = itemView.findViewById(R.id.cardImage);
            card = itemView.findViewById(R.id.clase_elemento);
        }
    }
}