package mx.unam.fciencias.medfil;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SelecFiltroFragment extends Fragment {

    /** Vista a inflar */
    View view;

    /** Boton para abrir un filtro */
    Button btn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_selec_filtro, container, false);

        btn = (Button) view.findViewById(R.id.button);
        Intent intent = new Intent(this.getActivity(), FiltroActivity.class);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FiltroFragment()).commit();
                //startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }



}