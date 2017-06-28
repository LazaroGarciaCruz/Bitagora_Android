package com.lazarogarciacruz.bitagora.vista.tasksScreen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.modelo.Juego;
import com.lazarogarciacruz.bitagora.modelo.Task;
import com.lazarogarciacruz.bitagora.utilidades.DataMaganer;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;
import com.lazarogarciacruz.bitagora.utilidades.TypewriterText;
import com.lazarogarciacruz.bitagora.vista.detailTaskScreen.TaskDetailActivity;
import com.lazarogarciacruz.bitagora.vista.newTaskScreen.CreateTaskActivity;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TaskLisActivity extends AppCompatActivity implements
                                                        TaskListRecyclerViewAdapter.ItemClickListener,
                                                        TaskListRecyclerViewAdapter.DeleteItemListener {

    private LinkedList<Task> listaTask = new LinkedList<>();
    private RecyclerView recyclerView;
    private int alturaCelda;
    private int juegoSeleccionado = -1;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            juegoSeleccionado = bundle.getInt("JUEGO");
        }

        inicializarComponentesGenerales();
        inicializarCabecera();
        inicializarRecyclerView();
        //setStatusBarColor();

    }

    @Override
    protected void onRestart() {

        super.onRestart();

        if (DataMaganer.getInstance().getDataset().getTasks().size() != listaTask.size()) {

            listaTask.clear();
            Juego juego = DataMaganer.getInstance().getDataset().getJuegos().get(juegoSeleccionado);
            for (Task task : DataMaganer.getInstance().getDataset().getTasks()) {
                if (task.getIdJuego().equals(juego.getId())) {
                    listaTask.add(task);
                }
            }
            ordenarListaFechaDescendente();

            final TaskListRecyclerViewAdapter adapter = new TaskListRecyclerViewAdapter(this, listaTask, alturaCelda);
            adapter.setClickListener(this);
            adapter.setmDeleteItemListener(this);
            recyclerView.setAdapter(adapter);

        }

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

        recyclerView = (RecyclerView) findViewById(R.id.taskListViewRecyclerView);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        alturaCelda = dm.heightPixels / 100;

        //Copiamos los distintos Task a la lista que va a contenerlos
        //y que es susceptible de ser reordenada

        listaTask.clear();
        Juego juego = DataMaganer.getInstance().getDataset().getJuegos().get(juegoSeleccionado);
        for (Task task : DataMaganer.getInstance().getDataset().getTasks()) {
            if (task.getIdJuego().equals(juego.getId())) {
                listaTask.add(task);
            }
        }
        ordenarListaFechaDescendente();

        //Configuramos el selector de ordenacion

        final BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.bubble_layout, null);
        popupWindow = BubblePopupHelper.create(this, bubbleLayout);

        bubbleLayout.findViewById(R.id.botonFechaAscendente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reordenarListaTasks("FECHA_ASCENDENTE");
                popupWindow.dismiss();
            }
        });

        bubbleLayout.findViewById(R.id.botonFechaDescendente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reordenarListaTasks("FECHA_DESCENDENTE");
                popupWindow.dismiss();
            }
        });

        bubbleLayout.findViewById(R.id.botonDificultadAscendente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reordenarListaTasks("DIFICULTAD_ASCENDENTE");
                popupWindow.dismiss();
            }
        });

        bubbleLayout.findViewById(R.id.botonDificultadDescendente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reordenarListaTasks("DIFICULTAD_DESCENDENTE");
                popupWindow.dismiss();
            }
        });

        bubbleLayout.findViewById(R.id.botonPrioridadAscendente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reordenarListaTasks("PRIORIDAD_ASCENDENTE");
                popupWindow.dismiss();
            }
        });

        bubbleLayout.findViewById(R.id.botonPrioridadDescendente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reordenarListaTasks("PRIORIDAD_DESCENDENTE");
                popupWindow.dismiss();
            }
        });

        Button botonFiltrar = (Button) findViewById(R.id.taskListViewBotonFiltrar);
        botonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                v.getLocationInWindow(location);
                bubbleLayout.setArrowPosition(location[0]/2 + bubbleLayout.getArrowWidth() * 2);
                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], v.getHeight() + location[1]);
            }
        });

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para inicializar
     * los componente de la cabecera de la ventana principal de la aplicacion
     * */
    private void inicializarCabecera() {

        //Con este codigo animamos el fondo de la cabecera

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        try {

            GifImageView gifImageView = (GifImageView) findViewById(R.id.taskListViewCabeceraBackground);
            GifDrawable drawable = new GifDrawable(this.getAssets(), "gif/pixel_city_rain.gif");
            gifImageView.setBackground(drawable);
            gifImageView.setScaleX(2.0f);
            gifImageView.setScaleY(4.0f);
            gifImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (!MyApp.getInstance().isSmallScreen()) {

                float scaleX = MyApp.getInstance().isSmallScreen() ? 1f : 1.5f;
                float scaleY = MyApp.getInstance().isSmallScreen() ? 1f : 1.5f;

                gifImageView = (GifImageView) findViewById(R.id.taskListViewCabeceraCharacter1);
                drawable = new GifDrawable(this.getAssets(), "gif/steampunk_character1.gif");
                gifImageView.setBackground(drawable);
                gifImageView.setScaleX(scaleX);
                gifImageView.setScaleY(scaleY);
                gifImageView.setScaleType(ImageView.ScaleType.FIT_XY);

                gifImageView = (GifImageView) findViewById(R.id.taskListViewCabeceraCharacter2);
                drawable = new GifDrawable(this.getAssets(), "gif/steampunk_character2.gif");
                gifImageView.setBackground(drawable);
                gifImageView.setScaleX(scaleX);
                gifImageView.setScaleY(scaleY);
                gifImageView.setScaleType(ImageView.ScaleType.FIT_XY);

                gifImageView = (GifImageView) findViewById(R.id.taskListViewCabeceraCharacter3);
                drawable = new GifDrawable(this.getAssets(), "gif/steampunk_character3.gif");
                gifImageView.setBackground(drawable);
                gifImageView.setScaleX(scaleX);
                gifImageView.setScaleY(scaleY);
                gifImageView.setScaleType(ImageView.ScaleType.FIT_XY);

                gifImageView = (GifImageView) findViewById(R.id.taskListViewCabeceraCharacter4);
                drawable = new GifDrawable(this.getAssets(), "gif/steampunk_character4.gif");
                gifImageView.setBackground(drawable);
                gifImageView.setScaleX(scaleX);
                gifImageView.setScaleY(scaleY);
                gifImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        TypewriterText textoAtras = (TypewriterText) findViewById(R.id.taskListViewTextoAtras);
        textoAtras.setTextSize(20);
        textoAtras.setTextColor(Color.GREEN);
        textoAtras.setShadowLayer(10, 0, 0, Color.GREEN);
        textoAtras.setTypeface(FontManager.getInstance().getFont("fonts/Bitwise.ttf"));
        textoAtras.setTypingSpeed(50);
        textoAtras.setTextAutoTyping("ATRAS", "_");
        textoAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskLisActivity.super.onBackPressed();
            }
        });

        Button botonFiltrar = (Button) findViewById(R.id.taskListViewBotonFiltrar);
        botonFiltrar.setShadowLayer(10, 0, 0, Color.GREEN);

        Button botonCrearTrask = (Button) findViewById(R.id.taskListViewBotonCrearTask);
        botonCrearTrask.setTextColor(Color.GREEN);
        botonCrearTrask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskLisActivity.this, CreateTaskActivity.class);
                intent.putExtra("JUEGO", juegoSeleccionado);
                startActivity(intent);
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

        int numberOfColumns = 1;

        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        final TaskListRecyclerViewAdapter adapter = new TaskListRecyclerViewAdapter(this, listaTask, alturaCelda);
        adapter.setmDeleteItemListener(this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {

        Task task = listaTask.get(position);

        for (int i = 0; i < DataMaganer.getInstance().getDataset().getTasks().size(); i++) {
            Task taskAux = DataMaganer.getInstance().getDataset().getTasks().get(i);
            if (task.getId().equals(taskAux.getId())) {
                Intent intent = new Intent(this, TaskDetailActivity.class);
                intent.putExtra("TASK", i);
                startActivity(intent);
                break;
            }
        }



    }

    @Override
    public void onDeleteItem(int position) {

        Task task = listaTask.get(position);

        for (int i = 0; i < DataMaganer.getInstance().getDataset().getTasks().size(); i++) {
            Task taskAux = DataMaganer.getInstance().getDataset().getTasks().get(i);
            if (task.getId().equals(taskAux.getId())) {
                DataMaganer.getInstance().borrarTask(i);
                listaTask.remove(position);
                break;
            }
        }

        listaTask.clear();
        Juego juego = DataMaganer.getInstance().getDataset().getJuegos().get(juegoSeleccionado);
        for (Task taskAux : DataMaganer.getInstance().getDataset().getTasks()) {
            if (taskAux.getIdJuego().equals(juego.getId())) {
                listaTask.add(taskAux);
            }
        }
        ordenarListaFechaDescendente();

        final TaskListRecyclerViewAdapter adapter = new TaskListRecyclerViewAdapter(this, listaTask, alturaCelda);
        adapter.setClickListener(this);
        adapter.setmDeleteItemListener(this);
        recyclerView.setAdapter(adapter);

    }

    //-----------------------------------------------------------------
    //-----------------------------------------------------------------
    //-------------------- FUNCIONES GENERALES ------------------------
    //-----------------------------------------------------------------
    //-----------------------------------------------------------------

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * reordenar la lista de tareas que se representa en la pantalla
     * */
    private void reordenarListaTasks(String tipoOrdenacion) {

        switch (tipoOrdenacion) {

            case "FECHA_ASCENDENTE":
                ordenarListaFechaAscendente();
                break;

            case "FECHA_DESCENDENTE":
                ordenarListaFechaDescendente();
                break;

            case "DIFICULTAD_ASCENDENTE":
                //ordenarListaFechaAscendente();
                ordenarListaDificultadAscendente();
                break;

            case "DIFICULTAD_DESCENDENTE":
                //ordenarListaFechaDescendente();
                ordenarListaDificultadDescendente();
                break;

            case "PRIORIDAD_ASCENDENTE":
                //ordenarListaFechaAscendente();
                ordenarListaPrioridadAscendente();
                break;

            case "PRIORIDAD_DESCENDENTE":
                //ordenarListaFechaDescendente();
                ordenarListaPrioridadDescendente();
                break;

        }

        final TaskListRecyclerViewAdapter adapter = new TaskListRecyclerViewAdapter(this, listaTask, alturaCelda);
        adapter.setmDeleteItemListener(this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    private void ordenarListaFechaAscendente() {

        Collections.sort(listaTask, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (o1.getCreationDate().getTime() > o2.getCreationDate().getTime() ? 1 : -1);
            }
        });

    }

    private void ordenarListaFechaDescendente() {

        Collections.sort(listaTask, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (o1.getCreationDate().getTime() > o2.getCreationDate().getTime() ? -1 : 1);
            }
        });

    }

    private void ordenarListaDificultadAscendente() {

        Collections.sort(listaTask, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (o1.getDificultad() > o2.getDificultad() ? 1 : -1);
            }
        });

    }

    private void ordenarListaDificultadDescendente() {

        Collections.sort(listaTask, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (o1.getDificultad() > o2.getDificultad() ? -1 : 1);
            }
        });

    }

    private void ordenarListaPrioridadAscendente() {

        Collections.sort(listaTask, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (o1.getPrioridad() > o2.getPrioridad() ? 1 : -1);
            }
        });

    }

    private void ordenarListaPrioridadDescendente() {

        Collections.sort(listaTask, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (o1.getPrioridad() > o2.getPrioridad() ? -1 : 1);
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

            int statusBarColor = Color.parseColor("#3e3c51");

            if (statusBarColor == Color.BLACK && getWindow().getNavigationBarColor() == Color.BLACK) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }

            getWindow().setStatusBarColor(statusBarColor);

        }

    }


}
