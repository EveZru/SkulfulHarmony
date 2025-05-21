package com.example.skulfulharmony.adapters;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.List;

public class AdapterBusquedaGeneral extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CURSO = 0;
    private static final int TYPE_USUARIO = 1;

    private List<Object> items; // Puede ser Curso o Usuario
    private OnItemClickListener listener;

    public AdapterBusquedaGeneral(List<Object> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Curso) {
            return TYPE_CURSO;
        } else if (items.get(position) instanceof Usuario) {
            return TYPE_USUARIO;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CURSO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_busqueda_general, parent, false);
            return new CursoViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_busqueda_persona, parent, false);
            return new UsuarioViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CursoViewHolder) {
            Curso curso = (Curso) items.get(position);
            ((CursoViewHolder) holder).bind(curso, listener);
        } else if (holder instanceof UsuarioViewHolder) {
            Usuario usuario = (Usuario) items.get(position);
            ((UsuarioViewHolder) holder).bind(usuario, listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder para cursos
    static class CursoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBusquedaItem;
        TextView txtTituloBusqueda, txtDetalleBusqueda;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBusquedaItem = itemView.findViewById(R.id.img_busqueda_item);
            txtTituloBusqueda = itemView.findViewById(R.id.txt_titulo_busqueda);
            txtDetalleBusqueda = itemView.findViewById(R.id.txt_detalle_busqueda);
        }

        public void bind(Curso curso, OnItemClickListener listener) {
            txtTituloBusqueda.setText(curso.getTitulo());

            String detalle = "Publicado por: " + (curso.getCreador() != null ? curso.getCreador() : "Desconocido");
            txtDetalleBusqueda.setText(detalle);

            Glide.with(itemView.getContext())
                    .load(curso.getImagen() != null ? curso.getImagen() : "")
                    .placeholder(R.drawable.logo_sh)
                    .centerCrop()
                    .into(imgBusquedaItem);

            itemView.setOnClickListener(v -> listener.onCursoClick(curso));
        }
    }

    // ViewHolder para usuarios
    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        ImageView imgPerfil;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txt_holder_busqueda_nombre);
            imgPerfil = itemView.findViewById(R.id.img_holder_busqueda_imagenperfil);
        }

        public void bind(Usuario usuario, OnItemClickListener listener) {
            txtNombre.setText(usuario.getNombre());
            Glide.with(itemView.getContext())
                    .load(usuario.getFotoPerfil())  // Usar fotoPerfil aquí
                    .placeholder(R.drawable.default_profile)
                    .into(imgPerfil);

            itemView.setOnClickListener(v -> listener.onUsuarioClick(usuario));
        }
    }

    // Interfaz para manejar clicks
    public interface OnItemClickListener {
        void onCursoClick(Curso curso);
        void onUsuarioClick(Usuario usuario);
    }
}