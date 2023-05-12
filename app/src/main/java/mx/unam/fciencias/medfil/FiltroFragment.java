package mx.unam.fciencias.medfil;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.ar.core.AugmentedFace;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFrontFacingFragment;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FiltroFragment extends Fragment {

    /** Referencia al fragmento de SceneView */
    private ArFrontFacingFragment fragmentRostorRA;

    /** Referencia a la escena donde se detectan rostros */
    private ArSceneView vistaEscenaRa;

    /** Tetura a superponer en los rostros */
    private Texture texturaRostro;

    /** Conjunto que guardara los modelos que se van a superponer en los rostros */
    private final Set<CompletableFuture<?>> cargadores = new HashSet<>();

    /** Hashmap que relaciona un rostro detectado con su modelo RA */
    private final HashMap<AugmentedFace, AugmentedFaceNode> nodosRostros = new HashMap<>();

    /** Almacena el modelo 3D */
    private ModelRenderable modeloRostro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filtro, container, false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getSupportFragmentManager().addFragmentOnAttachListener(this::onAttachFragment);

        if(savedInstanceState == null){

            if (Sceneform.isSupported(getActivity())) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.vista_ra_fl_1,
                        ArFrontFacingFragment.class, null).commit();
            }
        }

        // Asociando el boton de captura a los metodos
        ImageButton botonCaptura = getActivity().findViewById(R.id.captura_btn_2);
        botonCaptura.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                makeScreenShot();
            }
        });

        loadTextures();

    }

    /**
     * Realiza la carga de la textura a superponer en el rostro.
     */
    private void loadTextures() {

        // Cargando 1 filtro
        cargadores.add((Texture.builder()).setSource(getActivity(), Uri.parse("freckles.png"))
                .setUsage(Texture.Usage.COLOR_MAP)
                .build()
                .thenAccept(texture -> texturaRostro = texture)
                .exceptionally(throwable -> {
                    Toast.makeText(getActivity(), "No se logro cargar la textura",
                            Toast.LENGTH_LONG).show();
                    return null;
                }));


        // Para agregar mas filtros usar otro .add
    }

    /**
     * Registra los rostros detectados por la camara, revisa si ya fueron procesados y
     * asociarlos con su modelo 3D o texturas. Y libera modelos que hayan salido de
     * la vista de la camara.
     */
    public void onAugmentedFaceTrackingUpdate(AugmentedFace rostroRA) {

        if(texturaRostro == null && modeloRostro == null)
            return;

        AugmentedFaceNode nodoRostroExistente = nodosRostros.get(rostroRA);

        switch (rostroRA.getTrackingState()) {

            case TRACKING:
                if(nodoRostroExistente == null){
                    if(texturaRostro != null) {

                        aumentaTextura(rostroRA);

                    } else {

                        aumentaModelo(rostroRA);

                    }

                    /*AugmentedFaceNode nodoRostro = new AugmentedFaceNode(rostroRA);
                    nodoRostro.setFaceMeshTexture(texturaRostro);
                    vistaEscenaRa.getScene().addChild(nodoRostro);
                    nodosRostros.put(rostroRA, nodoRostro);*/
                }
                break;

            case STOPPED:
                if(nodoRostroExistente != null) {
                    vistaEscenaRa.getScene().removeChild(nodoRostroExistente);
                }

                nodosRostros.remove(rostroRA);
                break;
        }
    }

    /**
     * Metodo que responde a la creacion de la vista del fragmento RA
     */
    public void onViewCreated(ArSceneView arSceneView) {

        vistaEscenaRa = arSceneView;

        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        fragmentRostorRA.setOnAugmentedFaceUpdateListener(this::onAugmentedFaceTrackingUpdate);
    }

    /**
     * Metodo en el que se define el comportamiento cuando se ancla el Fragmento Ar
     * a la actividad
     */
    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {

        if (fragment.getId() == R.id.vista_ra_fl_1) {

            fragmentRostorRA = (ArFrontFacingFragment) fragment;
            fragmentRostorRA.setOnViewCreatedListener(this::onViewCreated);
        }
    }

    /* Liberando los recursos de la app */
    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelaCargadores();
    }

    /**
     * Detiene los cargadores del modelo
     */
    private void cancelaCargadores() {

        for (CompletableFuture<?> cargador : cargadores) {

            if(!cargador.isDone())
                cargador.cancel(true);
        }

    }

    /**
     * Realiza la captura de la vista al presionar el boton
     */
    private void makeScreenShot() {

        final Bitmap bitmap = Bitmap.createBitmap(vistaEscenaRa.getWidth(), vistaEscenaRa.getHeight(),
                Bitmap.Config.ARGB_8888);

        final HandlerThread handlerThread = new HandlerThread("PixelCopier");

        handlerThread.start();
        PixelCopy.request(vistaEscenaRa, bitmap, (copyResult) -> {

            if (copyResult == PixelCopy.SUCCESS) {

                try {

                    saveBitmapLocally(bitmap);

                }catch (IOException ioe) {

                    Toast.makeText(getActivity(), ioe.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getActivity(), "Captura guardada", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(getActivity(), "No se pudo realizazr la captura", Toast.LENGTH_LONG).show();

            }

            handlerThread.quitSafely();

        }, new Handler(handlerThread.getLooper()));


    }

    /**
     * Metodo auxiliar que guarda el Bitmap en una ruta por defecto del dispositivo
     */
    private void saveBitmapLocally(Bitmap bitmap) throws IOException {

        File dirDestino = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/arTestScreenshots");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
        String fechaFormateada =    simpleDateFormat.format(c.getTime());
        File archivoDestino = new File(dirDestino, "capturaAR_"+fechaFormateada+".jpg");
        Files.createDirectories(dirDestino.toPath());
        FileOutputStream fileOutputStream = new FileOutputStream(archivoDestino);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();

    }

    /**
     * Carga los modelos
     */
    private void loadModels() {

        cargadores.add(ModelRenderable.builder()
                .setSource(getActivity(), Uri.parse("fox.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(model -> modeloRostro = model)
                .exceptionally(throwable -> {
                    Toast.makeText(getActivity(), "No pudo cargarse el rendereable",
                            Toast.LENGTH_LONG).show();
                    return null;
                }));

    }


    /**
     * Superpone la textura
     */
    private void aumentaTextura(AugmentedFace rostroRA) {

        AugmentedFaceNode nodoRostro = new AugmentedFaceNode(rostroRA);
        nodoRostro.setFaceMeshTexture(texturaRostro);
        vistaEscenaRa.getScene().addChild(nodoRostro);
        nodosRostros.put(rostroRA, nodoRostro);

    }

    /**
     * Metodo auxiliar para superponer la textura
     */
    private void aumentaModelo(AugmentedFace rostroRA) {

        AugmentedFaceNode nodoRostro = new AugmentedFaceNode(rostroRA);

        RenderableInstance modelo = nodoRostro.setFaceRegionsRenderable(modeloRostro);
        modelo.setShadowCaster(false);
        modelo.setShadowReceiver(true);
        vistaEscenaRa.getScene().addChild(nodoRostro);
        nodosRostros.put(rostroRA, nodoRostro);

    }


    /**
     * Cambia los filtros aplicados
     */
    private void cambiaFiltro() {

        if (texturaRostro == null && modeloRostro == null)
            return;

        cancelaCargadores();


        for(AugmentedFace rostroAumentado : nodosRostros.keySet()) {

            AugmentedFaceNode nodoRostroAR = nodosRostros.remove(rostroAumentado);
            vistaEscenaRa.getScene().removeChild(nodoRostroAR);

        }

        if(texturaRostro != null) {
            texturaRostro = null;
            loadModels();
        } else {
            modeloRostro = null;
            loadTextures();
        }

    }

    /**
     * Cambia el filtro activo
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


}