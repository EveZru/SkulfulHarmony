package com.example.skulfulharmony.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.CrearDenuncia;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdapterVerClaseVerComentarios extends RecyclerView.Adapter<AdapterVerClaseVerComentarios.ViewHolder> {
    List<Comentario> listaComentarios;
    Integer idClase;
    Integer idCurso;

    public AdapterVerClaseVerComentarios(List<Comentario> comentarios, Integer idCurso, Integer idClase) {
        this.listaComentarios = comentarios;
        this.idClase = idClase;
        this.idCurso = idCurso;
    }

    @NonNull
    @Override
    public AdapterVerClaseVerComentarios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_verlista_comentarios, parent, false);
        return new AdapterVerClaseVerComentarios.ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterVerClaseVerComentarios.ViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);
        holder.bind(comentario, position);
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_comentario_usuario;
        TextView txt_comentario_fecha;
        TextView txt_comentario_texto;
        ImageView img_comentario_perfil;
        ImageView img_comentario_megusta;
        TextView txt_comentario_megusta_cantidad;
        TextView txt_comentario_reportar;

        GestureDetectorCompat gestureDetector;
        Context context;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;

            txt_comentario_usuario = itemView.findViewById(R.id.txt_comentario_usuario);
            txt_comentario_fecha = itemView.findViewById(R.id.txt_comentario_fecha);
            txt_comentario_texto = itemView.findViewById(R.id.txt_comentario_texto);
            img_comentario_perfil = itemView.findViewById(R.id.img_comentario_perfil);
            img_comentario_megusta = itemView.findViewById(R.id.img_comentario_megusta);
            txt_comentario_megusta_cantidad = itemView.findViewById(R.id.txt_comentario_megusta_cantidad);
            txt_comentario_reportar = itemView.findViewById(R.id.txt_comentario_reportar);
        }

        public void bind(Comentario comentario, int position) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String correoUsuario = user != null ? user.getEmail() : "";

            txt_comentario_usuario.setText(comentario.getUsuario());
            txt_comentario_fecha.setText(comentario.getFecha().toDate().toString());
            txt_comentario_texto.setText(comentario.getTexto());

            // Reacciones
            int cantidad = comentario.getReacciones() == null ? 0 : comentario.getReacciones().size();
            txt_comentario_megusta_cantidad.setText(String.valueOf(cantidad));

            if (comentario.getReacciones() != null && comentario.getReacciones().contains(correoUsuario)) {
                img_comentario_megusta.setImageResource(R.drawable.heart_red);
            } else {
                img_comentario_megusta.setImageResource(R.drawable.heart_corner);
            }

            // Foto y nombre de usuario
            db.collection("usuarios")
                    .whereEqualTo("correo", comentario.getUsuario())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            String nombre = snapshot.getDocuments().get(0).getString("nombre");
                            String foto = snapshot.getDocuments().get(0).getString("fotoPerfil");
                            txt_comentario_usuario.setText(nombre);
                            Glide.with(context)
                                    .load(foto)
                                    .placeholder(R.drawable.loading)
                                    .error(R.drawable.default_profile)
                                    .into(img_comentario_perfil);
                            } else {
                            txt_comentario_usuario.setText("Usuario desconocido");
                        }
                    }).addOnFailureListener(e -> {
                        txt_comentario_usuario.setText("Usuario desconocido");
                    });

            txt_comentario_reportar.setOnClickListener( view -> {
                if(user != null){
                    Intent intent = new Intent(itemView.getContext(), CrearDenuncia.class);
                    intent.putExtra("idCurso", idCurso);
                    intent.putExtra("idClase", idClase);
                    intent.putExtra("idComentario", comentario.getIdComentario());
                    itemView.getContext().startActivity(intent);
                }
            });

            img_comentario_megusta.setOnClickListener(view -> {
                if (user != null) {
                    ArrayList<String> reacciones = comentario.getReacciones() == null
                            ? new ArrayList<>()
                            : new ArrayList<>(comentario.getReacciones());

                    if (reacciones.contains(correoUsuario)) {
                        reacciones.remove(correoUsuario);
                        img_comentario_megusta.setImageResource(R.drawable.heart_corner);
                    }else {
                        reacciones.add(correoUsuario);
                        img_comentario_megusta.setImageResource(R.drawable.heart_red);
                    }

                    comentario.setReacciones(reacciones);
                    listaComentarios.set(position, comentario);
                    notifyItemChanged(position);

                    db.collection("clases")
                            .whereEqualTo("idCurso", idCurso)
                            .whereEqualTo("idClase", idClase)
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                if (!snapshot.isEmpty()) {
                                    String id = snapshot.getDocuments().get(0).getId();
                                    db.collection("clases")
                                            .document(id)
                                            .update("comentarios", listaComentarios);

                                }
                            }).addOnFailureListener(e -> {
                                txt_comentario_usuario.setText("Usuario desconocido");
                            });
                }
            });

            gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (user != null && comentario.getUsuario().equals(user.getEmail())) {
                        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_comentario, null);
                        EditText editTextComentario = dialogView.findViewById(R.id.et_editar_comentario_vercurso);
                        Button btn_cancelar = dialogView.findViewById(R.id.btn_cancelar_dialogcalificacionpreguntas);
                        Button btn_aceptar = dialogView.findViewById(R.id.btn_aceptar_dialogcalificacionpreguntas);
                        editTextComentario.setText(comentario.getTexto());

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setView(dialogView)
                                .create();

                        btn_aceptar.setOnClickListener(view -> {
                            String nuevoTexto = editTextComentario.getText().toString().trim();
                            comentario.setTexto(nuevoTexto);
                            listaComentarios.set(position, comentario);
                            notifyItemChanged(position);

                            db.collection("clases")
                                    .whereEqualTo("idCurso", idCurso)
                                    .whereEqualTo("idClase", idClase)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        if (!snapshot.isEmpty()) {
                                            String id = snapshot.getDocuments().get(0).getId();
                                            db.collection("clases")
                                                    .document(id)
                                                    .update("comentarios", listaComentarios);
                                        }
                                    });
                            dialog.dismiss();
                        });
                        btn_cancelar.setOnClickListener(view -> {
                            dialog.dismiss();
                        });

                        dialog.show();
                    }
                    return true;
                }

                @Override
                public void onLongPress(@NonNull MotionEvent e) {
                    if(user != null){
                        View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_preguntar_denunciarcomentario,null);
                        TextView usuario = dialogView.findViewById(R.id.txt_verusario_denunciar_comentario);
                        TextView mensajecom = dialogView.findViewById(R.id.txt_vermensaje_denunciar_comentario);
                        Button cancelardialog = dialogView.findViewById(R.id.btn_cancelar_dialog_denunciar_comentario);
                        Button aceptardialog = dialogView.findViewById(R.id.btn_enviar_dialog_denunciar_comentario);

                        mensajecom.setMovementMethod(new ScrollingMovementMethod());
                        usuario.setText(comentario.getUsuario());
                        mensajecom.setText(comentario.getTexto());

                        AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                                .setView(dialogView)
                                .create();


                        cancelardialog.setOnClickListener( view -> {
                            alertDialog.dismiss();
                        });

                        aceptardialog.setOnClickListener( view -> {
                            Intent intent = new Intent(itemView.getContext(), CrearDenuncia.class);
                            intent.putExtra("idCurso", idCurso);
                            intent.putExtra("idClase", idClase);
                            intent.putExtra("idComentario", comentario.getIdComentario());
                            itemView.getContext().startActivity(intent);
                            alertDialog.dismiss();
                        });
                        alertDialog.show();
                    }
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (user != null) {
                        ArrayList<String> reacciones = comentario.getReacciones() == null
                                ? new ArrayList<>()
                                : new ArrayList<>(comentario.getReacciones());

                        if (reacciones.contains(correoUsuario)) {
                            reacciones.remove(correoUsuario);
                            img_comentario_megusta.setImageResource(R.drawable.heart_corner);
                        } else {
                            reacciones.add(correoUsuario);
                            img_comentario_megusta.setImageResource(R.drawable.heart_red);
                        }

                        comentario.setReacciones(reacciones);
                        listaComentarios.set(position, comentario);
                        notifyItemChanged(position);

                        db.collection("clases")
                                .whereEqualTo("idCurso", idCurso)
                                .whereEqualTo("idClase", idClase)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (!snapshot.isEmpty()) {
                                        String id = snapshot.getDocuments().get(0).getId();
                                        db.collection("clases")
                                                .document(id)
                                                .update("comentarios", listaComentarios);
                                    }
                                    })
                                .addOnFailureListener(ex -> {
                                    txt_comentario_usuario.setText("Usuario desconocido");
                                });
                    }
                    return true;
                }

            });

            itemView.setClickable(true);
            itemView.setFocusable(true);
            itemView.setFocusableInTouchMode(true);
            itemView.setLongClickable(true); // Necesario para que funcione el double tap

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }

    }
}
