package com.lazarogarciacruz.bitagora.vista.mainScreen;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.utilidades.AnimatorManager;
import com.lazarogarciacruz.bitagora.utilidades.CustomFontText;
import com.lazarogarciacruz.bitagora.utilidades.DataMaganer;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;
import com.lazarogarciacruz.bitagora.vista.tasksScreen.TaskLisActivity;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements
                                                        MainViewRecyclerViewAdapter.ItemClickListener,
                                                        MainViewRecyclerViewAdapter.DeleteItemListener,
                                                        DataMaganer.OnDatosJuegosActualizados {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101;
    private static final int REQUEST_CODE_CAMERA = 102;

    //Variables generales

    private boolean isSistemaIniciaodo = false;
    private boolean isTablaVisible = false;
    private int tipoActualRecyclerView = MainViewRecyclerViewAdapter.TIPO_GRID;

    //Variables para la seleccion de imagenes

    private NuevoJuegoFragment nuevoJuegoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Gestionamos los permisos de la aplicacion

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            DataMaganer.getInstance();
            DataMaganer.getInstance().setOnDatosJuegosActualizadosListener(this);
            isSistemaIniciaodo = true;
            inicializarRecyclerView();
        } else {
            gestionarPermisos(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            gestionarPermisos(Manifest.permission.CAMERA, REQUEST_CODE_CAMERA);
        }

        //Establecemos las propiedades del control de las medidas de la pantalla

        MyApp.getInstance().configureScreenSettings(getWindowManager().getDefaultDisplay());

        //Gestionamos la inicializacion de los componentes

        FontManager.init(getAssets());

        inicializarCabecera();
        inicializarRecyclerView();
        gestionarScrollRecyclerView();
        inicializarComponentesGenerales();
        //setStatusBarColor();

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

        try {

            ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.mainViewBotonera);
            constraintLayout.bringToFront();

            Button botonNuevoJuego = (Button) findViewById(R.id.botonNuevoJuego);
            botonNuevoJuego.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));
            botonNuevoJuego.setTextColor(Color.WHITE);
            botonNuevoJuego.setShadowLayer(1, 5, 5, Color.BLACK);
            botonNuevoJuego.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    nuevoJuegoFragment = NuevoJuegoFragment.newInstance();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.mainViewFrameRoot, nuevoJuegoFragment, "nuevoJuegoFragment")
                            .commit();

                }
            });

            GifImageView gifImageView = (GifImageView) findViewById(R.id.mainViewCharacterNavi);
            GifDrawable drawable = new GifDrawable(this.getAssets(), "gif/navi_low.gif");
            gifImageView.setBackground(drawable);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para inicializar
     * los componente de la cabecera de la ventana principal de la aplicacion
     * */
    private void inicializarCabecera() {

        //Con este codigo establecemos el color del status bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //Con este codigo animamos el fondo de la cabecera

        final ImageView backgroundOne = (ImageView) findViewById(R.id.mainViewCabecera_background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.mainViewCabecera_background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();

        //Llamamos al metodo que anima el texto de la cabecera

        animarCabecera();

        //Gestionamos el elementos que permite cambiar la distribucion de los elementos

        try {

            GifImageView gifImageView = (GifImageView) findViewById(R.id.mainViewCharacter);
            GifDrawable drawable = new GifDrawable(this.getAssets(), "gif/orc_idle.gif");
            gifImageView.setBackground(drawable);
            gifImageView.bringToFront();

            //ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)gifImageView.getLayoutParams();
            //params.setMargins(0, MyApp.getInstance().isSmallScreen() ? 87 : 145, 0, 0); //substitute parameters for left, top, right, bottom
            //gifImageView.setLayoutParams(params);

            gifImageView = (GifImageView) findViewById(R.id.mainViewCharacterSpeech);
            drawable = new GifDrawable(this.getAssets(), "gif/speechLista.gif");
            drawable.setLoopCount(0);
            gifImageView.setBackground(drawable);
            gifImageView.bringToFront();

            //params = (ConstraintLayout.LayoutParams)gifImageView.getLayoutParams();
            //params.setMargins(0, MyApp.getInstance().isSmallScreen() ? 70 : 119, 10, 0); //substitute parameters for left, top, right, bottom
            //gifImageView.setLayoutParams(params);

            gifImageView = (GifImageView) findViewById(R.id.mainViewCharacterSwitch);
            drawable = new GifDrawable(this.getAssets(), "gif/switch_off.gif");
            gifImageView.setBackground(drawable);
            gifImageView.bringToFront();

            //params = (ConstraintLayout.LayoutParams)gifImageView.getLayoutParams();
            //params.setMargins(0, MyApp.getInstance().isSmallScreen() ? 118 : 186, MyApp.getInstance().isSmallScreen() ? 100 : 135, 0); //substitute parameters for left, top, right, bottom
            //gifImageView.setLayoutParams(params);

            ImageView viewFront = (ImageView)findViewById(R.id.mainViewBackgroundFront);
            viewFront.bringToFront();

            Guideline guideline = (Guideline) findViewById(R.id.guidelineCharacterHorizontal);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
            params.guidePercent = MyApp.getInstance().isSmallScreen() ? 0.065f : 0.075f; // 45% // range: 0 <-> 1
            guideline.setLayoutParams(params);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button botonCambiarLista = (Button)findViewById(R.id.botonCambiarLista);
        botonCambiarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarOrdenacionLista();
            }
        });

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para animar la cabecera
     * */
    private void animarCabecera() {

        String titulo = "BITAGORA";
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.mainViewTextoCabecera);

        int elemento = 0;
        int duracion = 250;
        int delay = 150;
        int indexInicial = 0;

        for (char caracter : titulo.toCharArray()) {

            CustomFontText textView = new CustomFontText(this);
            textView.setTypeface(FontManager.getInstance().getFont("fonts/PixelRunes.ttf"));
            textView.setText(" " + String.valueOf(caracter));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, MyApp.getInstance().isSmallScreen() ? 17 : 18);
            textView.setTextColor(Color.RED);
            textView.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            linearLayout.addView(textView, layoutParams);

            AnimatorManager.getInstance().animarTextoRunas(textView, indexInicial, duracion, delay * elemento);
            elemento++;

        }

    }

    /**
     * Este metodo cambiar la ordenacion de lista del formato tabla
     * al formato coleccion y viceversa
     * */
    private void cambiarOrdenacionLista() {

        try {

            final GifImageView gifImageView = (GifImageView) findViewById(R.id.mainViewCharacter);
            GifDrawable drawable = new GifDrawable(this.getAssets(), "gif/orc_attack_reverse.gif");
            drawable.setLoopCount(1);
            gifImageView.setBackground(drawable);
            drawable.start();
            drawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    try {
                        GifDrawable finalDrawable = new GifDrawable(MainActivity.this.getAssets(), "gif/orc_idle.gif");
                        gifImageView.setBackground(finalDrawable);
                    } catch (Exception e) {
                    }
                }
            });

            GifImageView gifImageViewSwitch = (GifImageView) findViewById(R.id.mainViewCharacterSwitch);
            drawable = new GifDrawable(this.getAssets(), isTablaVisible ? "gif/switch_off.gif" : "gif/switch_on.gif");
            drawable.setLoopCount(1);
            gifImageViewSwitch.setBackground(drawable);

            tipoActualRecyclerView = tipoActualRecyclerView == MainViewRecyclerViewAdapter.TIPO_GRID ? MainViewRecyclerViewAdapter.TIPO_TABLE : MainViewRecyclerViewAdapter.TIPO_GRID;
            inicializarRecyclerView();

        } catch (Exception e) {
            e.printStackTrace();
        }

        isTablaVisible = !isTablaVisible;

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

        if (isSistemaIniciaodo) {

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            int alturaCelda = -1;
            int numberOfColumns = -1;
            if (tipoActualRecyclerView == MainViewRecyclerViewAdapter.TIPO_GRID) {
                alturaCelda = (int) (dm.heightPixels / 3);
                numberOfColumns = 2;
            } else {
                alturaCelda = (int) (dm.heightPixels / 5);
                numberOfColumns = 1;
            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainViewRecyclerView);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.bringToFront();

            if (recyclerView.getAdapter() != null) {
                ((MainViewRecyclerViewAdapter) recyclerView.getAdapter()).switchAnimation(true);
            }

            final MainViewRecyclerViewAdapter adapter = new MainViewRecyclerViewAdapter(this, DataMaganer.getInstance().getDataset().getJuegos(), tipoActualRecyclerView, alturaCelda);
            adapter.setClickListener(this);
            adapter.setmDeleteItemListener(this);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns) {
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    //Sobreescribiendo este metodo y estableciendo la animacion
                    //a false conseguimos que solo se animen las celdas visibles
                    //en el momento de visualizar el recycler view
                    super.onLayoutChildren(recycler, state);
                    adapter.switchAnimation(false);
                }
            };

            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);

        }

    }

    private int scrollViewState = 0; //0 - IDLE, 1 - Up, 2 - Down
    private int screenPosition = 1; //0 - Izquierda, 1 - Centro, 2 - Derecha
    private boolean animteScrollView = false;

    /**
     * En este metodo se inicializa la gestion de los eventos de
     * scroll sobre el recycler view, que determinara la animacion
     * sobre los elementos del mismo
     * */
    private void gestionarScrollRecyclerView() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainViewRecyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                if ((newState == RecyclerView.SCROLL_STATE_IDLE && scrollViewState != 0)
                        || (newState == RecyclerView.SCROLL_STATE_SETTLING && scrollViewState != 0)) {
                    animteScrollView = false;
                    scrollViewState = 0;
                    GridLayoutManager layoutManager = ((GridLayoutManager)recyclerView.getLayoutManager());
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                    for (int i = 0; i <= DataMaganer.getInstance().getDataset().getJuegos().size(); i++) {
                        AnimatorManager.getInstance().animacionCeldasScroll(recyclerView.getChildAt(i), 0, screenPosition);
                    }
                }

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    animteScrollView = true;
                }
            }

            @Override
            public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (!recyclerView.canScrollVertically(-1)) {
                    //onScrolledToTop();
                } else if (!recyclerView.canScrollVertically(1)) {
                    //onScrolledToBottom();
                }

                if (animteScrollView) {

                    boolean animate = false;
                    int offset = -1;

                    if (dy < 0) {
                        //onScrolledUp(dy);
                        if (scrollViewState != 1) {
                            animate = true;
                            offset = 5;
                            scrollViewState = 1;
                        }
                    } else if (dy > 0) {
                        //onScrolledDown(dy);
                        if (scrollViewState != 2) {
                            animate = true;
                            offset = -5;
                            scrollViewState = 2;
                        }
                    }

                    if (animate) {

                        GridLayoutManager layoutManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                        //for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                        for (int i = 0; i <= DataMaganer.getInstance().getDataset().getJuegos().size(); i++) {
                            AnimatorManager.getInstance().animacionCeldasScroll(recyclerView.getChildAt(i), offset, screenPosition);
                        }

                    }

                }

            }

        });

        //Con este metodo determinamos en que posicion del recycler view
        //pulsado el usuario cuando se hace scroll en el mismo

        recyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                int limiteIzquierdo = dm.widthPixels / 3;
                int limiteDerecho = limiteIzquierdo * 2;

                if (event.getX() > 0 && event.getX() < limiteIzquierdo) {
                    screenPosition = 0;
                } else if (event.getX() > limiteIzquierdo && event.getX() < limiteDerecho) {
                    screenPosition = 1;
                } else {
                    screenPosition = 2;
                }

                return false;

            }

        });

    }

    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent(this, TaskLisActivity.class);
        intent.putExtra("JUEGO", position);
        startActivity(intent);

    }

    @Override
    public void onDeleteItem(int position) {
        DataMaganer.getInstance().borrarJuego(position);
    }

    @Override
    public void onJuegoCreado() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainViewRecyclerView);
        ((MainViewRecyclerViewAdapter)recyclerView.getAdapter()).insertarElemento(DataMaganer.getInstance().getDataset().getJuegos().getLast());
        recyclerView.smoothScrollToPosition(DataMaganer.getInstance().getDataset().getJuegos().size()-1);
        //((MainViewRecyclerViewAdapter)recyclerView.getAdapter()).acutalizarInforamcion();
    }

    @Override
    public void onJuegoBorrado(int position) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainViewRecyclerView);
        ((MainViewRecyclerViewAdapter)recyclerView.getAdapter()).eliminarComponente(position);
        //((MainViewRecyclerViewAdapter)recyclerView.getAdapter()).acutalizarInforamcion();
    }

    //-----------------------------------------------------------------
    //-----------------------------------------------------------------
    //------------- FUNCIONES SOBRE LA GESTION DE PERMISOS ------------
    //-----------------------------------------------------------------
    //-----------------------------------------------------------------

    /**
     * En este metodo se llevan a cabo los procesos necesarios
     * para gestionar los permisos de la aplicacion
     * */
    private boolean gestionarPermisos(final String tipoPermiso, final int requestCode) {

        int permiso = ContextCompat.checkSelfPermission(this, tipoPermiso);

        if (permiso != PackageManager.PERMISSION_GRANTED) {

            //makeRequest(Manifest.permission.RECORD_AUDIO, REQUEST_CODE_RECORD_AUDIO);

            //Este codigo es recomendacion por parte de google y comprueba si deberia de
            //pedirse el permiso aunque el usuario haya pulsado en "no volver a mostrar"
            //en la ventana por defecto de peticion de permisos
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Esta app requiere permisio para acceder al almacenamiento interno").setTitle("Recuerda");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        makeRequest(tipoPermiso, requestCode);
                    }
                });
                AlertDialog dialogo = builder.create();
                dialogo.show();
            } else {
                makeRequest(tipoPermiso,requestCode);
            }

        } else {
            return true;
        }

        return false;

    }

    /**
     * En este metodo se realiza la peticion de los permisos
     * */
    protected void makeRequest(String permiso, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permiso}, requestCode);
    }

    /**
     * En este metodo se gestionan las acciones que se ejecutan
     * despues de que el usuario haya gestionado los permisos
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:

                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "No se puede acceder al almacenamiento interno por lo que no se cargarán los datos", Toast.LENGTH_LONG).show();
                } else {
                    DataMaganer.getInstance();
                    DataMaganer.getInstance().setOnDatosJuegosActualizadosListener(this);
                    isSistemaIniciaodo = true;
                    inicializarRecyclerView();
                }

                break;

        }

    }

    /**
     * Este metodo establece el color del status bar
     * */
    public void setStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.

            //int statusBarColor = Color.parseColor("#172c31");
            int statusBarColor = Color.parseColor("#3e3c51");

            if (statusBarColor == Color.BLACK && getWindow().getNavigationBarColor() == Color.BLACK) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }

            getWindow().setStatusBarColor(statusBarColor);

        }

    }

    /**
     * Con este metodo definimos la pantalla en full screen mode
     * */
    /*private void setFullScreenMode() {

        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

    }*/

}
