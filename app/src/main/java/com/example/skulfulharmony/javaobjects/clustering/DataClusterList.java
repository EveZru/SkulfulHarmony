package com.example.skulfulharmony.javaobjects.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataClusterList {
    public static final ArrayList<Map<String, Integer>> listaInstrumentos = new ArrayList<>() {{
        add(new HashMap<>() {{put("Guitarra", 0);}});
        add(new HashMap<>() {{put("Bajo", 1);}});
        add(new HashMap<>() {{put("Flauta", 2);}});
        add(new HashMap<>() {{put("Trompeta", 3);}});
        add(new HashMap<>() {{put("Batería", 4);}});
        add(new HashMap<>() {{put("Piano", 5);}});
        add(new HashMap<>() {{put("Ukelele", 6);}});
        add(new HashMap<>() {{put("Violin", 7);}});
        add(new HashMap<>() {{put("Canto", 8);}});
        add(new HashMap<>() {{put("Otro", 9);}});
    }};
    public static final ArrayList<Map<String, Integer>> listaDificultad = new ArrayList<>() {{
        add(new HashMap<>() {{put("Principiante", 0);}});
        add(new HashMap<>() {{put("Intermedio", 1);}});
        add(new HashMap<>() {{put("Avanzado", 2);}});
    }};
    public static final ArrayList<Map<String, Integer>> listaGenero = new ArrayList<>() {{
        add(new HashMap<>() {{put("Pop", 0);}});
        add(new HashMap<>() {{put("Rock", 1);}});
        add(new HashMap<>() {{put("Hiphop/Rap", 2);}});
        add(new HashMap<>() {{put("Electronica", 3);}});
        add(new HashMap<>() {{put("Jazz", 4);}});
        add(new HashMap<>() {{put("Blues", 5);}});
        add(new HashMap<>() {{put("Reggaeton", 6);}});
        add(new HashMap<>() {{put("Reggae", 7);}});
        add(new HashMap<>() {{put("Clasica", 8);}});
        add(new HashMap<>() {{put("Country", 9);}});
        add(new HashMap<>() {{put("Metal", 10);}});
        add(new HashMap<>() {{put("Folk", 11);}});
        add(new HashMap<>() {{put("Independiente", 12);}});
    }};
}
