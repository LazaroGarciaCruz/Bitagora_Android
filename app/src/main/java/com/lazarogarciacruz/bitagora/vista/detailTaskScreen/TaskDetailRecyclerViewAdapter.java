package com.lazarogarciacruz.bitagora.vista.detailTaskScreen;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.modelo.Task;
import com.lazarogarciacruz.bitagora.modelo.TaskComponente;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteContador;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteEnlace;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteImagen;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTexto;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTipo;
import com.lazarogarciacruz.bitagora.utilidades.DataMaganer;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;
import com.lazarogarciacruz.bitagora.vista.newTaskScreen.CreateTaskActivity;
import com.pierfrancescosoffritti.youtubeplayer.AbstractYouTubeListener;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerView;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

public class TaskDetailRecyclerViewAdapter extends RecyclerView.Adapter {

    /**
     * Interfaz para controlar las distintas acciones del adapter
     * */
    public interface  RecyclerViewAdapterListener {
        void onActualizarComponeteContador(String idContador, int contador, int posicion);
    }

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------


    public class TipoTextoViewHolder extends RecyclerView.ViewHolder {

        private TextView textoCuerpo;

        public int position = -1;

        public TipoTextoViewHolder(View itemView) {

            super(itemView);

            textoCuerpo = (TextView) itemView.findViewById(R.id.detailTaskComponenteTextoCuerpo);
            textoCuerpo.setTypeface(FontManager.getInstance().getFont("fonts/EarlyGameBoy.ttf"));

        }

    }

    public class TipoImagenViewHolder extends RecyclerView.ViewHolder {

        private SliderLayout sliderLayout;

        public int position = -1;

        public TipoImagenViewHolder(View itemView) {

            super(itemView);

            sliderLayout = (SliderLayout) itemView.findViewById(R.id.detailTaskComponenteImagenesSlider);

            actualizarImagenes();

        }

        private void actualizarImagenes() {

            //Cargamos la imagenes que ya tenga seleccionadas el componente

            if (position >= 0) {

                TaskComponenteImagen taskComponenteImagen = (TaskComponenteImagen) dataSet.get(position);

                if (taskComponenteImagen.getListaNombresImagenes().size() == 0) {
                    sliderLayout.removeAllSliders();
                    DefaultSliderView sliderView = new DefaultSliderView(context);
                    sliderView.setCenterCrop(true);
                    sliderLayout.addSlider(sliderView);
                } else {

                    sliderLayout.removeAllSliders();
                    for (String ruta : taskComponenteImagen.getListaNombresImagenes()) {
                        DefaultSliderView sliderView = new DefaultSliderView(context);
                        sliderView.image(DataMaganer.getInstance().getStorage().getFile(task.getIdJuego(), ruta));
                        sliderView.setCenterCrop(true);
                        sliderLayout.addSlider(sliderView);
                    }

                    if (taskComponenteImagen.getListaNombresImagenes().size() < 2) {
                        sliderLayout.stopAutoCycle();
                    } else {
                        sliderLayout.startAutoCycle(1000, 3000, true);
                    }

                }

            }

        }

    }

    public class TipoEnlaceViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout seccionVideoPlayer;
        private RelativeLayout seccionEnlaceFinal;

        private YouTubePlayerView videoPlayerView;
        private TextView textoEnlaceFinal;

        public int position = -1;

        public TipoEnlaceViewHolder(View itemView) {

            super(itemView);

            seccionVideoPlayer = (FrameLayout) itemView.findViewById(R.id.detailTaskComponenteEnlaceSeccionVideoPlayer);
            seccionEnlaceFinal = (RelativeLayout) itemView.findViewById(R.id.detailTaskComponenteEnlaceSeccionEnlaceFinal);

            videoPlayerView = (YouTubePlayerView) itemView.findViewById(R.id.detailTaskComponenteEnlaceVideoPlayer);
            videoPlayerView.showFullScreenButton(false);

            textoEnlaceFinal = (TextView) itemView.findViewById(R.id.detailTaskComponenteEnlaceEnlaceFinal);
            textoEnlaceFinal.setTypeface(FontManager.getInstance().getFont("fonts/EarlyGameBoy.ttf"));
            textoEnlaceFinal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Uri webpage = Uri.parse(textoEnlaceFinal.getText().toString());
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
                        context.startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "Ninguna aplicaicion puede abrir esta pagina. Por favor, instala un navegador o comprueba en enlace.",  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

        }

        private void actualizarRepresentacion() {

            if (position >= 0) {

                TaskComponenteEnlace taskComponenteEnlace = (TaskComponenteEnlace) dataSet.get(position);
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();

                if (taskComponenteEnlace.getEnlaceVideo()) {

                    params.height = cellHeight * 40;
                    seccionVideoPlayer.setVisibility(View.VISIBLE);
                    seccionEnlaceFinal.setVisibility(View.GONE);

                    String youtubeId = "";
                    final String regexLong = "v=([^\\s&#]*)";
                    final String regexShort = "be/([^\\s&#]*)";
                    boolean urlValida = false;

                    Pattern pattern = Pattern.compile(regexLong, Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(taskComponenteEnlace.getEnlace());
                    if (matcher.find()) {
                        youtubeId = matcher.group(1);
                        urlValida = true;
                    } else {
                        pattern = Pattern.compile(regexShort, Pattern.MULTILINE);
                        matcher = pattern.matcher(taskComponenteEnlace.getEnlace());
                        if (matcher.find()) {
                            youtubeId = matcher.group(1);
                            urlValida = true;
                        }
                    }

                    if (urlValida) {
                        final String finalYoutubeId = youtubeId;
                        videoPlayerView.initialize(new AbstractYouTubeListener() {
                            @Override
                            public void onReady() {
                                videoPlayerView.loadVideo(finalYoutubeId, 0);
                                //videoPlayerView.pauseVideo();
                            }

                            /*@Override
                            public void onStateChange(int state) {
                                super.onStateChange(state);
                                if (state == 3) {
                                    videoPlayerView.pauseVideo();
                                }
                            }*/
                        }, true);
                    }

                } else if (!taskComponenteEnlace.getEnlace().equals("")) {
                    params.height = cellHeight * 10;
                    seccionVideoPlayer.setVisibility(View.GONE);
                    seccionEnlaceFinal.setVisibility(View.VISIBLE);
                }

                itemView.setLayoutParams(params);

            }

        }

    }

    public class TipoContadorViewHolder extends RecyclerView.ViewHolder {

        private TextView textoDescripcion;
        private TextView textoContador;
        private Button botonMas;
        private Button botonMenos;

        public int position = -1;

        public TipoContadorViewHolder(View itemView) {

            super(itemView);

            textoDescripcion = (TextView) itemView.findViewById(R.id.detailTaskComponenteContadorTextoDescripcion);
            textoDescripcion.setTypeface(FontManager.getInstance().getFont("fonts/EarlyGameBoy.ttf"));

            textoContador = (TextView) itemView.findViewById(R.id.detailTaskComponenteContadorTextoContador);
            textoContador.setTypeface(FontManager.getInstance().getFont("fonts/EarlyGameBoy.ttf"));

            botonMas = (Button) itemView.findViewById(R.id.detailTaskComponenteContadorBotonMas);
            botonMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskComponenteContador componenteContador = ((TaskComponenteContador)dataSet.get(position));
                    if (componenteContador.getCantidadElementosActuales() + 1 <= componenteContador.getCantidadElementosMaxima()) {

                        for (int i = 0; i < DataMaganer.getInstance().getDataset().getTasks().size(); i++) {
                            if (DataMaganer.getInstance().getDataset().getTasks().get(i).getId().equals(task.getId())) {

                                ((TaskComponenteContador)DataMaganer.getInstance().getDataset().getTasks().get(i).getListaComponentes().get(position)).
                                        setCantidadElementosActuales(((TaskComponenteContador)DataMaganer.getInstance().getDataset().getTasks().get(i).getListaComponentes().get(position)).
                                                getCantidadElementosActuales() + 1);

                                componenteContador.setCantidadElementosActuales(((TaskComponenteContador)DataMaganer.getInstance().getDataset().getTasks().get(i).getListaComponentes().get(position)).
                                        getCantidadElementosActuales());
                                textoContador.setText(Integer.toString(componenteContador.getCantidadElementosActuales())
                                        + " / " + Integer.toString(componenteContador.getCantidadElementosMaxima()));
                                DataMaganer.getInstance().actualizarTasks();

                                task = DataMaganer.getInstance().getDataset().getTasks().get(i);

                                break;

                            }
                        }

                    }
                }
            });

            botonMenos = (Button) itemView.findViewById(R.id.detailTaskComponenteContadorBotonMenos);
            botonMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskComponenteContador componenteContador = ((TaskComponenteContador)dataSet.get(position));
                    if (componenteContador.getCantidadElementosActuales() - 1 >= 0) {

                        for (int i = 0; i < DataMaganer.getInstance().getDataset().getTasks().size(); i++) {
                            if (DataMaganer.getInstance().getDataset().getTasks().get(i).getId().equals(task.getId())) {

                                ((TaskComponenteContador)DataMaganer.getInstance().getDataset().getTasks().get(i).getListaComponentes().get(position)).
                                        setCantidadElementosActuales(((TaskComponenteContador)DataMaganer.getInstance().getDataset().getTasks().get(i).getListaComponentes().get(position)).
                                                getCantidadElementosActuales() - 1);

                                componenteContador.setCantidadElementosActuales(((TaskComponenteContador)DataMaganer.getInstance().getDataset().getTasks().get(i).getListaComponentes().get(position)).
                                        getCantidadElementosActuales());
                                textoContador.setText(Integer.toString(componenteContador.getCantidadElementosActuales())
                                        + " / " + Integer.toString(componenteContador.getCantidadElementosMaxima()));
                                DataMaganer.getInstance().actualizarTasks();

                                task = DataMaganer.getInstance().getDataset().getTasks().get(i);

                                break;

                            }
                        }

                    }
                }
            });

        }

    }

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------

    private LinkedList<TaskComponente> dataSet = new LinkedList<>();
    private Task task;
    private int cellWidth;
    private int cellHeight;

    private Context context;
    private RecyclerViewAdapterListener mRecyclerViewAdapterListener;

    /**
     * Constructor del adapter
     * */
    public TaskDetailRecyclerViewAdapter(Context context, Task task, LinkedList<TaskComponente> data, int cellWidth, int cellHeight) {

        this.context = context;
        this.task = task;
        this.dataSet.addAll(data);
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;

    }

    /**
     * En este metodo se establece el numero total de celdas
     * */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * En este metodo se le asigna a la celda (inflate) el layout
     * desde el xml que se necesita en cada momento
     * */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        //El valor de viewType se obtiene sobreescribiendo el metodo
        //getItemViewType como vemos debajo de este metodo

        switch (viewType) {
            case TaskComponenteTipo.TEXTO:
                view = LayoutInflater.from(context).inflate(R.layout.detail_task_texto_element, parent, false);
                return new TipoTextoViewHolder(view);
            case TaskComponenteTipo.IMAGEN:
                view = LayoutInflater.from(context).inflate(R.layout.detail_task_imagen_element, parent, false);
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                params.height = cellHeight * 40;
                view.setLayoutParams(params);
                return new TipoImagenViewHolder(view);
            case TaskComponenteTipo.ENLACE:
                view = LayoutInflater.from(context).inflate(R.layout.detail_task_enlace_element, parent, false);
                return new TipoEnlaceViewHolder(view);
            case TaskComponenteTipo.CONTADOR:
                view = LayoutInflater.from(context).inflate(R.layout.detail_task_contador_element, parent, false);
                params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                params.height = cellHeight * 5;
                view.setLayoutParams(params);
                return new TipoContadorViewHolder(view);
        }

        return null;

    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getTipoComponete().getTipoComponente();
    }

    /**
     * En este metodo se asocia (bind) los datos a cada una de las
     * celdas en funcion de la posicion que se le pasa
     * */

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        TaskComponente object = dataSet.get(listPosition);

        if (object != null) {

            switch (object.getTipoComponete().getTipoComponente()) {

                case TaskComponenteTipo.TEXTO:
                    ((TipoTextoViewHolder)holder).position = listPosition;
                    ((TipoTextoViewHolder)holder).textoCuerpo.setText(((TaskComponenteTexto)object).getTexto());
                    break;

                case TaskComponenteTipo.IMAGEN:
                    ((TipoImagenViewHolder)holder).position = listPosition;
                    ((TipoImagenViewHolder)holder).actualizarImagenes();
                    break;

                case TaskComponenteTipo.ENLACE:
                    ((TipoEnlaceViewHolder)holder).position = listPosition;
                    ((TipoEnlaceViewHolder)holder).textoEnlaceFinal.setText(((TaskComponenteEnlace)object).getEnlace());
                    ((TipoEnlaceViewHolder)holder).actualizarRepresentacion();
                    break;

                case TaskComponenteTipo.CONTADOR:
                    ((TipoContadorViewHolder)holder).position = listPosition;
                    ((TipoContadorViewHolder)holder).textoDescripcion.setText(((TaskComponenteContador)object).getDescripcion());
                    ((TipoContadorViewHolder)holder).textoContador.setText(Integer.toString(((TaskComponenteContador)object).getCantidadElementosActuales())
                            + " / " + Integer.toString(((TaskComponenteContador)object).getCantidadElementosMaxima()));
                    break;

            }

        }

    }

    public void insertarElemento(TaskComponente componente) {

        dataSet.add(componente);
        notifyItemInserted(dataSet.size());

    }

    public void eliminarComponente(int posicion) {

        dataSet.remove(posicion);
        notifyDataSetChanged();

    }

    public void actualizarComponente(int posicion) {
        notifyItemChanged(posicion);
    }

    public void clearComponentes() {

        dataSet.clear();
        notifyDataSetChanged();

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Estable al delegado del evento de interactuar con el recycler view
     * */
    public void setmRecyclerViewAdapterListener(RecyclerViewAdapterListener recyclerViewAdapterListener) {
        this.mRecyclerViewAdapterListener = recyclerViewAdapterListener;
    }

}
