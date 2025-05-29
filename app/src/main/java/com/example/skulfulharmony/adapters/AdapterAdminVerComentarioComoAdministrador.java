package com.example.skulfulharmony.adapters;

import android.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.Denuncia;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdapterAdminVerComentarioComoAdministrador extends RecyclerView.Adapter<AdapterAdminVerComentarioComoAdministrador.ViewHolder> {
    List<Denuncia> comentarios = new ArrayList<>();

    public AdapterAdminVerComentarioComoAdministrador(List<Denuncia> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public AdapterAdminVerComentarioComoAdministrador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_verdenuncia_comentario, parent, false);
        return new AdapterAdminVerComentarioComoAdministrador.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AdapterAdminVerComentarioComoAdministrador.ViewHolder holder, int position) {
        Denuncia denuncia = comentarios.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Mostrar tipo y fecha de denuncia
        holder.tipo.setText(denuncia.getTipo_denuncia());
        holder.fecha.setText(denuncia.getFecha_denuncia().toDate().toString());

        // Establecer color de fondo según tipo
        int color;
        switch (denuncia.getTipo_denuncia()) {
            case "Contenido ilegal":
                color = R.color.rojo_oscuro;
                break;
            case "Suplantación de identidad":
            case "Contenido inapropiado":
                color = R.color.rojo;
                break;
            case "Spam":
            case "Abuso de plataforma":
                color = R.color.cafe;
                break;
            default:
                color = R.color.verde;
                break;
        }
        holder.card.setCardBackgroundColor(holder.itemView.getResources().getColor(color));

        // Verificar si la denuncia es de clase o curso
        if (denuncia.getIdClase() != null && !denuncia.getIdClase().equals("") && !denuncia.getIdClase().equals("-1")) {
            // Denuncia en clase
            db.collection("clases")
                    .whereEqualTo("idCurso", denuncia.getIdCurso())
                    .whereEqualTo("idClase", denuncia.getIdClase())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(claseSnapshot -> {
                        if (!claseSnapshot.isEmpty()) {
                            Clase clase = claseSnapshot.getDocuments().get(0).toObject(Clase.class);
                            String claseDocId = claseSnapshot.getDocuments().get(0).getId();
                            Comentario comentarioObj = clase.getComentarios().get(denuncia.getIdComentario());
                            String textoComentario = comentarioObj != null ? comentarioObj.getTexto() : "Comentario no encontrado";

                            // Obtener curso
                            db.collection("cursos")
                                    .whereEqualTo("idCurso", clase.getIdCurso())
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(cursoSnapshot -> {
                                        if (!cursoSnapshot.isEmpty()) {
                                            Curso curso = cursoSnapshot.getDocuments().get(0).toObject(Curso.class);
                                            holder.identificador_comentario.setText("#" + clase.getIdClase() + ": " + curso.getTitulo());

                                            // Obtener autor
                                            db.collection("usuarios")
                                                    .whereEqualTo("correo", curso.getCreador())
                                                    .limit(1)
                                                    .get()
                                                    .addOnSuccessListener(userSnapshot -> {
                                                        if (!userSnapshot.isEmpty()) {
                                                            Usuario user = userSnapshot.getDocuments().get(0).toObject(Usuario.class);
                                                            holder.autor.setText(user.getNombre());
                                                        } else {
                                                            holder.autor.setText("Autor desconocido");
                                                        }
                                                    });

                                            configurarDialogo(holder, denuncia, textoComentario, true, claseDocId);
                                        }
                                    });
                        }
                    });
        } else {
            // Denuncia en curso
            db.collection("cursos")
                    .whereEqualTo("idCurso", denuncia.getIdCurso())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(cursoSnapshot -> {
                        if (!cursoSnapshot.isEmpty()) {
                            Curso curso = cursoSnapshot.getDocuments().get(0).toObject(Curso.class);
                            String cursoDocId = cursoSnapshot.getDocuments().get(0).getId();
                            Comentario comentarioObj = curso.getComentarios().get(denuncia.getIdComentario());
                            String textoComentario = comentarioObj != null ? comentarioObj.getTexto() : "Comentario no encontrado";

                            holder.identificador_comentario.setText(curso.getTitulo());

                            db.collection("usuarios")
                                    .whereEqualTo("correo", curso.getCreador())
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(userSnapshot -> {
                                        if (!userSnapshot.isEmpty()) {
                                            Usuario user = userSnapshot.getDocuments().get(0).toObject(Usuario.class);
                                            holder.autor.setText(user.getNombre());
                                        } else {
                                            holder.autor.setText("Autor desconocido");
                                        }
                                    });

                            configurarDialogo(holder, denuncia, textoComentario, false, cursoDocId);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    private void configurarDialogo(ViewHolder holder, Denuncia denuncia, String textoComentario, boolean esClase, String documentoId) {
        View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_vercomentario_denunciado_admin, null);
        TextView tipoDenuncia = dialogView.findViewById(R.id.txt_vertipo_denuncia_comentario_admin);
        TextView denunciaTexto = dialogView.findViewById(R.id.et_vermensaje_denuncia_comentario_admin);
        TextView comentario = dialogView.findViewById(R.id.et_vercomentario_denuncia_admin);
        Button btnOmitir = dialogView.findViewById(R.id.btn_omitir_dialog_verdenuncia_comentario_admin);
        Button btnEliminar = dialogView.findViewById(R.id.btn_eliminar_dialog_verdenuncia_comentario_admin);

        denunciaTexto.setMovementMethod(new ScrollingMovementMethod());
        comentario.setMovementMethod(new ScrollingMovementMethod());

        tipoDenuncia.setText("Tipo:\n" + denuncia.getTipo_denuncia());
        denunciaTexto.setText("Contenido:\n" + denuncia.getDenuncia());
        comentario.setText(textoComentario);

        AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext())
                .setView(dialogView)
                .create();

        btnOmitir.setOnClickListener(v -> alertDialog.dismiss());

        btnEliminar.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Runnable eliminarDenuncia = () -> {
                db.collection("denuncias")
                        .whereEqualTo("idDenuncia", denuncia.getIdDenuncia())
                        .limit(1)
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            if (!snapshot.isEmpty()) {
                                String docId = snapshot.getDocuments().get(0).getId();
                                db.collection("denuncias").document(docId).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            comentarios.remove(denuncia);
                                            notifyDataSetChanged();
                                            alertDialog.dismiss();
                                        });
                            }
                        });
            };


            if (esClase) {
                db.collection("clases").document(documentoId).get().addOnSuccessListener(doc -> {
                    Clase clase = doc.toObject(Clase.class);
                    if (clase != null && clase.getComentarios() != null) {
                        clase.getComentarios().remove(denuncia.getIdComentario());
                        db.collection("clases").document(documentoId).set(clase).addOnSuccessListener(aVoid -> eliminarDenuncia.run());
                    }
                });
            } else {
                db.collection("cursos").document(documentoId).get().addOnSuccessListener(doc -> {
                    Curso curso = doc.toObject(Curso.class);
                    if (curso != null && curso.getComentarios() != null) {
                        curso.getComentarios().remove(denuncia.getIdComentario());
                        db.collection("cursos").document(documentoId).set(curso).addOnSuccessListener(aVoid -> eliminarDenuncia.run());
                    }
                });
            }
        });

        alertDialog.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView identificador_comentario;
        TextView autor;
        TextView fecha;
        TextView tipo;
        ImageView imagen;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            identificador_comentario = itemView.findViewById(R.id.txt_holder_denuncias_comentario_identificador_comentario);
            autor = itemView.findViewById(R.id.txt_holder_denuncias_comentario_autor);
            fecha = itemView.findViewById(R.id.txt_holder_denuncias_comentariofecha);
            tipo = itemView.findViewById(R.id.txt_holder_denuncias_comentario_tipo);
            imagen = itemView.findViewById(R.id.img_holder_denuncias_perfil_imagen);
            card = itemView.findViewById(R.id.card_holder_denuncias_comentario);
        }
    }
}
