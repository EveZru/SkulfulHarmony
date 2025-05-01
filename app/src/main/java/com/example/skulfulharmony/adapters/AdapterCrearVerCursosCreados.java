package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.VerCursoComoCreador;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterCrearVerCursosCreados extends RecyclerView.Adapter<AdapterCrearVerCursosCreados.ViewHolder>{
    ArrayList<Curso> listaCursos = new ArrayList<>();
    Context context;
    public AdapterCrearVerCursosCreados(ArrayList<Curso> listaCursos, Context context){
        this.listaCursos = listaCursos;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterCrearVerCursosCreados.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_verlista_clase_curso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCrearVerCursosCreados.ViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.tvTextPrincipal.setText(curso.getTitulo());
        if(curso.getFechaCreacionf() == null){
            holder.tvTextSegundo.setText("Error al cargar fecha de creacion");
        }else {
            Date date = curso.getFechaCreacionf().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.tvTextSegundo.setText(sdf.format(date));
        }
        Glide.with(context)
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.cardImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VerCursoComoCreador.class);
                intent.putExtra("idCurso", curso.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardImage;
        public TextView tvTextPrincipal;
        public TextView tvTextSegundo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.img_verlista_ccc);
            tvTextPrincipal = itemView.findViewById(R.id.txt_principal_verlista_ccc);
            tvTextSegundo = itemView.findViewById(R.id.txt_secundario_verlista_ccc);
        }
    }
}
