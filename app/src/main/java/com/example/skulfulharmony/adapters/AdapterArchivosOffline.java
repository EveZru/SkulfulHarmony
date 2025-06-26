package com.example.skulfulharmony.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;

import java.io.File;
import java.util.List;

public class AdapterArchivosOffline extends RecyclerView.Adapter<AdapterArchivosOffline.ViewHolder> {

    private final List<String> archivos;
    private final Context context;

    public AdapterArchivosOffline(List<String> archivos, Context context) {
        this.archivos = archivos;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreArchivo;
        public ViewHolder(View itemView) {
            super(itemView);
            nombreArchivo = itemView.findViewById(R.id.tv_nombre_archivo_offline);
        }
    }

    @NonNull
    @Override
    public AdapterArchivosOffline.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archivo_offline, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterArchivosOffline.ViewHolder holder, int position) {
        String path = archivos.get(position);
        File archivo = new File(path);

        if (archivo.exists()) {
            holder.nombreArchivo.setText(archivo.getName());

            holder.itemView.setOnClickListener(v -> {
                try {
                    Uri uri = FileProvider.getUriForFile(
                            context,
                            context.getPackageName() + ".provider",
                            archivo
                    );

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, getMimeType(uri.toString()));
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No hay apps instaladas para abrir este archivo", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, "No se pudo abrir el archivo: " + archivo.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.nombreArchivo.setText("Archivo no disponible");
            holder.itemView.setOnClickListener(v ->
                    Toast.makeText(context, "Este archivo ya no existe en el dispositivo", Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public int getItemCount() {
        return archivos.size();
    }

    private String getMimeType(String url) {
        String type = "*/*";
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
            if (mime != null) type = mime;
        }
        return type;
    }
}