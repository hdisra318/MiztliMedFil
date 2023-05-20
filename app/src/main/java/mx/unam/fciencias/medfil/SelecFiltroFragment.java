package mx.unam.fciencias.medfil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    /** Referencia al dialogo de bienvenida */
    private DialogoPersonalizado dialogoPersonalizado;

    /** Bandera que indica si ya se mostro por primera vez en la aplicacion el dialogo */
    boolean firstStart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        firstStart= preferences.getBoolean("firstStart", true);

        if (firstStart) {
            if (dialogoPersonalizado == null) {
                dialogoPersonalizado = new DialogoPersonalizado();
            }
            dialogoPersonalizado.show(getActivity().getSupportFragmentManager(), "FOSS");

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }

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