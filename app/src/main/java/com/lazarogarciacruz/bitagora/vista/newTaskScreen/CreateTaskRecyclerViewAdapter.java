package com.lazarogarciacruz.bitagora.vista.newTaskScreen;

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
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteImagen;
import com.lazarogarciacruz.bitagora.modelo.TaskComponente;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteContador;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteEnlace;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTexto;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTipo;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;
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

public class CreateTaskRecyclerViewAdapter extends RecyclerView.Adapter {

    /**
     * Interfaz para controlar el borrar de elementos del adapter
     * */
    public interface  RecyclerViewAdapterListener {
        void onOcultarTeclado(boolean isOcultar);
        void onActualizarComponenteTexto(String texto, int posicion);
        void onMostrarSelectorImagenes(int posicion);
        void onActualizarComponenteEnlace(String enlace, boolean isVideo, int posicion);
        void onActualizarComponeteContador(String descripcion, int contador, int posicion);
        void onMostrarCamara(int posicion);
        void onDeleteItem(int position);
    }

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------


    public class TipoTextoViewHolder extends RecyclerView.ViewHolder {

        private TextView textoTitulo;
        private Button botonCerrar;
        private EditText textoCuerpo;

        public int position = -1;

        public TipoTextoViewHolder(View itemView) {

            super(itemView);

            textoTitulo = (TextView) itemView.findViewById(R.id.taskComponenteTitulo);
            textoTitulo.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

            botonCerrar = (Button) itemView.findViewById(R.id.taskComponenteBotonCerrar);
            botonCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onDeleteItem(position);
                }
            });

            textoCuerpo = (EditText) itemView.findViewById(R.id.taskComponenteTextoCuerpo);
            textoCuerpo.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

            //Con las siguientes gestiones de eventos conseguimos lo siguiente
            //1. Con el setOnTouchListener, si es ACTION_DOWN desactiva el scroll de recycler
            //   para poder hacer el scroll en el contenido interno, eso si, esto desactiva
            //   las opciones de copiar y pegar texto. Posteriormente con el ACTION_UP volvemos
            //   a activar que en el recycler se pueda hacer scroll
            //2. Con el setOnLongClickListener hacemos que momentaneamete se permita hacer scroll
            //   otra vez en el recycler y con ello activando las opciones de copiar y pegar

            textoCuerpo.setOnTouchListener(new EditText.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            if (v.isFocused()) {
                                // Disallow ScrollView to intercept touch events.
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });

            textoCuerpo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            });

            itemView.findViewById(R.id.createTaskComponenteRoot).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onOcultarTeclado(true);
                    return false;
                }
            });

            //Con este codigo controlamos cuando aparece y desaparece el
            //teclado de pantalla y podemos actuar en consecuencia

            final View activityRootView = ((CreateTaskActivity)context).findViewById(R.id.createTaskViewFrameRoot);
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                    float dtToPx = dpToPx(MyApp.getContext(), 200);
                    if (heightDiff < dtToPx) { // if more than 200 dp, it's probably a keyboard...
                        //El teclado se ha escondido
                        textoCuerpo.clearFocus();
                        if (mRecyclerViewAdapterListener != null && position != -1 && position < dataSet.size() && dataSet.size() > 0 && dataSet.get(position) instanceof TaskComponenteTexto) {
                            ((TaskComponenteTexto)dataSet.get(position)).setTexto(textoCuerpo.getText().toString());
                            mRecyclerViewAdapterListener.onActualizarComponenteTexto(textoCuerpo.getText().toString(), position);
                            if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onOcultarTeclado(true);
                        }
                    } else {
                        if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onOcultarTeclado(false);
                    }
                }
            });

        }

    }

    public class TipoImagenViewHolder extends RecyclerView.ViewHolder {

        private TextView textoTitulo;
        private Button botonCerrar;
        private SliderLayout sliderLayout;
        private Button botonCamara;
        private Button botonDocumentos;

        public int position = -1;

        public TipoImagenViewHolder(View itemView) {

            super(itemView);

            textoTitulo = (TextView) itemView.findViewById(R.id.taskComponenteTitulo);
            textoTitulo.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

            botonCerrar = (Button) itemView.findViewById(R.id.taskComponenteBotonCerrar);
            botonCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onDeleteItem(position);
                }
            });

            sliderLayout = (SliderLayout) itemView.findViewById(R.id.taskComponenteImagenesSlider);

            botonCamara = (Button) itemView.findViewById(R.id.taskComponenteImagenesBotonCamara);
            botonCamara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onMostrarCamara(position);
                }
            });

            botonDocumentos = (Button) itemView.findViewById(R.id.taskComponenteImagenesBotonDocumentos);
            botonDocumentos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onMostrarSelectorImagenes(position);
                }
            });

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
                        sliderView.image(new File(ruta));
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

        private TextView textoTitulo;
        private Button botonCerrar;

        private FrameLayout seccionVideoPlayer;
        private RelativeLayout seccionEnlaceFinal;
        private LinearLayout seccionValidarEnlace;

        private YouTubePlayerView videoPlayerView;
        private TextView textoEnlaceFinal;
        private EditText textoEnlaceValidar;
        private Button botonValidar;

        public int position = -1;

        public TipoEnlaceViewHolder(View itemView) {

            super(itemView);

            textoTitulo = (TextView) itemView.findViewById(R.id.taskComponenteTitulo);
            textoTitulo.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

            botonCerrar = (Button) itemView.findViewById(R.id.taskComponenteBotonCerrar);
            botonCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onDeleteItem(position);
                }
            });

            seccionVideoPlayer = (FrameLayout) itemView.findViewById(R.id.taskComponenteEnlaceSeccionVideoPlayer);
            seccionEnlaceFinal = (RelativeLayout) itemView.findViewById(R.id.taskComponenteEnlaceSeccionEnlaceFinal);
            seccionValidarEnlace = (LinearLayout) itemView.findViewById(R.id.taskComponenteEnlaceSeccionValidarEnlace);

            videoPlayerView = (YouTubePlayerView) itemView.findViewById(R.id.taskComponenteEnlaceVideoPlayer);
            videoPlayerView.showFullScreenButton(false);
            videoPlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
                @Override
                public void onYouTubePlayerEnterFullScreen() {
                    ((CreateTaskActivity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                @Override
                public void onYouTubePlayerExitFullScreen() {
                    ((CreateTaskActivity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            });

            textoEnlaceFinal = (TextView) itemView.findViewById(R.id.taskComponenteEnlaceEnlaceFinal);
            textoEnlaceFinal.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));
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

            textoEnlaceValidar = (EditText) itemView.findViewById(R.id.taskComponenteEnlaceTextoEnlace);
            textoEnlaceValidar.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));
            textoEnlaceValidar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onOcultarTeclado(true);
                        return true;
                    }
                    return false;
                }
            });

            botonValidar = (Button) itemView.findViewById(R.id.taskComponenteEnlaceBotonValidar);
            botonValidar.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));
            botonValidar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onOcultarTeclado(true);
                    validarEnlace();
                }
            });

            //Con este codigo controlamos cuando aparece y desaparece el
            //teclado de pantalla y podemos actuar en consecuencia

            final View activityRootView = ((CreateTaskActivity)context).findViewById(R.id.createTaskViewFrameRoot);
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (activityRootView.getRootView().getHeight() - activityRootView.getHeight() < dpToPx(MyApp.getContext(), 200)) {
                        textoEnlaceValidar.clearFocus();
                        if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onOcultarTeclado(true);
                    } else {
                        if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onOcultarTeclado(false);
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
                    seccionValidarEnlace.setVisibility(View.GONE);
                } else if (!taskComponenteEnlace.getEnlace().equals("")) {
                    params.height = cellHeight * 15;
                    seccionVideoPlayer.setVisibility(View.GONE);
                    seccionEnlaceFinal.setVisibility(View.VISIBLE);
                    seccionValidarEnlace.setVisibility(View.GONE);
                } else {
                    params.height = cellHeight * 15;
                    seccionVideoPlayer.setVisibility(View.GONE);
                    seccionEnlaceFinal.setVisibility(View.GONE);
                    seccionValidarEnlace.setVisibility(View.VISIBLE);
                }

                itemView.setLayoutParams(params);

            }

        }

        private void validarEnlace() {

            //Pasamos a verificar la velidad del enlace proporcionado

            String url = textoEnlaceValidar.getText().toString().replace(" ", "");
            if (!url.contains("http") || (!url.contains("https"))) {
                url = "http://" + url;
            }

            if (Patterns.WEB_URL.matcher(url).matches()) {

                if (url.contains("youtu")) {

                    //En este caso la url es un video del youtube y tenemos que extraer la id

                    String youtubeId = "";
                    final String regexLong = "v=([^\\s&#]*)";
                    final String regexShort = "be/([^\\s&#]*)";
                    boolean urlValida = false;

                    Pattern pattern = Pattern.compile(regexLong, Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(url);
                    if (matcher.find()) {
                        youtubeId = matcher.group(1);
                        urlValida = true;
                    } else {
                        pattern = Pattern.compile(regexShort, Pattern.MULTILINE);
                        matcher = pattern.matcher(url);
                        if (matcher.find()) {
                            youtubeId = matcher.group(1);
                            urlValida = true;
                        }
                    }

                    if (urlValida) {

                        ((TaskComponenteEnlace)dataSet.get(position)).setEnlace(url);
                        ((TaskComponenteEnlace)dataSet.get(position)).setEnlaceVideo(true);
                        if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onActualizarComponenteEnlace(url, true, position);
                        actualizarRepresentacion();
                        final String finalYoutubeId = youtubeId;
                        videoPlayerView.initialize(new AbstractYouTubeListener() {
                            @Override
                            public void onReady() {
                                videoPlayerView.loadVideo(finalYoutubeId, 0);
                            }
                        }, true);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Enlace de YouTube incorrecto");
                        builder.setMessage("Parece que el formato del enlace de YouTube introducido no es correcto");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                    }

                } else {

                    ((TaskComponenteEnlace)dataSet.get(position)).setEnlace(url);
                    ((TaskComponenteEnlace)dataSet.get(position)).setEnlaceVideo(false);
                    textoEnlaceFinal.setText(url);
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onActualizarComponenteEnlace(url, false, position);
                    actualizarRepresentacion();

                }

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Enlace incorrecto");
                builder.setMessage("Parece que el formato del enlace introducido no es correcto");
                builder.setPositiveButton("OK", null);
                builder.show();

            }

        }

    }

    public class TipoContadorViewHolder extends RecyclerView.ViewHolder {

        private TextView textoTitulo;
        private Button botonCerrar;

        private EditText textoDescripcion;
        private TextView textoContador;
        private Button botonMas;
        private Button botonMenos;

        public int position = -1;

        public TipoContadorViewHolder(View itemView) {

            super(itemView);

            textoTitulo = (TextView) itemView.findViewById(R.id.taskComponenteTitulo);
            textoTitulo.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

            botonCerrar = (Button) itemView.findViewById(R.id.taskComponenteBotonCerrar);
            botonCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onDeleteItem(position);
                }
            });

            textoDescripcion = (EditText) itemView.findViewById(R.id.taskComponenteContadorTextoDescripcion);
            textoDescripcion.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

            textoContador = (TextView) itemView.findViewById(R.id.taskComponenteContadorTextoContador);
            textoContador.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

            botonMas = (Button) itemView.findViewById(R.id.taskComponenteContadorBotonMas);
            botonMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskComponenteContador componenteContador = ((TaskComponenteContador)dataSet.get(position));
                    componenteContador.setCantidadElementosMaxima(componenteContador.getCantidadElementosMaxima() + 1);
                    textoContador.setText(Integer.toString(componenteContador.getCantidadElementosMaxima()));
                    if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onActualizarComponeteContador(
                            textoDescripcion.getText().toString(), Integer.parseInt(textoContador.getText().toString()), position);
                }
            });

            botonMenos = (Button) itemView.findViewById(R.id.taskComponenteContadorBotonMenos);
            botonMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskComponenteContador componenteContador = ((TaskComponenteContador)dataSet.get(position));
                    if (componenteContador.getCantidadElementosMaxima() - 1 >= 0) {
                        componenteContador.setCantidadElementosMaxima(componenteContador.getCantidadElementosMaxima() - 1);
                        textoContador.setText(Integer.toString(componenteContador.getCantidadElementosMaxima()));
                        if (mRecyclerViewAdapterListener != null)
                            mRecyclerViewAdapterListener.onActualizarComponeteContador(
                                    textoDescripcion.getText().toString(), Integer.parseInt(textoContador.getText().toString()), position);
                    }
                }
            });

            //Con este codigo controlamos cuando aparece y desaparece el
            //teclado de pantalla y podemos actuar en consecuencia

            final View activityRootView = ((CreateTaskActivity)context).findViewById(R.id.createTaskViewFrameRoot);
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (activityRootView.getRootView().getHeight() - activityRootView.getHeight() < dpToPx(MyApp.getContext(), 200)) {
                        textoDescripcion.clearFocus();
                        if (dataSet.size() > 0 && position != -1 && position < dataSet.size() && dataSet.get(position) instanceof TaskComponenteContador) {
                            ((TaskComponenteContador) dataSet.get(position)).setDescripcion(textoDescripcion.getText().toString());
                            if (mRecyclerViewAdapterListener != null)
                                mRecyclerViewAdapterListener.onOcultarTeclado(true);
                            if (mRecyclerViewAdapterListener != null)
                                mRecyclerViewAdapterListener.onActualizarComponeteContador(
                                        textoDescripcion.getText().toString(), Integer.parseInt(textoContador.getText().toString()), position);
                        }
                    } else {
                        if (mRecyclerViewAdapterListener != null) mRecyclerViewAdapterListener.onOcultarTeclado(false);
                    }
                }
            });

        }

    }

    private float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------

    private LinkedList<TaskComponente> dataSet = new LinkedList<>();

    private int cellWidth;
    private int cellHeight;

    private Context context;
    private LayoutInflater mInflater;
    private RecyclerViewAdapterListener mRecyclerViewAdapterListener;

    /**
     * Constructor del adapter
     * */
    public CreateTaskRecyclerViewAdapter(Context context, LinkedList<TaskComponente> data, int cellWidth, int cellHeight) {

        this.context = context;
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
                view = LayoutInflater.from(context).inflate(R.layout.create_task_texto_element, parent, false);
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                params.height = cellHeight * 30;
                view.setLayoutParams(params);
                return new TipoTextoViewHolder(view);
            case TaskComponenteTipo.IMAGEN:
                view = LayoutInflater.from(context).inflate(R.layout.create_task_imagen_element, parent, false);
                params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                params.height = cellHeight * 40;
                view.setLayoutParams(params);
                return new TipoImagenViewHolder(view);
            case TaskComponenteTipo.ENLACE:
                view = LayoutInflater.from(context).inflate(R.layout.create_task_enlace_element, parent, false);
                return new TipoEnlaceViewHolder(view);
            case TaskComponenteTipo.CONTADOR:
                view = LayoutInflater.from(context).inflate(R.layout.create_task_contador_element, parent, false);
                params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                params.height = cellHeight * 15;
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
                    ((TipoEnlaceViewHolder)holder).textoEnlaceValidar.setText(((TaskComponenteEnlace)object).getEnlace());
                    ((TipoEnlaceViewHolder)holder).textoEnlaceFinal.setText(((TaskComponenteEnlace)object).getEnlace());
                    ((TipoEnlaceViewHolder)holder).actualizarRepresentacion();
                    break;

                case TaskComponenteTipo.CONTADOR:
                    ((TipoContadorViewHolder)holder).position = listPosition;
                    ((TipoContadorViewHolder)holder).textoDescripcion.setText(((TaskComponenteContador)object).getDescripcion());
                    ((TipoContadorViewHolder)holder).textoContador.setText(Integer.toString(((TaskComponenteContador)object).getCantidadElementosMaxima()));
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
