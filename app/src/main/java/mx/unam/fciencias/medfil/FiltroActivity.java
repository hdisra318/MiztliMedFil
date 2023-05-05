package mx.unam.fciencias.medfil;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.AugmentedFace;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFrontFacingFragment;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FiltroActivity extends AppCompatActivity {

    /* Referencia al fragmento de SceneView */
    private ArFrontFacingFragment fragmentRostorRA;

    /* Referencia a la escena donde se detectan rostros */
    private ArSceneView vistaEscenaRa;

    /* Tetura a superponer en los rostros */
    private Texture texturaRostro;

    /* Conjunto que guardara los modelos que se van a superponer en los rostros */
    private final Set<CompletableFuture<?>> cargadores = new HashSet<>();

    /* Hashmap que relaciona un rostro detectado con su modelo RA */
    private final HashMap<AugmentedFace, AugmentedFaceNode> nodosRostros = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);
    }

    /**
     * Realiza la carga de la textura a superponer en el rostro.
     */
    private void loadTextures() {

        // Cargando 1 filtro
        cargadores.add((Texture.builder()).setSource(this, Uri.parse("filtro.png"))
                .setUsage(Texture.Usage.COLOR_MAP)
                .build()
                .thenAccept(texture -> texturaRostro = texture)
                .exceptionally(throwable -> {
                    Toast.makeText(this, "No se logro cargar la textura",
                            Toast.LENGTH_LONG).show();
                    return null;
                }));

    }

    /**
     * Registra los rostros detectados por la camara, revisa si ya fueron procesados y
     * asociarlos con su modelo 3D o texturas. Y libera modelos que hayan salido de
     * la vista de la camara.
     */
    public void onAugmentedFaceTrackingUpdate(AugmentedFace rostroRA) {

        if(texturaRostro == null)
            return;

        AugmentedFaceNode nodoRostroExistente = nodosRostros.get(rostroRA);

        switch (rostroRA.getTrackingState()) {

            case TRACKING:
                if(nodoRostroExistente == null){
                    AugmentedFaceNode nodoRostro = new AugmentedFaceNode(rostroRA);
                    nodoRostro.setFaceMeshTexture(texturaRostro);
                    vistaEscenaRa.getScene().addChild(nodoRostro);
                    nodosRostros.put(rostroRA, nodoRostro);
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


}