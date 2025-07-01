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
import com.google.android.material.tabs.TabLayout;
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
    private TabLayout tabLayout;

    private static final int REQUEST_VER_CURSO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);

        rvDescargados = findViewById(R.id.rv_bliblioteca);
        tabLayout = findViewById(R.id.tab_layout);
        rvDescargados.setLayoutManager(new LinearLayoutManager(this));

        tabLayout.addTab(tabLayout.newTab().setText("Seguidos"));
        tabLayout.addTab(tabLayout.newTab().setText("Historial"));
        tabLayout.addTab(tabLayout.newTab().setText("Descargas"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        cargarCursosSeguidos();
                        break;
                    case 1:
                        cargarCursosHistorial();
                        break;
                    case 2:
                        cargarCursosDescargados();
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) mostrarDialogoOrdenSeguidos();
            }
        });

        cargarCursosSeguidos();

        bottomNavigationView = findViewById(R.id.barra_navegacion);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.it_homme) {
                    startActivity(new Intent(Biblioteca.this, Home.class));
                    finish();
                    return true;
                } else if (itemId == R.id.it_new) {
                    startActivity(new Intent(Biblioteca.this, VerCursosCreados.class));
                    finish();
                    return true;
                } else if (itemId == R.id.it_seguidos) {
                    return true;
                } else if (itemId == R.id.it_perfil) {
                    startActivity(new Intent(Biblioteca.this, Perfil.class));
                    finish();
                    return true;
                }
                return false;
            });
            bottomNavigationView.setSelectedItemId(R.id.it_seguidos);
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
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            Usuario usuario = snapshot.getDocuments().get(0).toObject(Usuario.class);
                            List<Curso> cursosHistorial = usuario != null ? usuario.getHistorialCursos() : new ArrayList<>();
                            for (Curso curso : cursosHistorial) {
                                if(curso.getTitulo().startsWith("✩♬ ₊˚.\uD83C\uDFA7⋆☾⋆⁺₊✧") || curso.getTitulo().isEmpty() || curso.getTitulo().toLowerCase().startsWith("desabilitado")
                                        || curso.getTitulo() == null || curso.getTitulo().equals("") || curso.getTitulo().equals("null") || curso.getTitulo().equals("NULL"))
                                {
                                    cursosHistorial.remove(curso);
                                }
                            }
                            rvDescargados.setAdapter(new AdapterBibliotecaVerCursosHistorial(cursosHistorial));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Error", "Error al obtener los cursos del historial", e));
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

    private void cargarCursosSeguidos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection("usuarios")
                    .whereEqualTo("correo", user.getEmail())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            Usuario usuario = snapshot.getDocuments().get(0).toObject(Usuario.class);
                            List<Integer> cursosSeguidos = usuario != null && usuario.getCursosSeguidos() != null
                                    ? usuario.getCursosSeguidos() : new ArrayList<>();
                            String orden = obtenerOrdenGuardado();
                            rvDescargados.setAdapter(new AdapterBibliotecaVerCursosActualizacion(cursosSeguidos, orden));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Error", "Error al obtener los cursos seguidos", e));
        }
    }

    private void mostrarDialogoOrdenSeguidos() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_seleccion_orden_biblioteca, null);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView).create();

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
}
