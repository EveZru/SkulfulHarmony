package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_clases;

import java.util.List;

public class AdapterArchivosClase extends RecyclerView.Adapter<AdapterArchivosClase.ArchivoViewHolder> {
    private final List<String> archivos;
    private final Context context;

    public AdapterArchivosClase(List<String> archivos, Context context) {
        this.archivos = archivos;
        this.context = context;
    }

    @NonNull
    @Override
    public ArchivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_archivo, parent, false);
        return new ArchivoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivoViewHolder holder, int position) {
        String urlArchivo = archivos.get(position);
        String nombreArchivo = Uri.parse(urlArchivo).getLastPathSegment();

        holder.textViewNombre.setText(nombreArchivo != null ? nombreArchivo : "Archivo " + (position + 1));

        String nombreLimpio = Uri.parse(urlArchivo).getLastPathSegment();
        if (nombreLimpio != null && nombreLimpio.contains(".")) {
            String extension = nombreLimpio.substring(nombreLimpio.lastIndexOf('.') + 1).toLowerCase();

            switch (extension) {
                case "pdf":
                    holder.iconoArchivo.setImageResource(R.drawable.icon_pdf);
                    break;
                case "doc":
                case "docx":
                    holder.iconoArchivo.setImageResource(R.drawable.icon_doc);
                    break;
                default:
                    holder.iconoArchivo.setImageResource(R.drawable.icon_file);
                    break;
            }
        } else {
            holder.iconoArchivo.setImageResource(R.drawable.icon_file);
        }


        holder.itemView.setOnClickListener(v -> {
            try {
                if (context instanceof Ver_clases) {
                    ((Ver_clases) context).abrirArchivoEnWebView(urlArchivo);
                } else {
                    Toast.makeText(context, "No se puede abrir el archivo", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, "Error al intentar abrir el archivo", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return archivos.size();
    }

    public static class ArchivoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;
        ImageView iconoArchivo;

        public ArchivoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.tv_nombre_archivo);
            iconoArchivo = itemView.findViewById(R.id.icono_archivo);
        }
    }
}
