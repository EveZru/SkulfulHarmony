package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterBibliotecaVerCursosActualizacion;
import com.example.skulfulharmony.adapters.AdapterBibliotecaVerCursosHistorial;
import com.example.skulfulharmony.adapters.AdapterCursosDescargados;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Biblioteca extends AppCompatActivity {

    private static final String PREF_ORDEN_CURSOS = "orden_cursos_seguidos";
    private static final String KEY_ORDEN = "orden";

    private DbUser dbUser = new DbUser(this);
    private BottomNavigationView bottomNavigationView;

    private RecyclerView rvDescargados;
    private Button btnVerDescargas;
    private Button btnVerSeguidos;
    private Button btnVerHistorial;

    private static final int REQUEST_VER_CURSO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);

        rvDescargados = findViewById(R.id.rv_bliblioteca);
        btnVerDescargas = findViewById(R.id.btn_gotodescargados);
        btnVerSeguidos = findViewById(R.id.btn_gotoseguidos);
        btnVerHistorial = findViewById(R.id.btn_gotohistorial);

        rvDescargados.setLayoutManager(new LinearLayoutManager(this));
        btnVerDescargas.setOnClickListener(v -> {
            resaltarBotonSeleccionado(btnVerDescargas);
            cargarCursosDescargados();
        });
        btnVerSeguidos.setOnClickListener(v -> {
            resaltarBotonSeleccionado(btnVerSeguidos);
            cargarCursosSeguidos();
        });
        btnVerSeguidos.setOnLongClickListener(v -> {
            resaltarBotonSeleccionado(btnVerSeguidos);
            mostrarDialogoOrdenSeguidos();
            return true;
        });
        btnVerHistorial.setOnClickListener(v -> {
            resaltarBotonSeleccionado(btnVerHistorial);
            cargarCursosHistorial();
        });

        resaltarBotonSeleccionado(btnVerSeguidos);
        cargarCursosSeguidos();

        bottomNavigationView = findViewById(R.id.barra_navegacion);


        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.it_homme) {
                    startActivity(new Intent(Biblioteca.this, Home.class));
                    return true;
                } else if (itemId == R.id.it_new) {
                    startActivity(new Intent(Biblioteca.this, VerCursosCreados.class));
                    return true;
                } else if (itemId == R.id.it_seguidos) {
                    return true;
                } else if (itemId == R.id.it_perfil) {
                    startActivity(new Intent(Biblioteca.this, Perfil.class));
                    return true;
                }
                return false;
            });

            bottomNavigationView.setSelectedItemId(R.id.it_seguidos);
        } else {
            Log.e("Error", "La vista BottomNavigationView no se ha encontrado");
        }
    }

    private void cargarCursosHistorial() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            db.collection("usuarios")
                    .whereEqualTo("correo", user.getEmail())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(onQuerySnapshot -> {
                        if (!onQuerySnapshot.isEmpty()) {
                            Usuario usuario = onQuerySnapshot.getDocuments().get(0).toObject(Usuario.class);
                            List<Curso> cursosHistorial = new ArrayList<>();
                            if (usuario != null) {
                                cursosHistorial = usuario.getHistorialCursos();
                            }

                                AdapterBibliotecaVerCursosHistorial adapter = new AdapterBibliotecaVerCursosHistorial(cursosHistorial);
                                rvDescargados.setAdapter(adapter);

                        }
                    })
                    .addOnFailureListener( e -> {
                        Log.e("Error", "Error al obtener los cursos del historial", e);
                    });
        }
    }


    private void cargarCursosDescargados() {
        DbHelper dbHelper = new DbHelper(this);
        List<Curso> cursos = dbHelper.obtenerCursosDescargados();

        AdapterCursosDescargados adapter = new AdapterCursosDescargados(cursos, curso -> {
            Intent intent = new Intent(Biblioteca.this, VerCursoDescargado.class);
            intent.putExtra("curso_id", curso.getId());
            startActivityForResult(intent, REQUEST_VER_CURSO);
        });
        rvDescargados.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VER_CURSO && resultCode == RESULT_OK) {
            cargarCursosDescargados();
        }
    }

    private String obtenerOrdenGuardado() {
        String orden = getSharedPreferences(PREF_ORDEN_CURSOS, MODE_PRIVATE).getString(KEY_ORDEN, null);
        if (orden == null) {
            guardarOrden("popularidad");
            return "popularidad";
        }
        return orden;
    }

    private void guardarOrden(String orden) {
        getSharedPreferences(PREF_ORDEN_CURSOS, MODE_PRIVATE)
                .edit()
                .putString(KEY_ORDEN, orden)
                .apply();
    }

    private void mostrarDialogoOrdenSeguidos() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_seleccion_orden_biblioteca, null);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btn_orden_popularidad).setOnClickListener(v -> {
            guardarOrden("popularidad");
            cargarCursosSeguidos();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_orden_alfabetico).setOnClickListener(v -> {
            guardarOrden("alfabetico");
            cargarCursosSeguidos();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_orden_recientes).setOnClickListener(v -> {
            guardarOrden("recientes");
            cargarCursosSeguidos();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_cancelar_dialog).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void cargarCursosSeguidos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            db.collection("usuarios")
                    .whereEqualTo("correo", user.getEmail())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(onQuerySnapshot -> {
                        if (!onQuerySnapshot.isEmpty()) {
                            Usuario usuario = onQuerySnapshot.getDocuments().get(0).toObject(Usuario.class);
                            List<Integer> cursosSeguidos = usuario != null && usuario.getCursosSeguidos() != null
                                    ? usuario.getCursosSeguidos() : new ArrayList<>();

                            String orden = obtenerOrdenGuardado();
                            AdapterBibliotecaVerCursosActualizacion adapter =
                                    new AdapterBibliotecaVerCursosActualizacion(cursosSeguidos, orden);

                            rvDescargados.setAdapter(adapter);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Error", "Error al obtener los cursos seguidos", e));
        }
    }
    private void resaltarBotonSeleccionado(Button botonSeleccionado) {
        Button[] botones = {btnVerDescargas, btnVerSeguidos, btnVerHistorial};

        for (Button btn : botones) {
            if (btn == botonSeleccionado) {
                btn.setTextColor(getResources().getColor(R.color.rojo_oscuro, getTheme()));
            } else {
                btn.setTextColor(getResources().getColor(R.color.white, getTheme()));
            }
        }
    }
}