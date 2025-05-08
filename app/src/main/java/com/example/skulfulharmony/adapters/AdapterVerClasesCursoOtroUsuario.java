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
import com.example.skulfulharmony.VerClaseComoCreador;
import com.example.skulfulharmony.Ver_clases;
import com.example.skulfulharmony.javaobjects.courses.Clase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterVerClasesCursoOtroUsuario extends RecyclerView.Adapter<AdapterVerClasesCursoOtroUsuario.ViewHolder> {
    Context context;
    ArrayList<Clase> listaClase = new ArrayList<>();

    public AdapterVerClasesCursoOtroUsuario(ArrayList<Clase> listaClase, Context context) {
        this.listaClase = listaClase;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_verlista_clase_curso, parent, false);
        return new AdapterVerClasesCursoOtroUsuario.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clase clase = listaClase.get(position);
        holder.tvTextPrincipal.setText(clase.getTitulo());
        if(clase.getFechaCreacionf() == null){
            holder.tvTextSegundo.setText("Error al cargar fecha de creacion");
        }else {
            Date date = clase.getFechaCreacionf().getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.tvTextSegundo.setText(sdf.format(date));
        }

        Glide.with(context)
                .load(clase.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.cardImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Ver_clases.class);
                intent.putExtra("idCurso", clase.getIdCurso());
                intent.putExtra("idClase", clase.getIdClase());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaClase.size();
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
