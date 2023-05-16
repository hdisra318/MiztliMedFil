package mx.unam.fciencias.medfil;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelecFiltroFragment extends Fragment {

    /** Vista a inflar */
    View view;

    /** Grid de la galeria de filtros */
    GridView grid;

    /** Imagenes de los filtros */
    int[] filtros = {R.drawable.filtro1, R.drawable.filtro1, R.drawable.filtro1, R.drawable.filtro1, R.drawable.filtro1};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_selec_filtro, container, false);

        grid = (GridView) view.findViewById(R.id.gridView);
        GridAdapter gridAdapter = new GridAdapter(getContext(), filtros);
        grid.setAdapter(gridAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Mostrando el fragmento del filtro
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FiltroFragment()).commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }



}