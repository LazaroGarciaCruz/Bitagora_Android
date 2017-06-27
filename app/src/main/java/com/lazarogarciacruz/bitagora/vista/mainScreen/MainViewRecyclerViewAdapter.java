package com.lazarogarciacruz.bitagora.vista.mainScreen;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.modelo.Juego;
import com.lazarogarciacruz.bitagora.utilidades.AnimatorManager;
import com.lazarogarciacruz.bitagora.utilidades.DataMaganer;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;

import java.io.File;
import java.util.LinkedList;

import pl.droidsonroids.gif.GifImageView;

public class MainViewRecyclerViewAdapter extends RecyclerView.Adapter<MainViewRecyclerViewAdapter.MainViewRecyclerGridAdapterViewHolder> {

    /**
     * Interfaz para controlar la pulsacion sobre un elemento
     * */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * Interfaz para controlar el borrar de elementos del adapter
     * */
    public interface  DeleteItemListener {
        void onDeleteItem(int position);
    }

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------

    /**
     * Guarda y recicla las vistas conforme el scroll las saca de la pantalla
     * */
    public class MainViewRecyclerGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public GifImageView gridCover;
        public ImageView gridLogo;
        public TextView gridTexto;

        public LinearLayout gridTextoBorrar;
        public FrameLayout gridContenidoGeneral;
        public FrameLayout gridContenidoBorrar;

        MainViewRecyclerGridAdapterViewHolder(final View itemView) {

            super(itemView);

            gridContenidoGeneral = (FrameLayout) itemView.findViewById(R.id.main_grid_contenido_general);
            gridContenidoGeneral.setVisibility(View.VISIBLE);
            gridContenidoBorrar = (FrameLayout) itemView.findViewById(R.id.main_grid_contenido_borrar);
            gridContenidoBorrar.setVisibility(View.INVISIBLE);

            gridCover = (GifImageView) itemView.findViewById(R.id.main_grid_cover);
            gridLogo = (ImageView) itemView.findViewById(R.id.main_grid_logo);
            gridTexto = (TextView) itemView.findViewById(R.id.main_grid_texto);
            gridTexto.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));
            gridTextoBorrar = (LinearLayout) itemView.findViewById(R.id.main_grid_texto_borrar);

            Button gridBotonBorrar = (Button) itemView.findViewById(R.id.main_grid_boton_borrar);
            gridBotonBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    gridContenidoGeneral.setVisibility(View.VISIBLE);
                    gridContenidoBorrar.setVisibility(View.INVISIBLE);
                    itemView.setRotationY(0);
                    gridTextoBorrar.setRotationY(-180f);
                    gridTextoBorrar.removeAllViews();

                    if (mDeleteItemListener != null) mDeleteItemListener.onDeleteItem(getAdapterPosition());

                }
            });

            Button gridBotonAtras = (Button) itemView.findViewById(R.id.main_grid_boton_atras);
            gridBotonAtras.setTypeface(FontManager.getInstance().getFont("fonts/Pixellari.ttf"));
            gridBotonAtras.setShadowLayer(1, 5, 5, Color.BLACK);
            gridBotonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorManager.getInstance().animarOcultarOpcionBorrar(itemView, gridContenidoGeneral, gridContenidoBorrar, gridTextoBorrar);
                }
            });

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            AnimatorManager.getInstance().animarMostrarOpcionBorrar(view, gridContenidoGeneral, gridContenidoBorrar, gridTextoBorrar);
            return true;
        }

    }

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------

    public static final int TIPO_GRID = 0;
    public static final int TIPO_TABLE = 1;

    private LinkedList<Juego> mData = new LinkedList<>();

    private int tipoRecyclerView;
    private int cellHeight;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private DeleteItemListener mDeleteItemListener;

    private int lastPosition = -1;
    private boolean isAnimation = false;

    /**
     * Constructor del adapter
     * */
    public MainViewRecyclerViewAdapter(Context context, LinkedList<Juego> data, int tipoRecyclerView, int cellHeight) {

        this.mInflater = LayoutInflater.from(context);
        this.mData.addAll(data);
        this.tipoRecyclerView = tipoRecyclerView;
        this.cellHeight = cellHeight;
        this.isAnimation = true;

    }

    /**
     * En este metodo se establece el numero total de celdas
     * */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * En este metodo se le asigna a la celda (inflate) el layout
     * desde el xml que se necesita en cada momento
     * */
    @Override
    public MainViewRecyclerGridAdapterViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = null;

        if (tipoRecyclerView == TIPO_GRID) {
            view = mInflater.inflate(R.layout.main_grid_elemento, parent, false);
        } else {
            view = mInflater.inflate(R.layout.main_table_elemento, parent, false);
        }

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = cellHeight;
        view.setLayoutParams(params);
        view.setVisibility(View.INVISIBLE);

        return new MainViewRecyclerGridAdapterViewHolder(view);

    }

    /**
     * En este metodo se asocia (bind) los datos a cada una de las
     * celdas en funcion de la posicion que se le pasa
     * */
    @Override
    public void onBindViewHolder(MainViewRecyclerGridAdapterViewHolder holder, int position) {

        Juego juego = mData.get(position);
        //holder.gridCover.setImageBitmap(juego.getCoverImage());
        //holder.gridLogo.setImageBitmap(juego.getLogoImage());
        holder.gridTexto.setText(juego.getTitulo());
        holder.itemView.setTag(position);

        File imagen = DataMaganer.getInstance().getStorage().getFile(juego.getId(), juego.getCover());
        if (imagen.getAbsolutePath().endsWith(".gif")) {
            Glide.with(MyApp.getContext()).load(imagen.getAbsolutePath()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(holder.gridCover);
        } else {
            Glide.with(MyApp.getContext()).load(imagen.getAbsolutePath()).crossFade().into(holder.gridCover);
        }

        imagen = DataMaganer.getInstance().getStorage().getFile(juego.getId(), juego.getLogo());
        Glide.with(MyApp.getContext()).load(imagen.getAbsolutePath()).override(600, 200).crossFade().into(holder.gridLogo);

        holder.gridTexto.setVisibility(View.VISIBLE);
        if (!juego.getLogo().equals("")) {
            holder.gridTexto.setVisibility(View.INVISIBLE);
        } else if (juego.getCover().equals("")) {
            holder.gridTexto.setTextColor(Color.BLACK);
            holder.gridTexto.setShadowLayer(1, 5, 5, Color.WHITE);
        } else {
            holder.gridTexto.setTextColor(Color.WHITE);
            holder.gridTexto.setShadowLayer(1, 5, 5, Color.BLACK);
        }

        //En la primera condicion se animan las celdas y en la
        //segunda unicamente se hacen visibles

        if (position > lastPosition && isAnimation) {
            AnimatorManager.getInstance().animacionEntradaCeldasMainView(holder.itemView, position, tipoRecyclerView);
            lastPosition = position;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Este metodo se llama cada vez que se saca un elemento del adapter
     * */
    @Override
    public void onViewDetachedFromWindow(MainViewRecyclerGridAdapterViewHolder holder) {

        if (isAnimation) {
            super.onViewDetachedFromWindow(holder);
            AnimatorManager.getInstance().animacionSalidaCeldasMainView(holder.itemView, (int) holder.itemView.getTag(), tipoRecyclerView);
        }

    }

    /**
     * Con este metodo se actualiza los datos de el adapter
     * */
    /*public void acutalizarInforamcion() {

        if (mData != null) {
            mData.clear();
            mData.addAll(DataMaganer.getInstance().getDataset().getJuegos());
        } else {
            mData = DataMaganer.getInstance().getDataset().getJuegos();
        }

        notifyDataSetChanged();

    }*/

    public void insertarElemento(Juego juego) {

        mData.add(juego);
        notifyItemInserted(mData.size());

    }

    public void eliminarComponente(int posicion) {

        mData.remove(posicion);
        notifyDataSetChanged();

    }

    /**
     * Con esta variable controlamos que la animacion solo afecte a las celdas
     * visibles en el momento en el que se visualiza el recycler view
     * */
    public void switchAnimation(boolean isAnimation) {
        this.isAnimation = isAnimation;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Estable al delegado del evento de click
     * */
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * Estable al delegado del evento de borrar item
     * */
    public void setmDeleteItemListener(DeleteItemListener deleteItemListener) {
        this.mDeleteItemListener = deleteItemListener;
    }

}
