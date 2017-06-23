package com.lazarogarciacruz.bitagora.vista.detailTaskScreen;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.modelo.Task;
import com.lazarogarciacruz.bitagora.utilidades.DataMaganer;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;

public class TaskDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout scanlinesLayout;

    private int anchoCelda = -1;
    private int alturaCelda = -1;

    private Task taskSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            taskSeleccionado = DataMaganer.getInstance().getDataset().getTasks().get(bundle.getInt("TASK"));
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

        recyclerView = (RecyclerView) findViewById(R.id.taskDetailRecyclerView);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        anchoCelda = dm.widthPixels / 100;
        alturaCelda = dm.heightPixels / 100;

        //---------------------------------------------------
        //-------------------- SCANLINES --------------------
        //---------------------------------------------------

        scanlinesLayout = (LinearLayout) findViewById(R.id.taskDetailScanlines);

        int totalImagenes = 16;
        //int totalImagenes = 8;
        int heigth = dm.heightPixels / totalImagenes;

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

        Button botonAtras = (Button) findViewById(R.id.taskDetailBotonAtras);
        botonAtras.setTextSize(12);
        botonAtras.setTypeface(FontManager.getInstance().getFont("fonts/EarlyGameBoy.ttf"));
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskDetailActivity.super.onBackPressed();
            }
        });

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

        if (taskSeleccionado != null) {

            //adapter.setmRecyclerViewAdapterListener(this);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            recyclerView.setAdapter(new TaskDetailRecyclerViewAdapter(this, taskSeleccionado, taskSeleccionado.getListaComponentes(), anchoCelda, alturaCelda));

        }

    }

    /**
     * Este metodo establece el color del status bar
     * */
    public void setStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.

            int statusBarColor = Color.parseColor("#E1DFAE");
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
