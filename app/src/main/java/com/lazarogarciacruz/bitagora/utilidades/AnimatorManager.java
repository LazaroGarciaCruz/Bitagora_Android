package com.lazarogarciacruz.bitagora.utilidades;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.modelo.TaskDificultad;
import com.lazarogarciacruz.bitagora.modelo.TaskPrioridad;
import com.lazarogarciacruz.bitagora.vista.mainScreen.MainViewRecyclerViewAdapter;
import com.lazarogarciacruz.bitagora.vista.mainScreen.NuevoJuegoFragment;
import com.lazarogarciacruz.bitagora.vista.newTaskScreen.NuevoTaskFragment;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by lazarogarciacruz on 1/6/17.
 */

public class AnimatorManager {

    private static AnimatorManager instance;

    private String colorRunas[] = {"#FF0000", "#FF4500", "#FF6347", "#FF7F50", "#FFA500", "#FFD700", "#FFFF66", "#FFFFCC", "#FFFFFF"
    /*"#000000", "#808080", "#C0C0C0", "#F5F5F5", "#FFFFFF", "#FFFFCC", "#FFFF66", "#FFD700", "#FFA500", "#FF7F50",
            "#FF6347", "#FF4500", "#FF0000", "#FF0000", "#FF4500", "#FF6347", "#FF7F50", "#FFA500", "#FFD700", "#FFFF66", "#FFFFCC", "#FFFFFF",
            "#F5F5F5", "#C0C0C0", "#808080", "#000000"*/};

    /**
     * Constructor por defecto del singleton
     * */
    public static AnimatorManager getInstance() {
        if (instance == null) {
            instance = new AnimatorManager();
        }
        return instance;
    }

    /**
     * En este metodo se establecen las propiedades de la
     * animacion del texto de la cabecera de la pantalla principal
     * */
    public void animarTextoRunas(final CustomFontText texto, final int index, final int duration, final int delay) {

        if (index + 1 < colorRunas.length) {

            final float[] from = new float[3];
            final float[] to = new float[3];

            Color.colorToHSV(Color.parseColor(colorRunas[index]), from);
            Color.colorToHSV(Color.parseColor(colorRunas[index + 1]), to);

            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            animator.setDuration(duration);
            animator.setStartDelay(delay);

            final float[] hsv = new float[3];// transition color
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // Transition along each axis of HSV (hue, saturation, value)
                    hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                    hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                    hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();
                    texto.setTextColor(Color.HSVToColor(hsv));
                    texto.setShadowLayer(10, 0, 0, Color.HSVToColor(hsv));
                }
            });

            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    texto.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animarTextoRunas(texto, index+1, duration, 0);
                }
            });

            animator.start();

        } else {

            ValueAnimator rotateAnimacion = ValueAnimator.ofFloat(0, 1);
            rotateAnimacion.setDuration(250);
            rotateAnimacion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator pAnimation) {
                    float value = (Float) (pAnimation.getAnimatedValue());
                    texto.setRotationY(90 * value);
                }
            });

            rotateAnimacion.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    texto.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));
                    texto.setShadowLayer(1, 5, 5, Color.BLACK);
                    ValueAnimator rotateAnimacion = ValueAnimator.ofFloat(1, 0);
                    rotateAnimacion.setDuration(250);
                    rotateAnimacion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator pAnimation) {
                            float value = (Float) (pAnimation.getAnimatedValue());
                            texto.setRotationY(90 * value);
                        }
                    });
                    rotateAnimacion.start();
                }
            });

            rotateAnimacion.start();

        }

    }

    /**
     * En este metodo se definen las animaciones de carga de las celdas
     * del recycler view de la ventana principal
     * */
    public void animacionEntradaCeldasMainView(final View itemView, int position, int tipoReyclerView) {

        final Resources resources = MyApp.getContext().getResources();
        int screenWidth = resources.getDisplayMetrics().widthPixels;
        int screenHeigth = resources.getDisplayMetrics().heightPixels;

        TranslateAnimation animation = new TranslateAnimation(0, 0, -screenHeigth, 0);
        animation.setDuration(2000);
        if (tipoReyclerView == MainViewRecyclerViewAdapter.TIPO_GRID) {
            animation = new TranslateAnimation(-screenWidth, 0, 0, 0);
            animation.setDuration(2000);
        }

        animation.setInterpolator(new CustomBounceInterpolator());
        animation.setStartOffset(500 + (position * 50));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                itemView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        itemView.startAnimation(animation);

    }

    /**
     * En este metodo se definen las animaciones de borrado de las celdas
     * del recycler view de la ventana principal
     * */
    public void animacionSalidaCeldasMainView(final View itemView, int position, int tipoReyclerView) {

        final Resources resources = MyApp.getContext().getResources();
        int screenWidth = resources.getDisplayMetrics().widthPixels;
        int screenHeigth = resources.getDisplayMetrics().heightPixels;

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 1.0f);
        alphaAnimation.setDuration(200);

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -screenHeigth);
        if (tipoReyclerView == MainViewRecyclerViewAdapter.TIPO_GRID) {
            translateAnimation = new TranslateAnimation(0, -screenWidth, 0, 0);
        }

        translateAnimation.setInterpolator(new LinearInterpolator());
        translateAnimation.setDuration(200);
        translateAnimation.setStartOffset(position * 50);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(translateAnimation);

        itemView.startAnimation(set);

    }

    /**
     * En este metodo se definen las animaciones de carga de las celdas
     * del recycler view de la ventana con la lista de tasks
     * */
    public void animacionEntradaCeldasTaskList(final View viewGeneral, final View viewPanatalla,
                                               final TypewriterText textoTitulo, final String titulo, final TypewriterText textoFecha, final String fecha,
                                               final GifImageView iconoDificultad, final int dificultad, final GifImageView iconoPrioridad, final int prioridad,
                                               final int position) {

        viewPanatalla.setVisibility(View.INVISIBLE);

        viewGeneral.setRotationX(90);
        ViewAnimator
                .animate(viewGeneral)
                .rotationX(0)
                .duration(500)
                //.startDelay(position * 300)
                .interpolator(new LinearInterpolator())
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {

                        viewPanatalla.setVisibility(View.VISIBLE);
                        viewPanatalla.setScaleX(0.01f);
                        viewPanatalla.setScaleY(0.01f);

                        ViewAnimator
                                .animate(viewPanatalla)
                                .scaleX(0.01f, 1)
                                .duration(250)
                                .startDelay(150)
                                .thenAnimate(viewPanatalla)
                                .scaleY(0.01f, 1)
                                .duration(250)
                                .onStop(new AnimationListener.Stop() {
                                    @Override
                                    public void onStop() {

                                        textoTitulo.setTextAutoTyping(titulo.toUpperCase(), "_");
                                        textoTitulo.setTextSize(20);
                                        if (titulo.length() >= 30) {
                                            textoTitulo.setTextSize(15);
                                        }
                                        textoFecha.setTextAutoTyping(fecha, "_");

                                        try {

                                            String path = "";
                                            float scaleX = 1.25f;
                                            float scaleY = 1.25f;

                                            switch (dificultad) {
                                                case TaskDificultad.FACIL:
                                                    path = "gif/dificultad_facil.gif";
                                                    break;
                                                case TaskDificultad.NORMAL:
                                                    path = "gif/dificultad_normal.gif";
                                                    break;
                                                case TaskDificultad.DIFICIL:
                                                    path = "gif/dificultad_dificil.gif";
                                                    break;
                                            }

                                            GifDrawable drawable = new GifDrawable(MyApp.getContext().getAssets(), path);
                                            iconoDificultad.setBackground(drawable);
                                            iconoDificultad.setScaleX(scaleX);
                                            iconoDificultad.setScaleY(scaleY);
                                            iconoDificultad.setScaleType(ImageView.ScaleType.FIT_XY);

                                            switch (prioridad) {
                                                case TaskPrioridad.BAJA:
                                                    path = "gif/prioridad_baja_star.gif";
                                                    break;
                                                case TaskPrioridad.MEDIA:
                                                    path = "gif/prioridad_media_star.gif";
                                                    break;
                                                case TaskPrioridad.ALTA:
                                                    path = "gif/prioridad_alta_star.gif";
                                                    break;
                                            }

                                            drawable = new GifDrawable(MyApp.getContext().getAssets(), path);
                                            iconoPrioridad.setBackground(drawable);
                                            iconoPrioridad.setScaleX(scaleX);
                                            iconoPrioridad.setScaleY(scaleY);
                                            iconoPrioridad.setScaleType(ImageView.ScaleType.FIT_XY);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }



                                    }
                                })
                                .start();

                    }
                })
                .start();

    }

    /**
     * En este metodo se definen las animaciones de carga de las celdas
     * del recycler view de la ventana principal
     * */
    public void animacionCeldasScroll(View itemView, int rotation, int posicion) {

        if (itemView != null) {

            RotateAnimation rotateAnimation = new RotateAnimation(
                    itemView.getRotation(),
                    posicion == 0 ? -rotation : posicion == 1 ? 0 : rotation,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(rotation == 0 ? 1000 : 300);
            //rotateAnimation.setInterpolator(new CustomBounceInterpolator());
            rotateAnimation.setFillEnabled(true);
            rotateAnimation.setFillAfter(true);
            itemView.startAnimation(rotateAnimation);

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * animar la aparicion de la ventana de Nuevo Juego
     * */
    public void animarVentanaNuevoJuego(final NuevoJuegoFragment fragment, final View background, final View componentes, boolean mostrarVentana) {

        if (mostrarVentana) {

            background.setAlpha(0.0f);
            background.animate()
                    .alpha(1.0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            background.setAlpha(1.0f);
                        }
                    });

            componentes.setAlpha(1.0f);
            componentes.setScaleX(0.5f);
            componentes.setScaleY(0.5f);
            componentes.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(250)
                    .setInterpolator(new OvershootInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            componentes.setScaleX(1.0f);
                            componentes.setScaleY(1.0f);
                        }
                    });

        } else {

            background.animate()
                    .alpha(0.0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            background.setAlpha(0.0f);
                        }
                    });

            componentes.animate()
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .alpha(0.0f)
                    .setDuration(250)
                    .setInterpolator(new OvershootInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            componentes.setScaleX(0.5f);
                            componentes.setScaleY(0.5f);
                            componentes.setAlpha(0.0f);
                            fragment.getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                    });

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * animar la aparicion de la ventana de Nuevo Task
     * */
    public void animarVentanaNuevoTask(final NuevoTaskFragment fragment, final View background, final View componentes, boolean mostrarVentana) {

        if (mostrarVentana) {

            background.setAlpha(0.0f);
            background.animate()
                    .alpha(1.0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            background.setAlpha(1.0f);
                        }
                    });

            componentes.setAlpha(1.0f);
            componentes.setScaleX(0.5f);
            componentes.setScaleY(0.5f);
            componentes.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(250)
                    .setInterpolator(new OvershootInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            componentes.setScaleX(1.0f);
                            componentes.setScaleY(1.0f);
                        }
                    });

        } else {

            background.animate()
                    .alpha(0.0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            background.setAlpha(0.0f);
                        }
                    });

            componentes.animate()
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .alpha(0.0f)
                    .setDuration(250)
                    .setInterpolator(new OvershootInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            componentes.setScaleX(0.5f);
                            componentes.setScaleY(0.5f);
                            componentes.setAlpha(0.0f);
                            fragment.getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                    });

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * animar el giro de los elementros de recycler layout hacia
     * la opcion de borrar
     * */
    public void animarMostrarOpcionBorrar(final View view, final FrameLayout gridContenidoGeneral, final FrameLayout gridContenidoBorrar, final LinearLayout gridTextoBorrar) {

        view.setPivotX(view.getWidth() / 2.0f);
        view.setPivotY(view.getHeight() / 2.0f);

        float scale = MyApp.getContext().getResources().getDisplayMetrics().density;
        view.setCameraDistance(8000 * scale);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 0, 90);
        animator.setDuration(150);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                gridContenidoGeneral.setVisibility(View.INVISIBLE);
                gridContenidoBorrar.setVisibility(View.VISIBLE);

                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 90, 180);
                animator.setDuration(150);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        String titulo = "BORRAR";

                        int elemento = 0;
                        int duracion = 50;
                        int delay = 150;
                        int indexInicial = 0;

                        gridTextoBorrar.setRotationY(180f);

                        for (char caracter : titulo.toCharArray()) {

                            CustomFontText textView = new CustomFontText(MyApp.getContext());
                            textView.setTypeface(FontManager.getInstance().getFont("fonts/PixelRunes.ttf"));
                            textView.setText(" " + String.valueOf(caracter));
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, MyApp.getInstance().isSmallScreen() ? 16 : 18);
                            textView.setTextColor(Color.RED);
                            textView.setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                            gridTextoBorrar.addView(textView, layoutParams);

                            AnimatorManager.getInstance().animarTextoRunas(textView, indexInicial, duracion, delay * elemento);
                            elemento++;

                        }

                    }
                });
                animator.start();
            }
        });
        animator.start();

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * animar el giro de los elementros de recycler layout hacia
     * la opcion de borrar
     * */
    public void animarMostrarOpcionBorrarListaTask(final View view, final FrameLayout gridContenidoGeneral, final FrameLayout gridContenidoBorrar, final TextView gridTextoBorrar) {

        view.setPivotX(view.getWidth() / 2.0f);
        view.setPivotY(view.getHeight() / 2.0f);

        float scale = MyApp.getContext().getResources().getDisplayMetrics().density;
        view.setCameraDistance(8000 * scale);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 0, 90);
        animator.setDuration(150);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                gridContenidoGeneral.setVisibility(View.INVISIBLE);
                gridContenidoBorrar.setVisibility(View.VISIBLE);

                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 90, 180);
                animator.setDuration(150);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        gridTextoBorrar.setRotationY(180f);
                    }
                });
                animator.start();
            }
        });
        animator.start();

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * animar el giro de los elementros de recycler layout de veulta
     * de la opcion de borrar
     * */
    public void animarOcultarOpcionBorrar(final View view, final FrameLayout gridContenidoGeneral, final FrameLayout gridContenidoBorrar, final LinearLayout gridTextoBorrar) {

        view.setPivotX(view.getWidth() / 2.0f);
        view.setPivotY(view.getHeight() / 2.0f);

        float scale = MyApp.getContext().getResources().getDisplayMetrics().density;
        view.setCameraDistance(8000 * scale);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 180, 90);
        animator.setDuration(150);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                gridContenidoGeneral.setVisibility(View.VISIBLE);
                gridContenidoBorrar.setVisibility(View.INVISIBLE);

                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 90, 0);
                animator.setDuration(150);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
            }
        });
        animator.start();

        gridTextoBorrar.setRotationY(-180f);
        gridTextoBorrar.removeAllViews();

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * animar el giro de los elementros de recycler layout de veulta
     * de la opcion de borrar
     * */
    public void animarOcultarOpcionBorrarListaTask(final View view, final FrameLayout gridContenidoGeneral, final FrameLayout gridContenidoBorrar, final TextView gridTextoBorrar) {

        view.setPivotX(view.getWidth() / 2.0f);
        view.setPivotY(view.getHeight() / 2.0f);

        float scale = MyApp.getContext().getResources().getDisplayMetrics().density;
        view.setCameraDistance(8000 * scale);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 180, 90);
        animator.setDuration(150);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                gridContenidoGeneral.setVisibility(View.VISIBLE);
                gridContenidoBorrar.setVisibility(View.INVISIBLE);

                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 90, 0);
                animator.setDuration(150);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
            }
        });
        animator.start();

        gridTextoBorrar.setRotationY(-180f);

    }

    /**
     * Esta clase representa un interpolador customizado
     * */
    public class CustomBounceInterpolator implements Interpolator {
        @Override public float getInterpolation(float ratio) {
            if (ratio == 0.0f || ratio == 1.0f)
                return ratio;
            else {
                float p = 0.9f;
                float two_pi = (float) (Math.PI * 2.7f);
                return (float) Math.pow(2.0f, -10.0f * ratio) * (float) Math.sin((ratio - (p/5.0f)) * two_pi/p) + 1.0f;
            }
        }
    }

}
