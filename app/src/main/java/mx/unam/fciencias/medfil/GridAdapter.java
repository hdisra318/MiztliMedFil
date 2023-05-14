package mx.unam.fciencias.medfil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Adaptador para el grid de la galeria de filtros
 */
public class GridAdapter extends BaseAdapter {

    private Context context;

    private int[] image;

    LayoutInflater inflater;

    /** Imagen del filtro */
    ImageView filtroImage;

    public GridAdapter(Context context, int[] image) {

        this.context = context;
        this.image = image;
        
    }

    @Override
    public int getCount() {
        return image.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null){

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(view == null) {

            view = inflater.inflate(R.layout.filtro_grid_item, null);
        }

        filtroImage = view.findViewById(R.id.filtro_image);

        filtroImage.setImageResource(image[i]);

        return view;
    }


}
