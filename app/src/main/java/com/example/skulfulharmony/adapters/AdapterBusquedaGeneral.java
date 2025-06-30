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
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.List;

public class AdapterBusquedaGeneral extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CURSO = 0;
    private static final int TYPE_USUARIO = 1;
    private static final int TYPE_CLASE = 2;
    private List<Object> items;
    private OnItemClickListener listener;

    public AdapterBusquedaGeneral(List<Object> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Curso) {
            return TYPE_CURSO;
        } else if (item instanceof Usuario) {
            return TYPE_USUARIO;
        } else if (item instanceof Clase) {
            return TYPE_CLASE;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CURSO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_busqueda_general, parent, false);
            return new CursoViewHolder(view);
        } else if (viewType == TYPE_USUARIO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_busqueda_persona, parent, false);
            return new UsuarioViewHolder(view);
        } else if (viewType == TYPE_CLASE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_busqueda_clase, parent, false);
            return new ClaseViewHolder(view);
        }
        throw new IllegalArgumentException("Tipo de vista desconocido");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof CursoViewHolder && item instanceof Curso) {
            ((CursoViewHolder) holder).bind((Curso) item, listener);
        } else if (holder instanceof UsuarioViewHolder && item instanceof Usuario) {
            ((UsuarioViewHolder) holder).bind((Usuario) item, listener);
        } else if (holder instanceof ClaseViewHolder && item instanceof Clase) {
            ((ClaseViewHolder) holder).bind((Clase) item, listener, items);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

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

            if (curso.getImagen() != null && !curso.getImagen().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(curso.getImagen())
                        .placeholder(R.drawable.logo_sh)
                        .centerCrop()
                        .into(imgBusquedaItem);
            } else {
                imgBusquedaItem.setImageResource(R.drawable.logo_sh);
            }

            itemView.setOnClickListener(v -> listener.onCursoClick(curso));
        }
    }
    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        ImageView imgPerfil;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txt_nombre_persona_busqueda);
            imgPerfil = itemView.findViewById(R.id.img_holder_busqueda_imagenperfil);
        }

        public void bind(Usuario usuario, OnItemClickListener listener) {
            txtNombre.setText(usuario.getNombre());
            if (usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(usuario.getFotoPerfil())
                        .placeholder(R.drawable.default_profile)
                        .into(imgPerfil);
            } else {
                imgPerfil.setImageResource(R.drawable.default_profile);
            }

            itemView.setOnClickListener(v -> listener.onUsuarioClick(usuario));
        }
    }
    static class ClaseViewHolder extends RecyclerView.ViewHolder {
        ImageView imgClase;
        TextView txtTituloClase, txtCurso;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            imgClase = itemView.findViewById(R.id.img_holder_busqueda_imagencurso);
            txtTituloClase = itemView.findViewById(R.id.txt_holder_busqueda_tituloclase);
            txtCurso = itemView.findViewById(R.id.txt_holder_busqueda_titulocurso);
        }

        public void bind(Clase clase, OnItemClickListener listener, List<Object> items) {
            txtTituloClase.setText(clase.getTitulo());

            Curso cursoRelacionado = null;
            for (Object obj : items) {
                if (obj instanceof Curso) {
                    Curso c = (Curso) obj;
                    if (c.getIdCurso() != null && clase.getIdCurso() != null && c.getIdCurso().equals(clase.getIdCurso())) {
                        cursoRelacionado = c;
                        break;
                    }
                }
            }

            if (cursoRelacionado != null) {
                txtCurso.setText(cursoRelacionado.getTitulo() != null ? cursoRelacionado.getTitulo() : "Curso sin título");
                if (cursoRelacionado.getImagen() != null && !cursoRelacionado.getImagen().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(cursoRelacionado.getImagen())
                            .placeholder(R.drawable.logo_sh)
                            .centerCrop()
                            .into(imgClase);
                } else {
                    imgClase.setImageResource(R.drawable.logo_sh);
                }
            } else {
                txtCurso.setText("Curso desconocido");
                imgClase.setImageResource(R.drawable.logo_sh);
            }

            itemView.setOnClickListener(v -> listener.onClaseClick(clase));
        }
    }

    // Interfaz para manejar clicks
    public interface OnItemClickListener {
        void onCursoClick(Curso curso);
        void onUsuarioClick(Usuario usuario);
        void onClaseClick(Clase clase);
    }
}