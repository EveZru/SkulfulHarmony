package com.example.skulfulharmony;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterPreguntasEnCuestionariosparaContestar;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Clase_Fundamentos extends AppCompatActivity {
    private ImageView ivImagenCurso;
    private RecyclerView rvPreguntasCurso;
    private AdapterPreguntasEnCuestionariosparaContestar adapterPreguntas;
    private Button btnComprobar,btnReintentar;
    private List<PreguntaCuestionario> listaPreguntas;

    private static final String TAG = "Clase_Fundamentos"; // Para logs de depuración

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clase_fundamentos);


        ivImagenCurso = findViewById(R.id.iv_imagen_curso);
        rvPreguntasCurso = findViewById(R.id.rv_preguntas_curso);
        rvPreguntasCurso.setLayoutManager(new LinearLayoutManager(this));
        btnComprobar = findViewById(R.id.btn_comprobar);
        btnReintentar = findViewById(R.id.btn_reintentar);

       // listaPreguntas = (List<PreguntaCuestionario>) getIntent().getSerializableExtra("lista_preguntas");
        String nombreCurso = getIntent().getStringExtra("nombre_curso");

      //  A r rayList<Object> Listapreguntas = new ArrayList<>();

        listaPreguntas = obtenerPreguntas(nombreCurso);
        adapterPreguntas = new  AdapterPreguntasEnCuestionariosparaContestar(this, listaPreguntas);

        int imagenResId = obtenerImagenPorCurso(nombreCurso);
        ivImagenCurso.setImageResource(imagenResId);

        rvPreguntasCurso.setAdapter(adapterPreguntas);
//botones para comprobar respuestas
        btnComprobar.setOnClickListener(v -> {

            adapterPreguntas.comprobarRespuestas();
            List<PreguntaCuestionario> incorrectas = adapterPreguntas.getPreguntasIncorrectas();
            btnComprobar.setVisibility(View.GONE);
            btnReintentar.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            SharedPreferences prefs = getSharedPreferences("cuestionario", MODE_PRIVATE);
            String jsonGuardado = prefs.getString("preguntas_incorrectas", null);

// Lista existente
            List<PreguntaCuestionario> preguntasGuardadas;
            if (jsonGuardado != null) {
                Type listType = new TypeToken<List<PreguntaCuestionario>>() {}.getType();
                try{
                    preguntasGuardadas = gson.fromJson(jsonGuardado, listType);
                }catch (Exception e) {
                    Log.e(TAG, "Error al cargar preguntas incorrectas: " + e.getMessage());
                    preguntasGuardadas = new ArrayList<>();
                }

                //  preguntasGuardadas = gson.fromJson(jsonGuardado, new com.google.gson.reflect.TypeToken<List<String>>(){}.getType());
            } else {

               preguntasGuardadas=new ArrayList<>();
            }

// Formatear nuevas preguntas
            for (PreguntaCuestionario pregunta : incorrectas) {
              if(!preguntasGuardadas.contains(pregunta)){
                  preguntasGuardadas.add(pregunta);
              }
            }

// Guardar la lista actualizada
            String jsonNuevo = gson.toJson(preguntasGuardadas);
            prefs.edit().putString("preguntas_incorrectas", jsonNuevo).apply();



        });

        btnReintentar.setOnClickListener(v -> {
            // Aquí puedes implementar la lógica para reintentar el cuestionario
            // Por ejemplo, podrías recrear la Activity o resetear el adapter
            Toast.makeText(this, "Reintentando cuestionario...", Toast.LENGTH_SHORT).show();
            btnComprobar.setVisibility(View.VISIBLE);
            btnReintentar.setVisibility(View.GONE);
            adapterPreguntas.setMostrarResultados(false);

            adapterPreguntas.notifyDataSetChanged();
        });

        }

    private int obtenerImagenPorCurso(String tituloCurso) {
        switch (tituloCurso) {
            case "Curso 1":
                return R.drawable.curso_1;
            case "Curso 2":
                return R.drawable.curso_2;
            case "Curso 3":
                return R.drawable.curso_3;
            case"Curso 4":
                return R.drawable.curso_4;
            case "Curso 5":
                return R.drawable.curso_5;
            case "Curso 6":
                return R.drawable.curso_6;
            default:
                return R.drawable.third_rounder_button;
        }
    }
    private List<PreguntaCuestionario> obtenerPreguntas(String tituloCurso){
        List<PreguntaCuestionario> Preguntas = new ArrayList<>();

        switch (tituloCurso) {
            case  "Curso 1":
                Preguntas.add(new PreguntaCuestionario("¿Cuál clave se usa para tonos altos ?", Arrays.asList("\uD834\uDD1E", "\uD834\uDD22 ","\uD834\uDD21"), 0));
                Preguntas.add(new PreguntaCuestionario("¿Cuál clave se usa para tonos intermedios ?", Arrays.asList("\uD834\uDD1E", "\uD834\uDD22 ","\uD834\uDD21"), 2));
                Preguntas.add(new PreguntaCuestionario("¿Cuál clave se usa para tonos graves?", Arrays.asList("\uD834\uDD1E", "\uD834\uDD22 ","\uD834\uDD21"), 1));


                break;
            case"Curso 2":

            Preguntas.add(new PreguntaCuestionario("¿Cuál es la nota guia en la clave de sol?", Arrays.asList("\uD834\uDD1E sol en el primer espacio", "\uD834\uDD1E sol en la segunda linea de ","\uD834\uDD1Esol en el tercer espacio "), 1));
            Preguntas.add(new PreguntaCuestionario("¿Cuál es la nota guia en la clave de fa?", Arrays.asList("\uD834\uDD22 fa en la cuarta linea ", "\uD834\uDD22 fa en el tercer espacio ","\uD834\uDD22 fa en la tercera linea"), 0));
            Preguntas.add(new PreguntaCuestionario("¿Cuál es la nota guia en la clave de do?", Arrays.asList("\uD834\uDD21do en el tercer espacio", "\uD834\uDD21do el la cuarta linea","\uD834\uDD21 do en la tercer linea "), 2));
            break;
            case "Curso 3":
                Preguntas.add(new PreguntaCuestionario("¿Cuántos tiempos dura una redonda?", Arrays.asList("4 tiempos", "2 tiempos", "1 tiempo"), 0));
                Preguntas.add(new PreguntaCuestionario("¿Qué figura representa 1/2 tiempo?", Arrays.asList("Corchea", "Negra", "Blanca"), 0));
                break;
            case "Curso 4":
               Preguntas.add(new PreguntaCuestionario("¿Cuántas blancas caben en un compás de 4/4?", Arrays.asList("1", "2", "4"), 1));
               Preguntas.add(new PreguntaCuestionario("¿Cuántas negras caben en un compás de 2/4?", Arrays.asList("2", "4", "8"), 0));
                break;
            case "Curso 5":
                Preguntas.add(new PreguntaCuestionario("¿Cuál es el rango de BPM del tempo Andante?", Arrays.asList("76–108 BPM", "120–168 BPM", "66–76 BPM"), 0));
                Preguntas.add(new PreguntaCuestionario("¿Qué tempo es más rápido?", Arrays.asList("Adagio", "Presto", "Largo"), 1));
                break;
            case "Curso 6":
                 Preguntas.add(new PreguntaCuestionario("¿Qué símbolo representa un sostenido?", Arrays.asList(" ♭ ", " ♮ ", " ♯ "), 2));
                 Preguntas.add(new PreguntaCuestionario("¿Qué hace un becuadro?", Arrays.asList("Sube medio tono", "Cancela una alteración previa", "Baja medio tono"), 1));

            default:
               Preguntas.add(new PreguntaCuestionario("error al cargar preguntas", Arrays.asList(".", "."), 1));
                break;
        }
        return Preguntas;
    }

}