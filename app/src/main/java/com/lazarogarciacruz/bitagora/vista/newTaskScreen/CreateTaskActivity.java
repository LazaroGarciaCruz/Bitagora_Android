package com.lazarogarciacruz.bitagora.vista.newTaskScreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteImagen;
import com.lazarogarciacruz.bitagora.modelo.TaskComponente;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteContador;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteEnlace;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTexto;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTipo;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class CreateTaskActivity extends AppCompatActivity implements CreateTaskRecyclerViewAdapter.RecyclerViewAdapterListener {

    private int juegoSeleccionado = -1;
    private LinkedList<TaskComponente> listaComponentes = new LinkedList<>();
    private RecyclerView recyclerView;
    private ConstraintLayout botoneraView;
    private FrameLayout cabeceraView;
    private Guideline guidelineSuperior;
    private Guideline guidelineInferior;
    private LinearLayout scanlinesLayout;

    private int componenteSeleccionado = -1;

    private NuevoTaskFragment nuevoTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            juegoSeleccionado = bundle.getInt("JUEGO");
        }

        inicializarComponentesGenerales();
        inicializarRecyclerView();
        setStatusBarColor();

    }

    //-----------------------------------------------------------------
    //-----------------------------------------------------------------
    //---------- FUNCIONES SOBRE LA CREACION DE LA VENTANA ------------
    //-----------------------------------------------------------------
    //-----------------------------------------------------------------

    /**
     * En este metodo se llevan a cabo las tareas necesarias para inicializar
     * los componentes generales de la ventana
     * */
    private void inicializarComponentesGenerales() {

        recyclerView = (RecyclerView) findViewById(R.id.createTaskRecyclerView);
        botoneraView = (ConstraintLayout) findViewById(R.id.createTraskBotonera);
        cabeceraView = (FrameLayout) findViewById(R.id.createTaskViewCabecera);
        guidelineSuperior = (Guideline) findViewById(R.id.mainViewGuideline1);
        guidelineInferior = (Guideline) findViewById(R.id.mainViewGuideline2);

        //Con este codigo hacemos que cada vez que se pulse en la pantalla
        //se oculte el teclado si este se encuentra activo

        final View activityRootView = findViewById(R.id.createTaskViewFrameRoot);
        activityRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onOcultarTeclado(true);
                return false;
            }
        });

        //---------------------------------------------------
        //-------------------- SCANLINES --------------------
        //---------------------------------------------------

        scanlinesLayout = (LinearLayout) findViewById(R.id.createTaskViewScanlines);
        int totalImagenes = 16;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int heigth = dm.heightPixels / totalImagenes;

        for (int i = 0; i < totalImagenes; i++) {

            ImageView imageView = new ImageView(this);
            Glide.with(MyApp.getContext()).load(R.drawable.scanlines).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            imageView.setRotationX(180.0f);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAlpha(0.10f);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heigth, 1f);
            layoutParams.setMargins(0,0,0,0);
            imageView.setLayoutParams(layoutParams);
            scanlinesLayout.addView(imageView);

        }

        //---------------------------------------------------
        //--------------------- CABECERA --------------------
        //---------------------------------------------------

        //Con este codigo establecemos el color del status bar

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/

        try {

            GifImageView gifImageView = (GifImageView) findViewById(R.id.createTaskViewBackground);
            GifDrawable drawable = new GifDrawable(this.getAssets(), "gif/stars_purple_background.gif");
            gifImageView.setBackground(drawable);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button botonAtras = (Button) findViewById(R.id.createTaskBotonAtras);
        botonAtras.setTextSize(30);
        botonAtras.setShadowLayer(10, 1, 1, Color.BLACK);
        botonAtras.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOcultarTeclado(true);
                CreateTaskActivity.super.onBackPressed();
            }
        });

        Button botonLimpiar = (Button) findViewById(R.id.createTaskBotonLimpiar);
        botonLimpiar.setShadowLayer(10, 1, 1, Color.BLACK);
        botonLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOcultarTeclado(true);
                listaComponentes.clear();
                ((CreateTaskRecyclerViewAdapter)recyclerView.getAdapter()).clearComponentes();
            }
        });

        Button botonGuardar = (Button) findViewById(R.id.createTaskBotonGuardar);
        botonGuardar.setShadowLayer(10, 1, 1, Color.BLACK);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onOcultarTeclado(true);

                if (verificarGuardarTask()) {

                    nuevoTaskFragment = NuevoTaskFragment.newInstance();
                    nuevoTaskFragment.setDatosGenerales(listaComponentes, juegoSeleccionado);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.createTaskViewFrameRoot, nuevoTaskFragment, "nuevoTaskFragment")
                            .commit();

                    scanlinesLayout.bringToFront();

                }

            }
        });

        //---------------------------------------------------
        //-------------------- CONTROLES --------------------
        //---------------------------------------------------

        Button nuevoComponenteTexto = (Button) findViewById(R.id.createTaskBotonTexto);
        nuevoComponenteTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskComponenteTexto componenteTexto = new TaskComponenteTexto();
                listaComponentes.add(componenteTexto);
                ((CreateTaskRecyclerViewAdapter)recyclerView.getAdapter()).insertarElemento(componenteTexto);
                recyclerView.smoothScrollToPosition(listaComponentes.size()-1);
            }
        });

        Button nuevoComponenteImagen = (Button) findViewById(R.id.createTaskBotonImagen);
        nuevoComponenteImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskComponenteImagen componenteImagen = new TaskComponenteImagen();
                listaComponentes.add(componenteImagen);
                ((CreateTaskRecyclerViewAdapter)recyclerView.getAdapter()).insertarElemento(componenteImagen);
                recyclerView.smoothScrollToPosition(listaComponentes.size()-1);
            }
        });

        Button nuevoComponenteEnlace = (Button) findViewById(R.id.createTaskBotonEnlace);
        nuevoComponenteEnlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskComponenteEnlace componenteEnlace = new TaskComponenteEnlace();
                listaComponentes.add(componenteEnlace);
                ((CreateTaskRecyclerViewAdapter)recyclerView.getAdapter()).insertarElemento(componenteEnlace);
                recyclerView.smoothScrollToPosition(listaComponentes.size()-1);
            }
        });

        Button nuevoComponenteContador = (Button) findViewById(R.id.createTaskBotonContador);
        nuevoComponenteContador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskComponenteContador componenteContador = new TaskComponenteContador();
                listaComponentes.add(componenteContador);
                ((CreateTaskRecyclerViewAdapter)recyclerView.getAdapter()).insertarElemento(componenteContador);
                recyclerView.smoothScrollToPosition(listaComponentes.size()-1);
            }
        });

    }

    /**
     * En este metodo se comprueba si el estado de los componentes
     * permite pasar a guardar la informacion a disco
     * */
    private boolean verificarGuardarTask() {

        if (listaComponentes.size() == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(CreateTaskActivity.this);
            builder.setTitle("Atencion");
            builder.setMessage("Debe haber al menos un componente en la tarea a crear");
            builder.setPositiveButton("OK", null);
            builder.show();

        } else {

            boolean componentesCorrectos = true;

            for (TaskComponente componente : listaComponentes) {

                switch (componente.getTipoComponete().getTipoComponente()) {

                    case TaskComponenteTipo.TEXTO:
                        if (((TaskComponenteTexto)componente).getTexto().equals("")) {
                            componentesCorrectos = false;
                        }
                        break;

                    case TaskComponenteTipo.IMAGEN:
                        if (((TaskComponenteImagen)componente).getListaNombresImagenes().size() == 0) {
                            componentesCorrectos = false;
                        }
                        break;

                    case TaskComponenteTipo.ENLACE:
                        if (((TaskComponenteEnlace)componente).getEnlace().equals("")) {
                            componentesCorrectos = false;
                        }
                        break;

                    case TaskComponenteTipo.CONTADOR:
                        if (((TaskComponenteContador)componente).getDescripcion().equals("") || ((TaskComponenteContador)componente).getCantidadElementosMaxima() == 0) {
                            componentesCorrectos = false;
                        }
                        break;

                }

            }

            if (!componentesCorrectos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateTaskActivity.this);
                builder.setTitle("Atencion");
                builder.setMessage("En alguno de los componentes insertados no se han establecido sus propiedades correctamente. " +
                        "Si es texto verifique que se ha introducido algun texto, si son imagenes que se ha seleccionado alguna imagen," +
                        " si son enlaces que se ha verificado realmente algun enlace y si es un contador que posee descripcion y un numero de elementos mayor a 0");
                builder.setPositiveButton("OK", null);
                builder.show();
                return false;
            }

            return true;

        }

        return false;

    }

    //-----------------------------------------------------------------
    //-----------------------------------------------------------------
    //---------------- FUNCIONES SOBRE EL RECYCLER VIEW ---------------
    //-----------------------------------------------------------------
    //-----------------------------------------------------------------

    /**
     * Este metodo se encarga de inicializar las propiedades del recycler view
     * */

    private void inicializarRecyclerView() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int anchoCelda = dm.widthPixels / 100;
        int alturaCelda = dm.heightPixels / 100;

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.bringToFront();

        final CreateTaskRecyclerViewAdapter adapter = new CreateTaskRecyclerViewAdapter(this, listaComponentes, anchoCelda, alturaCelda);
        adapter.setmRecyclerViewAdapterListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDeleteItem(int position) {
        listaComponentes.remove(position);
        //((CreateTaskRecyclerViewAdapter)recyclerView.getAdapter()).acutalizarInforamcion(listaComponentes);
        ((CreateTaskRecyclerViewAdapter)recyclerView.getAdapter()).eliminarComponente(position);
    }

    @Override
    public void onOcultarTeclado(boolean isOcultar) {

        if (isOcultar) {
            botoneraView.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) MyApp.getContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else {
            botoneraView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onActualizarComponenteTexto(String texto, int posicion) {
        ((TaskComponenteTexto)listaComponentes.get(posicion)).setTexto(texto);
    }

    @Override
    public void onMostrarSelectorImagenes(int posicion) {
        componenteSeleccionado = posicion;
        mostrarSelectorImagenes();
    }

    @Override
    public void onMostrarCamara(int posicion) {
        componenteSeleccionado = posicion;
        mostrarCamara();
    }

    @Override
    public void onActualizarComponenteEnlace(String enlace, boolean isVideo, int posicion) {
        ((TaskComponenteEnlace)listaComponentes.get(posicion)).setEnlace(enlace);
        ((TaskComponenteEnlace)listaComponentes.get(posicion)).setEnlaceVideo(isVideo);
    }

    @Override
    public void onActualizarComponeteContador(String descripcion, int contador, int posicion) {
        ((TaskComponenteContador)listaComponentes.get(posicion)).setDescripcion(descripcion);
        ((TaskComponenteContador)listaComponentes.get(posicion)).setCantidadElementosMaxima(contador);
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cabeceraView.setVisibility(View.GONE);
            cabeceraView.invalidate();
            botoneraView.setVisibility(View.GONE);
            botoneraView.invalidate();
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) guidelineSuperior.getLayoutParams();
            layoutParams.guidePercent = 0;
            guidelineSuperior.setLayoutParams(layoutParams);
            layoutParams = (ConstraintLayout.LayoutParams) guidelineInferior.getLayoutParams();
            layoutParams.guidePercent = 1;
            guidelineInferior.setLayoutParams(layoutParams);
        } else {
            cabeceraView.setVisibility(View.VISIBLE);
            cabeceraView.invalidate();
            botoneraView.setVisibility(View.VISIBLE);
            botoneraView.invalidate();
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) guidelineSuperior.getLayoutParams();
            layoutParams.guidePercent = 0.1f;
            guidelineSuperior.setLayoutParams(layoutParams);
            layoutParams = (ConstraintLayout.LayoutParams) guidelineInferior.getLayoutParams();
            layoutParams.guidePercent = 0.9f;
            guidelineInferior.setLayoutParams(layoutParams);
        }

    }*/

    /*@Override
    public void onJuegoCreado() {
        //inicializarRecyclerView();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainViewRecyclerView);
        ((MainViewRecyclerViewAdapter)recyclerView.getAdapter()).acutalizarInforamcion();
    }

    @Override
    public void onJuegoBorrado(int position) {
        //inicializarRecyclerView();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainViewRecyclerView);
        ((MainViewRecyclerViewAdapter)recyclerView.getAdapter()).acutalizarInforamcion();
    }*/

    //-----------------------------------------------------------------
    //-----------------------------------------------------------------
    //----------- FUNCIONES SOBRE LA SELECCION DE IMAGENES ------------
    //-----------------------------------------------------------------
    //-----------------------------------------------------------------

    /**
     * Este metodo lleva a cabo las tareas necesarias para mostar el
     * selector de imagenes para la creacion de un nuevo task
     * */
    private void mostrarSelectorImagenes() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openDocuments(this, 0);
        }

    }

    /**
     * Este metodo lleva a cabo las tareas necesarias para mostar la
     * camara en el proceso de creacion de un nuevo task
     * */
    private void mostrarCamara() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openCamera(this, 0);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                if (imageFiles.size() > 0 && componenteSeleccionado >= 0) {
                    ((TaskComponenteImagen)listaComponentes.get(componenteSeleccionado)).getListaNombresImagenes().add(imageFiles.get(0).getAbsolutePath());
                    ((CreateTaskRecyclerViewAdapter) recyclerView.getAdapter()).actualizarComponente(componenteSeleccionado);
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(CreateTaskActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }

        });

    }

    /**
     * Este metodo establece el color del status bar
     * */
    public void setStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.

            int statusBarColor = Color.parseColor("#655cb0");
            //int statusBarColor = Color.parseColor("#3e3c51");

            if (statusBarColor == Color.BLACK && getWindow().getNavigationBarColor() == Color.BLACK) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }

            getWindow().setStatusBarColor(statusBarColor);

        }

    }

}
