package com.example.skulfulharmony.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.List;

public class AdapterBusquedaUsuarios extends RecyclerView.Adapter<AdapterBusquedaUsuarios.PerfilViewHolder> {

    private List<Usuario> usuarios;
    private OnItemClickListener onItemClickListener;

    public AdapterBusquedaUsuarios(List<Usuario> usuarios, OnItemClickListener listener) {
        this.usuarios = usuarios;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_perfil_mostrar, parent, false);
        return new PerfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.nombreUsuario.setText(usuario.getNombre());
        holder.descripcionUsuario.setText(usuario.getDescripcion());
        holder.infoUsuario.setText(usuario.getCursos() + " cursos | " + usuario.getSeguidores() + " seguidores");

        // Cargar la foto de perfil utilizando Glide
        Glide.with(holder.itemView)
                .load(usuario.getFotoPerfil())  // Usamos la URL de la foto de perfil de Firebase
                .placeholder(R.drawable.default_profile)  // Imagen predeterminada si no hay foto
                .into(holder.imagenPerfil);

        // Configurar el click listener
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(usuario);  // Pasa el usuario al hacer clic
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class PerfilViewHolder extends RecyclerView.ViewHolder {
        TextView nombreUsuario, descripcionUsuario, infoUsuario;
        ImageView imagenPerfil;

        public PerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenPerfil = itemView.findViewById(R.id.iv_perfil);
            nombreUsuario = itemView.findViewById(R.id.tv_nombre_usuario);
            descripcionUsuario = itemView.findViewById(R.id.tv_descripcion_usuario);
            infoUsuario = itemView.findViewById(R.id.tv_info_usuario);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Usuario usuario); // Maneja el clic en un perfil de usuario
    }
}