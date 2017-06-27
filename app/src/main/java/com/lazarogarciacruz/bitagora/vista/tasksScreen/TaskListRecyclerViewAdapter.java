package com.lazarogarciacruz.bitagora.vista.tasksScreen;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.modelo.Task;
import com.lazarogarciacruz.bitagora.utilidades.AnimatorManager;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;
import com.lazarogarciacruz.bitagora.utilidades.TypewriterText;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.TaskListAdapterViewHolder> {

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
    public class TaskListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private int[] coloresPantalla = {Color.argb(255, 0, 128, 64), Color.argb(255, 0, 128, 0), Color.argb(255, 64, 128, 0)};

        public View contenidoMonitor;
        public ImageView scanlines;
        public LinearLayout scanlinesLayout;

        public TypewriterText textoTitulo;
        public TypewriterText textoFecha;
        public GifImageView iconoDificultad;
        public GifImageView iconoPrioridad;

        public LinearLayout gridTextoBorrar;
        public TextView textoBorrar;
        public FrameLayout gridContenidoBorrar;

        String titulo;
        String fecha;
        int dificultad;
        int prioridad;
        public int position = -1;

        TaskListAdapterViewHolder(final View itemView) {

            super(itemView);

            contenidoMonitor = itemView.findViewById(R.id.task_list_contenido_monitor);
            contenidoMonitor.setBackgroundColor(coloresPantalla[ThreadLocalRandom.current().nextInt(0, 2 + 1)]);
            scanlines = (ImageView) itemView.findViewById(R.id.task_list_scanlines);
            scanlinesLayout = (LinearLayout) itemView.findViewById(R.id.task_list_scanlines_layout);

            textoTitulo = (TypewriterText) itemView.findViewById(R.id.task_list_texto_titulo);
            textoTitulo.setTextSize(20);
            textoTitulo.setTextColor(Color.GREEN);
            textoTitulo.setShadowLayer(10, 0, 0, Color.GREEN);
            textoTitulo.setTypeface(FontManager.getInstance().getFont("fonts/Bitwise.ttf"));
            textoTitulo.setTypingSpeed(50);

            textoFecha = (TypewriterText) itemView.findViewById(R.id.task_list_texto_fecha);
            textoFecha.setTextSize(15);
            textoFecha.setTextColor(Color.GREEN);
            textoFecha.setShadowLayer(10, 0, 0, Color.GREEN);
            textoFecha.setTypeface(FontManager.getInstance().getFont("fonts/Bitwise.ttf"));
            textoFecha.setTypingSpeed(50);

            iconoDificultad = (GifImageView) itemView.findViewById(R.id.task_list_dificultad);
            iconoPrioridad = (GifImageView) itemView.findViewById(R.id.task_list_prioridad);

            //Glide.with(MyApp.getContext()).load(R.drawable.scanlines).fitCenter().crossFade().into(scanlines);
            generarScanlines();

            gridContenidoBorrar = (FrameLayout) itemView.findViewById(R.id.task_list_contenido_borrar);
            gridContenidoBorrar.setVisibility(View.INVISIBLE);

            textoBorrar = (TextView) itemView.findViewById(R.id.task_list_texto_borrar);
            textoBorrar.setTypeface(FontManager.getInstance().getFont("fonts/Bitwise.ttf"));
            textoBorrar.setTextSize(MyApp.getInstance().isSmallScreen() ? 20 : 25);
            textoBorrar.setTextColor(Color.GREEN);
            textoBorrar.setShadowLayer(10, 0, 0, Color.GREEN);

            Button gridBotonBorrar = (Button) itemView.findViewById(R.id.task_list_boton_borrar);
            gridBotonBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteItemListener != null) mDeleteItemListener.onDeleteItem(position);
                }
            });

            Button gridBotonAtras = (Button) itemView.findViewById(R.id.task_list_boton_atras);
            gridBotonAtras.setTypeface(FontManager.getInstance().getFont("fonts/Bitwise.ttf"));
            gridBotonAtras.setTextColor(Color.GREEN);
            gridBotonAtras.setShadowLayer(10, 0, 0, Color.GREEN);
            gridBotonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorManager.getInstance().animarOcultarOpcionBorrarListaTask(itemView, (FrameLayout) contenidoMonitor, gridContenidoBorrar, textoBorrar);
                }
            });

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        private void generarScanlines() {

            int totalImagenes = 15;
            final int heigth = itemView.getHeight() / totalImagenes;
            scanlinesLayout.removeAllViews();
            for (int i = 0; i < totalImagenes; i++) {
                ImageView imageView = new ImageView(context);
                Glide.with(MyApp.getContext()).load(R.drawable.scanlines).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
                imageView.setRotationX(180.0f);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setAlpha(0.5f);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heigth, 1f);
                layoutParams.setMargins(0,0,0,2);
                imageView.setLayoutParams(layoutParams);
                scanlinesLayout.addView(imageView);
            }

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, position);
        }

        @Override
        public boolean onLongClick(View view) {
            AnimatorManager.getInstance().animarMostrarOpcionBorrarListaTask(view, (FrameLayout) contenidoMonitor, gridContenidoBorrar, textoBorrar);
            return true;
        }

    }

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------

    private LinkedList<Task> mData = new LinkedList<>();
    private Context context;

    private int cellHeight;
    private ItemClickListener mClickListener;
    private DeleteItemListener mDeleteItemListener;

    private int lastPosition = -1;
    private boolean isAnimation = false;

    /**
     * Constructor del adapter
     * */
    public TaskListRecyclerViewAdapter(Context context, LinkedList<Task> data, int cellHeight) {

        this.mData.addAll(data);
        this.context = context;
        this.cellHeight = cellHeight;
        this.isAnimation = true;

    }

    public TaskListRecyclerViewAdapter(Context context, int cellHeight) {

        this.context = context;
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
    public TaskListAdapterViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.task_list_elemento, parent, false);
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = cellHeight * (MyApp.getInstance().isSmallScreen() ? 18 : 15);
        view.setLayoutParams(params);
        view.setVisibility(View.VISIBLE);

        return new TaskListAdapterViewHolder(view);

    }

    /**
     * En este metodo se asocia (bind) los datos a cada una de las
     * celdas en funcion de la posicion que se le pasa
     * */
    @Override
    public void onBindViewHolder(TaskListAdapterViewHolder holder, int position) {

        Task task = mData.get(position);
        holder.position = position;
        holder.titulo = task.getTitulo();
        holder.fecha = task.getFechaCreacion();
        holder.dificultad = task.getDificultad();
        holder.prioridad = task.getPrioridad();

        //En la primera condicion se animan las celdas y en la
        //segunda unicamente se hacen visibles

        if (position > lastPosition && isAnimation) {
            AnimatorManager.getInstance().animacionEntradaCeldasTaskList(holder.itemView, holder.contenidoMonitor,
                    holder.textoTitulo, holder.titulo, holder.textoFecha, holder.fecha,
                    holder.iconoDificultad, holder.dificultad, holder.iconoPrioridad, holder.prioridad, position);
            lastPosition = position;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

    }

    public void insertarElemento(Task task) {

        mData.add(task);
        notifyItemInserted(mData.size());

    }

    public void eliminarComponente(int posicion) {

        mData.remove(posicion);
        notifyDataSetChanged();

    }

    public void updateDataSet(LinkedList<Task> dataSet) {

        mData.clear();
        notifyDataSetChanged();
        mData.addAll(dataSet);
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
