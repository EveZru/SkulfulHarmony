package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.Act_flauta;
import com.example.skulfulharmony.Act_pianoAcordes;
import com.example.skulfulharmony.EscribirPartiturasAct;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.List;

public class AdapterHomeVerCursosOriginales extends RecyclerView.Adapter<AdapterHomeVerCursosOriginales.CursoViewHolder> {
    //ya que los cursos origunales no se cargan por medio de firebase cre que seria mejor que tubiera un adapter aparte por lo que lo cree por cuestiones de sueño no los termine de vincular porque me daban errores asi que lo deje con lo de firebase por lo que no funciona por que pues no existe jsjsj jiji
    private Context context;
    private List<Curso> listaCursos;

    public AdapterHomeVerCursosOriginales(Context context, List<Curso> listaCursos) {
        this.context = context;
        this.listaCursos = listaCursos;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new CursoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.tvTextPrincipal.setText(curso.getTitulo());

        // Opcional: Establecer un icono diferente si lo tienes en tu objeto CursoLocal

        Glide.with(context)
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.perfil_logo)
                .into(holder.cardImage);

        // Click para abrir actividad de clases
        holder.itemView.setOnClickListener(v -> {
            String tituloCurso = curso.getTitulo();
            Log.d("AdapterOriginales", "Título del curso clickeado: " + tituloCurso);
            Toast.makeText(context, "Clic en: " + tituloCurso, Toast.LENGTH_SHORT).show();

            if(tituloCurso!=null){

                    if(tituloCurso.equals("Simulador de flauta"))  {
                      //  Toast.makeText(context, "click clase 4", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Act_flauta.class);
                        context.startActivity(intent);
                    } else if(tituloCurso.equals("Simulador de piano"))  {
                       // Toast.makeText(context, "click clase 4", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Act_pianoAcordes.class);
                        context.startActivity(intent);
                    }else if(tituloCurso.equals("Repaso de escribir partituras"))  {
                       // Toast.makeText(context, "click clase 4", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, EscribirPartiturasAct.class);
                        context.startActivity(intent);
                    }

                    else Toast.makeText(context, "Clase no disponible por el momento", Toast.LENGTH_SHORT).show();

                }
            /*  String tituloCurso = curso.getTitulo();
            Log.d("AdapterOriginales", "Título del curso clickeado: " + tituloCurso);
            if (tituloCurso != null) {
                if (tituloCurso.equals("curso4")) {
                    // Navegar a ActividadSimulador para curso3
                    Intent intent = new Intent(context, Act_flauta.class);
                    context.startActivity(intent);
                } else if (tituloCurso.equals("curso3")) {
                    // Navegar a otra actividad para curso4 (ejemplo: EscribirPartiturasAct)
                    Intent intent = new Intent(context, Act_pianoAcordes.class);
                    context.startActivity(intent);
                } else if (tituloCurso.equals("curso5")) {
                    // Navegar a Home para curso1
                    Intent intent = new Intent(context, Act_flauta.class);
                    context.startActivity(intent);
                } else {
                    // Navegar a la actividad por defecto para otros cursos (ejemplo: Ver_cursos)
                    Intent intent = new Intent(context, Ver_cursos.class);
                    context.startActivity(intent);
                }
            }*/
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