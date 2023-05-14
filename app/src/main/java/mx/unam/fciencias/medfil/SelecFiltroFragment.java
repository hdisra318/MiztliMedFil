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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Mostrando el fragmento del filtro
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FiltroFragment()).commit();

            }
        });

        // Boton de la informacion
        ImageButton infoBtn = (ImageButton) view.findViewById(R.id.info_btn);

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });


        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Muestra el dialogo con la informacion de la enfermedad/condicion actual
     */
    private void showBottomDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filtro_info_layout);

        ImageView cancelBtn = dialog.findViewById(R.id.cancelButton);
        LinearLayout infoText = dialog.findViewById(R.id.layoutInfoText);
        TextView textoEnfermedad = dialog.findViewById(R.id.textoEnf);
        TextView nombreEnfermedad = dialog.findViewById(R.id.nombreEnf);

        // Agregando texto de la informacion de la enferemdad
        nombreEnfermedad.setText("Lúpica");

        String info = "Se presenta en el lupus vulgar y en menos frecuencia, en el lupus eritematoso\n"+
                "Características:\n\n"+
                "1. Eritema malar: eritema fijo, plano o elevado, sobre las prominencias malares, sin afectación de los pliegues nasolabiales.\n"+
                "2. Erupción discoide: placas eritematosas elevadas con descamación queratósica adherente; en lesiones antiguas, puede ocurrir cicatrización atrófica.\n"+
                "3. Fotosensibilidad: erupción cutánea como resultado de una reacción inusual a los rayos solares, por historia u observación del médico.\n"+
                "4. Úlceras orales: ulceración oral o nasofaríngea, usualmente indolora, observada por el médico.\n";

        textoEnfermedad.setText(info);

        // Accion del boton para quitar el dialogo
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }


}