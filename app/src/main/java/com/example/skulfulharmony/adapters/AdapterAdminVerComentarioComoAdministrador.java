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

    private List<Denuncia> comentarios = new ArrayList<>();

    public AdapterAdminVerComentarioComoAdministrador(List<Denuncia> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_verdenuncia_comentario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Denuncia denuncia = comentarios.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        holder.tipo.setText(denuncia.getTipo_denuncia());

        if (denuncia.getFecha_denuncia() != null) {
            holder.fecha.setText(denuncia.getFecha_denuncia().toDate().toString());
        } else {
            holder.fecha.setText("Fecha desconocida");
        }

        // Color por tipo
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

        // Validar formato
        if (!"comentario".equals(denuncia.getFormato())) {
            holder.identificador_comentario.setText("⚠️ Formato inválido");
            return;
        }

        // Primero obtener el comentario
        db.collection("comentarios")
                .whereEqualTo("idComentario", denuncia.getIdComentario())
                .limit(1)
                .get()
                .addOnSuccessListener(comentarioSnapshot -> {
                    String textoComentario;
                    if (!comentarioSnapshot.isEmpty()) {
                        Comentario comentario = comentarioSnapshot.getDocuments().get(0).toObject(Comentario.class);
                        textoComentario = comentario != null ? comentario.getTexto() : "Comentario vacío";
                    } else {
                        textoComentario = "Comentario no encontrado";
                    }

                    // Comentario dentro de una CLASE
                    if (denuncia.getIdClase() != -1) {
                        db.collection("clases")
                                .whereEqualTo("idCurso", denuncia.getIdCurso())
                                .whereEqualTo("idClase", denuncia.getIdClase())
                                .limit(1)
                                .get()
                                .addOnSuccessListener(claseSnapshot -> {
                                    if (!claseSnapshot.isEmpty()) {
                                        Clase clase = claseSnapshot.getDocuments().get(0).toObject(Clase.class);
                                        String claseDocId = claseSnapshot.getDocuments().get(0).getId();

                                        db.collection("cursos")
                                                .whereEqualTo("idCurso", clase.getIdCurso())
                                                .limit(1)
                                                .get()
                                                .addOnSuccessListener(cursoSnapshot -> {
                                                    if (!cursoSnapshot.isEmpty()) {
                                                        Curso curso = cursoSnapshot.getDocuments().get(0).toObject(Curso.class);
                                                        holder.identificador_comentario.setText("#" + clase.getIdClase() + ": " + curso.getTitulo());

                                                        db.collection("usuarios")
                                                                .whereEqualTo("correo", curso.getCreador())
                                                                .limit(1)
                                                                .get()
                                                                .addOnSuccessListener(userSnapshot -> {
                                                                    if (!userSnapshot.isEmpty()) {
                                                                        Usuario user = userSnapshot.getDocuments().get(0).toObject(Usuario.class);
                                                                        holder.autor.setText(user != null ? user.getNombre() : "Autor desconocido");
                                                                    } else {
                                                                        holder.autor.setText("Autor desconocido");
                                                                    }
                                                                });

                                                        holder.itemView.setOnClickListener(v -> {
                                                            configurarDialogo(holder, denuncia, textoComentario, true, claseDocId);
                                                        });
                                                    }
                                                });
                                    }
                                });

                    } else { // Comentario en CURSO
                        db.collection("cursos")
                                .whereEqualTo("idCurso", denuncia.getIdCurso())
                                .limit(1)
                                .get()
                                .addOnSuccessListener(cursoSnapshot -> {
                                    if (!cursoSnapshot.isEmpty()) {
                                        Curso curso = cursoSnapshot.getDocuments().get(0).toObject(Curso.class);
                                        String cursoDocId = cursoSnapshot.getDocuments().get(0).getId();

                                        holder.identificador_comentario.setText(curso.getTitulo());

                                        db.collection("usuarios")
                                                .whereEqualTo("correo", curso.getCreador())
                                                .limit(1)
                                                .get()
                                                .addOnSuccessListener(userSnapshot -> {
                                                    if (!userSnapshot.isEmpty()) {
                                                        Usuario user = userSnapshot.getDocuments().get(0).toObject(Usuario.class);
                                                        holder.autor.setText(user != null ? user.getNombre() : curso.getCreador());
                                                    } else {
                                                        holder.autor.setText("Autor desconocido");
                                                    }
                                                });

                                        holder.itemView.setOnClickListener(v -> {
                                            configurarDialogo(holder, denuncia, textoComentario, false, cursoDocId);
                                        });
                                    }
                                });
                    }

                }); // Fin comentario
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

        tipoDenuncia.setText("Tipo:\n" + denuncia.getTipo_denuncia());
        denunciaTexto.setText("Contenido:\n" + denuncia.getDenuncia());
        comentario.setText(textoComentario);

        denunciaTexto.setMovementMethod(new ScrollingMovementMethod());
        comentario.setMovementMethod(new ScrollingMovementMethod());

        AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext())
                .setView(dialogView)
                .create();

        btnOmitir.setOnClickListener(v -> alertDialog.dismiss());

        btnEliminar.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("denuncias")
                    .whereEqualTo("idDenuncia", denuncia.getIdDenuncia())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            String docId = snapshot.getDocuments().get(0).getId();
                            Denuncia denunciad = snapshot.getDocuments().get(0).toObject(Denuncia.class);
                            db.collection("comentarios")
                                            .whereEqualTo("idComentario", denunciad.getIdComentario())
                                            .limit(1)
                                            .get()
                                            .addOnSuccessListener(comentarioSnapshot -> {
                                                if (!comentarioSnapshot.isEmpty()) {
                                                    String docIdComentario = comentarioSnapshot.getDocuments().get(0).getId();
                                                    db.collection("comentarios").document(docIdComentario).delete()
                                                            .addOnSuccessListener( unused -> {
                                                                db.collection("denuncias").document(docId).delete()
                                                                        .addOnSuccessListener(aVoid -> {
                                                                            comentarios.remove(denuncia);
                                                                            notifyDataSetChanged();
                                                                            alertDialog.dismiss();
                                                                        });
                                                            });
                                                }
                                            });

                        }
                    });

        });

        alertDialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView identificador_comentario, autor, fecha, tipo;
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
