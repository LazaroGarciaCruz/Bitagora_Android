package com.lazarogarciacruz.bitagora.vista.newTaskScreen;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.modelo.TaskComponente;
import com.lazarogarciacruz.bitagora.modelo.TaskDificultad;
import com.lazarogarciacruz.bitagora.modelo.TaskPrioridad;
import com.lazarogarciacruz.bitagora.utilidades.AnimatorManager;
import com.lazarogarciacruz.bitagora.utilidades.DataMaganer;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;

import java.util.LinkedList;

/**
 * create an instance of this fragment.
 */
public class NuevoTaskFragment extends Fragment {

    private TaskDificultad dificultad = new TaskDificultad(TaskDificultad.FACIL);
    private TaskPrioridad prioridad = new TaskPrioridad(TaskPrioridad.BAJA);
    private String textoNombre = "";

    private View view;
    private TextView textoTitulo;
    private Button botonCerrar;

    private EditText textoNombreTask;
    private Button botonDificultadFacil;
    private Button botonDificultadNormal;
    private Button botonDificultadDificil;
    private Button botonPrioridadBaja;
    private Button botonPrioridadMedia;
    private Button botonPrioridadAlta;
    private Button botonGuardar;

    private CreateTaskActivity mainContext;
    private int juegoSeleccionado = -1;
    private LinkedList<TaskComponente> listaComponentes;

    /**
     * @return A new instance of fragment NuevoTaskFragment.
     */
    public static NuevoTaskFragment newInstance() {
        return new NuevoTaskFragment();
    }

    public NuevoTaskFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.nuevo_task_fragment, container, false);

        textoTitulo = (TextView) view.findViewById(R.id.taskFragmentTitulo);
        textoTitulo.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

        botonCerrar = (Button) view.findViewById(R.id.taskFragmentBotonCerrar);
        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarVentanaNuevoJuego();
            }
        });

        textoNombreTask = (EditText) view.findViewById(R.id.taskFragmentTextoNombre);
        textoNombreTask.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));

        botonDificultadFacil = (Button) view.findViewById(R.id.taskFragmentBotonDificultadFacil);
        botonDificultadFacil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dificultad = new TaskDificultad(TaskDificultad.FACIL);
                actualizarRepresentacionDificultad();
            }
        });
        botonDificultadNormal = (Button) view.findViewById(R.id.taskFragmentBotonDificultadNormal);
        botonDificultadNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dificultad = new TaskDificultad(TaskDificultad.NORMAL);
                actualizarRepresentacionDificultad();
            }
        });
        botonDificultadDificil = (Button) view.findViewById(R.id.taskFragmentBotonDificultadDificil);
        botonDificultadDificil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dificultad = new TaskDificultad(TaskDificultad.DIFICIL);
                actualizarRepresentacionDificultad();
            }
        });
        botonPrioridadBaja = (Button) view.findViewById(R.id.taskFragmentBotonPrioridadBaja);
        botonPrioridadBaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prioridad = new TaskPrioridad(TaskPrioridad.BAJA);
                actualizarRepresentacionPrioridad();
            }
        });
        botonPrioridadMedia = (Button) view.findViewById(R.id.taskFragmentBotonPrioridadMedia);
        botonPrioridadMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prioridad = new TaskPrioridad(TaskPrioridad.MEDIA);
                actualizarRepresentacionPrioridad();
            }
        });
        botonPrioridadAlta = (Button) view.findViewById(R.id.taskFragmentBotonPrioridadAlta);
        botonPrioridadAlta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prioridad = new TaskPrioridad(TaskPrioridad.ALTA);
                actualizarRepresentacionPrioridad();
            }
        });

        botonGuardar = (Button) view.findViewById(R.id.taskFragmentBotonGuardar);
        botonGuardar.setTypeface(FontManager.getInstance().getFont("fonts/Pixelade.ttf"));
        botonGuardar.setEnabled(false);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearNuevoTask();
            }
        });

        View background = view.findViewById(R.id.mainViewBlackBackground);
        View componentes = view.findViewById(R.id.mainViewNuevoJuegoComponentes);

        //Determinamos la altura del panel de los componentes en funcion del tamaño de la pantalla
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) componentes.getLayoutParams();
        params.height = Math.round(MyApp.getInstance().getHeightPixels() * (MyApp.getInstance().isSmallScreen() ? 0.45f : 0.40f));
        params.width = Math.round(MyApp.getInstance().getWidthPixels() * (MyApp.getInstance().isSmallScreen() ? 0.75f : 0.68f));

        AnimatorManager.getInstance().animarVentanaNuevoTask(this, background, componentes, true);

        return view;

    }

    /**
     * Este metodo se llama cuando el Fragment se asocia
     * a una vista despues del metodo anterior
     * */
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        //Con este codigo controlamos cuando se muestra o no el teclado
        //y ajustamos los parametros de la pantalla en consecuencia

        mainContext = (CreateTaskActivity) context;
        final View activityRootView = mainContext.findViewById(R.id.createTaskViewFrameRoot);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                float dtToPx = dpToPx(mainContext, 200);
                if (heightDiff < dtToPx) {
                    //No hay teclado
                    botonGuardar.setEnabled(false);
                    if (textoNombreTask.getText().length() > 0) {
                        textoNombre = textoNombreTask.getText().toString();
                        botonGuardar.setEnabled(true);
                        textoNombreTask.clearFocus();
                    }
                }
            }
        });

    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public void setDatosGenerales(LinkedList<TaskComponente> listaComponentes, int juegoSeleccionado) {
        this.listaComponentes = listaComponentes;
        this.juegoSeleccionado = juegoSeleccionado;
    }

    //-----------------------------------------------------------------
    //-----------------------------------------------------------------
    //-------------------- FUNCIONES GENERALES ------------------------
    //-----------------------------------------------------------------
    //-----------------------------------------------------------------

    /**
     * En este metodo se llevan a cabo las animaciones y tareas
     * necesarias para ocultar la ventana de Nuevo Juego
     * */
    private void ocultarVentanaNuevoJuego() {

        View background = view.findViewById(R.id.mainViewBlackBackground);
        View componentes = view.findViewById(R.id.mainViewNuevoJuegoComponentes);
        AnimatorManager.getInstance().animarVentanaNuevoTask(this, background, componentes, false);

        View view = mainContext.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)mainContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para actualizar
     * la representacion visual de los componentes de la dificultad
     * */
    private void actualizarRepresentacionDificultad() {

        switch (dificultad.getDificultad()) {

            case TaskDificultad.FACIL:
                botonDificultadFacil.setBackground(mainContext.getDrawable(R.drawable.dificultad_facil_color));
                botonDificultadNormal.setBackground(mainContext.getDrawable(R.drawable.dificultad_normal_desactivado));
                botonDificultadDificil.setBackground(mainContext.getDrawable(R.drawable.dificultad_dificil_desactivado));
                break;
            case TaskDificultad.NORMAL:
                botonDificultadFacil.setBackground(mainContext.getDrawable(R.drawable.dificultad_facil_desactivado));
                botonDificultadNormal.setBackground(mainContext.getDrawable(R.drawable.dificultad_normal_color));
                botonDificultadDificil.setBackground(mainContext.getDrawable(R.drawable.dificultad_dificil_desactivado));
                break;
            case TaskDificultad.DIFICIL:
                botonDificultadFacil.setBackground(mainContext.getDrawable(R.drawable.dificultad_facil_desactivado));
                botonDificultadNormal.setBackground(mainContext.getDrawable(R.drawable.dificultad_normal_desactivado));
                botonDificultadDificil.setBackground(mainContext.getDrawable(R.drawable.dificultad_dificl_color));
                break;

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para actualizar
     * la representacion visual de los componentes de la prioridad
     * */
    private void actualizarRepresentacionPrioridad() {

        switch (prioridad.getPrioridad()) {

            case TaskPrioridad.BAJA:
                botonPrioridadBaja.setBackground(mainContext.getDrawable(R.drawable.prioridad_baja_star_color));
                botonPrioridadMedia.setBackground(mainContext.getDrawable(R.drawable.prioridad_media_star_desactivado));
                botonPrioridadAlta.setBackground(mainContext.getDrawable(R.drawable.prioridad_alta_star_desactivado));
                break;
            case TaskPrioridad.MEDIA:
                botonPrioridadBaja.setBackground(mainContext.getDrawable(R.drawable.prioridad_baja_star_desactivado));
                botonPrioridadMedia.setBackground(mainContext.getDrawable(R.drawable.prioridad_media_star_color));
                botonPrioridadAlta.setBackground(mainContext.getDrawable(R.drawable.prioridad_alta_star_desactivado));
                break;
            case TaskPrioridad.ALTA:
                botonPrioridadBaja.setBackground(mainContext.getDrawable(R.drawable.prioridad_baja_star_desactivado));
                botonPrioridadMedia.setBackground(mainContext.getDrawable(R.drawable.prioridad_media_star_desactivado));
                botonPrioridadAlta.setBackground(mainContext.getDrawable(R.drawable.prioridad_alta_star_color));
                break;

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * almacenar la informacion de un nuevo task
     * */
    private void crearNuevoTask() {
        DataMaganer.getInstance().almacenarNuevoTask(textoNombre, dificultad, prioridad, listaComponentes, juegoSeleccionado);
        this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        mainContext.onBackPressed();
    }

}
