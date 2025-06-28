package com.example.skulfulharmony.fragments;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.skulfulharmony.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class FragmentInstrumentosPopulares extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        List<Integer> colores = new ArrayList<>();
        colores.add(ContextCompat.getColor(requireContext(), R.color.rojo_oscuro));
        colores.add(ContextCompat.getColor(requireContext(), R.color.tinto));
        colores.add(ContextCompat.getColor(requireContext(), R.color.rojo));
        colores.add(ContextCompat.getColor(requireContext(), R.color.grayrojo2));
        colores.add(ContextCompat.getColor(requireContext(), R.color.verde));
        colores.add(ContextCompat.getColor(requireContext(), R.color.grayverde2));
        colores.add(ContextCompat.getColor(requireContext(), R.color.cafe3));
        PieChart pieChart = view.findViewById(R.id.pieChart);


        db.collection("cursos").get().addOnSuccessListener(snapshot -> {
            Map<String, Double> instrumentos = new HashMap<>();

            for (DocumentSnapshot doc : snapshot) {
                Double popularidad = doc.getDouble("popularidad");
                Map<String, Long> instrumento = (Map<String, Long>) doc.get("instrumento");

                if (instrumento != null && !instrumento.isEmpty()) {
                    String nombre = instrumento.keySet().iterator().next();
                    instrumentos.merge(nombre, popularidad != null ? popularidad : 0.0, Double::sum);
                }
            }

            List<PieEntry> entries = new ArrayList<>();
            instrumentos.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(7)
                    .forEach(e -> entries.add(new PieEntry(e.getValue().floatValue(), e.getKey())));

            PieDataSet dataSet = new PieDataSet(entries, "Instrumentos Populares");
            dataSet.setColors(colores);
            dataSet.setValueTextColor(Color.WHITE); // para que se vean los valores
            dataSet.setValueTextSize(14f);
            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.invalidate();
        });

        return view;
    }
}
