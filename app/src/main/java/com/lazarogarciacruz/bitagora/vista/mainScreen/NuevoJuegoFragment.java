package com.lazarogarciacruz.bitagora.vista.mainScreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.github.florent37.viewanimator.ViewAnimator;
import com.lazarogarciacruz.bitagora.R;
import com.lazarogarciacruz.bitagora.utilidades.AnimatorManager;
import com.lazarogarciacruz.bitagora.utilidades.DataMaganer;
import com.lazarogarciacruz.bitagora.utilidades.FontManager;
import com.lazarogarciacruz.bitagora.utilidades.MyApp;
import java.io.File;
import java.util.List;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.droidsonroids.gif.GifImageView;

import static android.R.attr.bitmap;
import static android.R.attr.fragment;
import static com.lazarogarciacruz.bitagora.R.id.imageView;

/**
 * Created by lazarogarciacruz on 9/6/17.
 */

public class NuevoJuegoFragment extends Fragment {

    private File nuevoJuegoImagenCover;
    private Bitmap nuevoJuegoBitmapCover;
    private File nuevoJuegoImagenLogo;
    private Bitmap nuevoJuegoBitmapLogo;
    private String nuevoJuegoTitulo;
    private boolean nuevoJuegoIsCoverSeleccionado = false;
    private byte[] coverGifData;
    private boolean isCoverGif = false;

    private View view;
    private Button botonCrear;
    private Button botonCover;
    private Button botonLogo;
    private TextView textoTitulo;
    private GifImageView coverUp;
    private GifImageView coverDown;
    private ImageView logoUp;
    private ImageView logoDown;
    private TextView tituloUp;
    private TextView tituloDown;

    private MainActivity mainContext;

    public static NuevoJuegoFragment newInstance() {
        return new NuevoJuegoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.nuevo_juego_fragment, container, false);

        nuevoJuegoImagenLogo = null;
        nuevoJuegoImagenCover = null;
        nuevoJuegoBitmapCover = null;
        nuevoJuegoBitmapLogo = null;
        nuevoJuegoTitulo = "";
        nuevoJuegoIsCoverSeleccionado = false;

        coverUp = (GifImageView) view.findViewById(R.id.mainViewNuevoUpCover);
        coverUp.setImageBitmap(null);
        coverDown = (GifImageView) view.findViewById(R.id.mainViewNuevoDownCover);
        coverDown.setImageBitmap(null);
        logoUp = (ImageView) view.findViewById(R.id.mainViewNuevoUpLogo);
        logoUp.setImageBitmap(null);
        logoDown = (ImageView) view.findViewById(R.id.mainViewNuevoDownLogo);
        logoDown.setImageBitmap(null);
        tituloUp = (TextView) view.findViewById(R.id.mainViewNuevoUpTexto);
        tituloUp.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));
        tituloUp.setText("");
        tituloDown = (TextView) view.findViewById(R.id.mainViewNuevoDownTexto);
        tituloDown.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));
        tituloDown.setText("");
        textoTitulo = (TextView) view.findViewById(R.id.mainViewTextoTitulo);
        textoTitulo.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));
        textoTitulo.setText("");

        textoTitulo.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // or other action, which you are using
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    botonCrear.setEnabled(false);
                    if (textoTitulo.getText().length() > 0) {
                        botonCrear.setEnabled(true);
                        nuevoJuegoTitulo = textoTitulo.getText().toString();
                        actualizarVentanaNuevoJuego(true);
                    } else {
                        nuevoJuegoTitulo = textoTitulo.getText().toString();
                        actualizarVentanaNuevoJuego(true);
                    }
                    if (nuevoJuegoBitmapCover != null || nuevoJuegoBitmapLogo != null || coverGifData != null) {
                        botonCrear.setEnabled(true);
                    }
                    InputMethodManager imm = (InputMethodManager) MyApp.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    return true;
                }
                return false;
            }

        });

        /*textoTitulo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    botonCrear.setEnabled(false);
                    if (textoTitulo.getText().length() > 0) {
                        botonCrear.setEnabled(true);
                        nuevoJuegoTitulo = textoTitulo.getText().toString();
                        actualizarVentanaNuevoJuego(true);
                    } else {
                        nuevoJuegoTitulo = textoTitulo.getText().toString();
                        actualizarVentanaNuevoJuego(true);
                    }
                    if (nuevoJuegoBitmapCover != null || nuevoJuegoBitmapLogo != null || coverGifData != null) {
                        botonCrear.setEnabled(true);
                    }
                }
            }
        });*/

        Button botonCerrar = (Button) view.findViewById(R.id.mainViewBotonCerrar);
        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarVentanaNuevoJuego();
            }
        });

        botonCover = (Button) view.findViewById(R.id.mainViewBotonCover);
        botonCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevoJuegoIsCoverSeleccionado = true;
                mostrarSelectorImagenes();
            }
        });
        botonCover.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));

        botonLogo = (Button) view.findViewById(R.id.mainViewBotonLogo);
        botonLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevoJuegoIsCoverSeleccionado = false;
                mostrarSelectorImagenes();
            }
        });
        botonLogo.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));

        botonCrear = (Button) view.findViewById(R.id.mainViewBotonCrear);
        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearNuevoJuego();
            }
        });
        botonCrear.setTypeface(FontManager.getInstance().getFont("fonts/PixelArt.ttf"));
        botonCrear.setEnabled(false);

        //Con esta accion controlamos que el keyboard se oculte cuando
        //se presiona la tecla ENTER del mismo
            /*textoTitulo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    botonCrear.setEnabled(false);
                    if (textoTitulo.getText().length() > 0) {
                        botonCrear.setEnabled(true);
                    }
                    return false;
                }
            });*/

        View background = view.findViewById(R.id.mainViewBlackBackground);
        View componentes = view.findViewById(R.id.mainViewNuevoJuegoComponentes);

        //Determinamos la altura del panel de los componentes en funcion del tamaño de la pantalla
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) componentes.getLayoutParams();
        params.height = Math.round(MyApp.getInstance().getHeightPixels() * (MyApp.getInstance().isSmallScreen() ? 0.75f : 0.7f));
        params.width = Math.round(MyApp.getInstance().getWidthPixels() * (MyApp.getInstance().isSmallScreen() ? 0.75f : 0.68f));

        AnimatorManager.getInstance().animarVentanaNuevoJuego(this, background, componentes, true);

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

        mainContext = (MainActivity) context;
        /*final View activityRootView = mainContext.findViewById(R.id.mainViewFrameRoot);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                float dtToPx = dpToPx(mainContext, 200);
                if (heightDiff < dtToPx) { // if more than 200 dp, it's probably a keyboard...
                    //No hay teclado
                    botonCrear.setEnabled(false);
                    if (textoTitulo.getText().length() > 0) {
                        botonCrear.setEnabled(true);
                        nuevoJuegoTitulo = textoTitulo.getText().toString();
                        actualizarVentanaNuevoJuego(true);
                    } else {
                        nuevoJuegoTitulo = textoTitulo.getText().toString();
                        actualizarVentanaNuevoJuego(true);
                    }
                    if (nuevoJuegoBitmapCover != null || nuevoJuegoBitmapLogo != null || coverGifData != null) {
                        botonCrear.setEnabled(true);
                    }
                }
            }

        });*/

        /*public void onGlobalLayout() {
                Rect measureRect = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(measureRect);
                int keypadHeight = activityRootView.getRootView().getHeight() - measureRect.bottom;

                if (keypadHeight > 0) {
                    // keyboard is opened
                } else {
                    // keyboard is closed
                }
            }*/

    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    //-----------------------------------------------------------------
    //-----------------------------------------------------------------
    //----------- FUNCIONES SOBRE LA SELECCION DE IMAGENES ------------
    //-----------------------------------------------------------------
    //-----------------------------------------------------------------

    /**
     * Este metodo lleva a cabo las tareas necesarias para mostar el
     * selector de imagenes par la creacion de un nuevo juego
     * */
    private void mostrarSelectorImagenes() {

        if (ContextCompat.checkSelfPermission(mainContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openDocuments(this, 0);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, mainContext, new DefaultCallback() {

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                if (imageFiles.size() > 0) {
                    if (nuevoJuegoIsCoverSeleccionado) {
                        nuevoJuegoImagenCover = imageFiles.get(0);
                    } else {
                        nuevoJuegoImagenLogo = imageFiles.get(0);
                    }
                    actualizarVentanaNuevoJuego(false);
                    botonCrear.setEnabled(true);
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(mainContext);
                    if (photoFile != null) photoFile.delete();
                }
            }

        });

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
        AnimatorManager.getInstance().animarVentanaNuevoJuego(this, background, componentes, false);

        View view = mainContext.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)mainContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * actualizar las imagenes de las miniaturas de la ventana
     * para la creacion de un nuevo juego
     * */
    private void actualizarVentanaNuevoJuego(boolean isActualizarTitulo) {

        if (!isActualizarTitulo) {

            if (nuevoJuegoImagenCover != null) {

                if (nuevoJuegoImagenCover.getAbsolutePath().endsWith(".gif")) {

                    try {

                        pl.droidsonroids.gif.GifDrawable drawable = new pl.droidsonroids.gif.GifDrawable(this.getResources(), R.drawable.loader);

                        Glide.with(this)
                                .load(nuevoJuegoImagenCover.getAbsolutePath())
                                .asGif()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                //.placeholder(drawable)
                                //.override(coverUp.getWidth(), coverUp.getHeight() / 2)
                                //.centerCrop()
                                .listener(new RequestListener<String, GifDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        coverGifData = resource.getData();
                                        botonCrear.setEnabled(true);
                                        return false;
                                    }
                                })
                                .into(coverUp);

                        Glide.with(this)
                                .load(nuevoJuegoImagenCover.getAbsolutePath())
                                .asGif()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                //.placeholder(drawable)
                                //.override(coverDown.getWidth(), coverDown.getHeight())
                                //.centerCrop()
                                .into(coverDown);

                    } catch (Exception e) {
                    }

                    isCoverGif = true;

                } else {

                    /*Glide.with(this)
                            .load(nuevoJuegoImagenCover.getAbsolutePath())
                            .asBitmap()
                            .override(coverUp.getWidth(), coverUp.getHeight() / 2)
                            .centerCrop()
                            .into(new BitmapImageViewTarget(coverUp) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    super.onResourceReady(resource, glideAnimation);
                                    nuevoJuegoBitmapCover = resource;
                                }
                            });
                    //.into(coverUp);

                    Glide.with(this)
                            .load(nuevoJuegoImagenCover.getAbsolutePath())
                            .asBitmap()
                            .override(coverDown.getWidth(), coverDown.getHeight())
                            .centerCrop()
                            .into(coverDown);*/

                    Bitmap bitmap = BitmapFactory.decodeFile(nuevoJuegoImagenCover.getAbsolutePath());

                    if (bitmap.getHeight() > bitmap.getWidth()) {
                        int yPos = (bitmap.getHeight() - bitmap.getWidth()) / 2;
                        nuevoJuegoBitmapCover = Bitmap.createBitmap(bitmap, 0, yPos, bitmap.getWidth(), bitmap.getWidth() - (bitmap.getWidth() / 5));
                    } else {
                        nuevoJuegoBitmapCover = bitmap;
                    }

                    if (bitmap != null) {
                        coverUp.setImageBitmap(nuevoJuegoBitmapCover);
                        coverDown.setImageBitmap(nuevoJuegoBitmapCover);
                    }

                }

            }

            if (nuevoJuegoImagenLogo != null) {

                /*Glide.with(this)
                        .load(nuevoJuegoImagenLogo.getAbsolutePath())
                        .asBitmap()
                        .override(logoUp.getWidth()/2, logoUp.getHeight()/2)
                        .fitCenter()
                        .into(new BitmapImageViewTarget(logoUp) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                super.onResourceReady(resource, glideAnimation);
                                nuevoJuegoBitmapLogo = resource;
                            }
                        });
                        //.into(logoUp);

                Glide.with(this)
                        .load(nuevoJuegoImagenLogo.getAbsolutePath())
                        .asBitmap()
                        .override(logoDown.getWidth()/2, logoDown.getHeight()/2)
                        .fitCenter()
                        .into(logoDown);*/

                Bitmap bitmap = BitmapFactory.decodeFile(nuevoJuegoImagenLogo.getAbsolutePath());

                /*if (bitmap.getHeight() > bitmap.getWidth()) {
                    int yPos = (bitmap.getHeight() - bitmap.getWidth()) / 2;
                    nuevoJuegoBitmapLogo = Bitmap.createBitmap(bitmap, 0, yPos, bitmap.getWidth(), bitmap.getWidth() - (bitmap.getWidth() / 5));
                } else {

                    int height = bitmap.getHeight();
                    int width = bitmap.getWidth();
                    float scaleWidth = ((float) width * 0.75f) / width;
                    float scaleHeight = ((float) height * 0.75f) / height;
                    // here we do resize the bitmap
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    // and create new one
                    nuevoJuegoBitmapLogo = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
                }*/

                nuevoJuegoBitmapLogo = bitmap;

                if (bitmap != null) {
                    logoUp.setImageBitmap(nuevoJuegoBitmapLogo);
                    logoDown.setImageBitmap(nuevoJuegoBitmapLogo);
                }

            }

            actualizarVentanaNuevoJuego(true);

        } else {

            tituloUp.setText(nuevoJuegoTitulo);
            tituloDown.setText(nuevoJuegoTitulo);
            tituloUp.setVisibility(View.VISIBLE);
            tituloDown.setVisibility(View.VISIBLE);

            if (nuevoJuegoImagenLogo != null) {
                tituloUp.setVisibility(View.INVISIBLE);
                tituloDown.setVisibility(View.INVISIBLE);
            } else if (nuevoJuegoImagenCover == null) {
                tituloUp.setTextColor(Color.BLACK);
                tituloUp.setShadowLayer(1, 5, 5, Color.WHITE);
                tituloDown.setTextColor(Color.BLACK);
                tituloDown.setShadowLayer(1, 5, 5, Color.WHITE);
            } else {
                tituloUp.setTextColor(Color.WHITE);
                tituloUp.setShadowLayer(1, 5, 5, Color.BLACK);
                tituloDown.setTextColor(Color.WHITE);
                tituloDown.setShadowLayer(1, 5, 5, Color.BLACK);
            }

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * almacenar la informacion de un nuevo juego
     * */
    private void crearNuevoJuego() {
        DataMaganer.getInstance().almacenarNuevoJuego(textoTitulo.getText().toString(), nuevoJuegoBitmapCover, nuevoJuegoBitmapLogo, isCoverGif, coverGifData);
        this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
