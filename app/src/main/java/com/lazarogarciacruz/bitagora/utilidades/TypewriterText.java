package com.lazarogarciacruz.bitagora.utilidades;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.support.v7.widget.AppCompatTextView;

import com.lazarogarciacruz.bitagora.R;

import java.util.Random;

/**
 * Created by lazarogarciacruz on 13/6/17.
 */

public class TypewriterText extends AppCompatTextView {

    public static int PRECISSION_LOW = 8;
    public static int PRECISSION_MED = 9;
    public static int PRECISSION_HIGH = 11;

    private int decryptionSpeed = 10;
    private int encryptionSpeed = 10;
    private int typingSpeed =100;
    private int precision = 5;
    private String animateEncryption = "";
    private String animateDecryption = "";
    private String animateTextTyping = "";
    private String animateTextTypingWithMistakes = "";

    private Handler handler;
    private int counter=0;
    private boolean misstakeFound = false;
    private boolean executed = false;
    private Random ran = new Random();
    public String misstakeValues = "qwertyuiop[]asdfghjkl;zxcvbnm,./!@#$%^&*()_+1234567890";
    private String encryptedText;
    private int countLetter=0;
    private int cocatation=0;

    public TypewriterText(Context context) {
        super(context);
    }

    public TypewriterText(Context context, AttributeSet attrs) {

        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AutoTypeTextView);

        try {
            animateDecryption = ta.getString(R.styleable.AutoTypeTextView_animateDecryption);
            animateEncryption = ta.getString(R.styleable.AutoTypeTextView_animateEncryption);
            animateTextTyping = ta.getString(R.styleable.AutoTypeTextView_animateTextTypeWithoutMistakes);
            animateTextTypingWithMistakes = ta.getString(R.styleable.AutoTypeTextView_animateTextTypeWithMistakes);
            typingSpeed = ta.getInt(R.styleable.AutoTypeTextView_typingSpeed, 100);
            decryptionSpeed = ta.getInt(R.styleable.AutoTypeTextView_decryptionSpeed, 20);
            encryptionSpeed = ta.getInt(R.styleable.AutoTypeTextView_encryptionSpeed , 20);
            precision = ta.getInt(R.styleable.AutoTypeTextView_typingPrecision, precision);
        } catch(Exception e) {
        }

        setupAttributes();
        ta.recycle();

    }

    public TypewriterText(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AutoTypeTextView);

        try {
            animateDecryption = ta.getString(R.styleable.AutoTypeTextView_animateDecryption);
            animateEncryption = ta.getString(R.styleable.AutoTypeTextView_animateEncryption);
            animateTextTyping = ta.getString(R.styleable.AutoTypeTextView_animateTextTypeWithoutMistakes);
            animateTextTypingWithMistakes = ta.getString(R.styleable.AutoTypeTextView_animateTextTypeWithMistakes);
            typingSpeed = ta.getInt(R.styleable.AutoTypeTextView_typingSpeed, 100);
            decryptionSpeed = ta.getInt(R.styleable.AutoTypeTextView_decryptionSpeed, 20);
            encryptionSpeed = ta.getInt(R.styleable.AutoTypeTextView_encryptionSpeed , 20);
            precision = ta.getInt(R.styleable.AutoTypeTextView_typingPrecision, precision);
        } catch(Exception e) {
        }

        setupAttributes();
        ta.recycle();

    }

    private void setupAttributes() {

        if(animateTextTyping!=null)
            setTextAutoTyping(animateTextTyping);

        if(animateTextTypingWithMistakes!=null) {
            if (precision < 6)
                precision = 6;
            setTextAutoTypingWithMistakes(animateTextTypingWithMistakes, precision);
        }

        if(animateDecryption!=null)
            animateDecryption(animateDecryption);

        if(animateEncryption!=null)
            animateEncryption(animateEncryption);

    }

    public void setTextAutoTyping(final String text) {

        if(!executed) {

            executed = true;
            counter = 0;
            handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    setText(text.substring(0, counter));
                    counter++;
                    if (text.length() >= counter) {
                        postDelayed(this, getTypingSpeed());
                    } else {
                        executed = false;
                    }
                }

            }, getTypingSpeed());

        }

    }

    public void setTextAutoTyping(final String text, final String special) {

        if(!executed) {

            executed = true;
            counter = 0;
            handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    setText(text.substring(0, counter) + (counter > 0 ? special : ""));
                    counter++;
                    if (text.length() >= counter) {
                        postDelayed(this, getTypingSpeed());
                    } else {
                        executed = false;
                        setText(text);
                    }
                }

            }, getTypingSpeed());

        }

    }

    public void setTextAutoTypingWithMistakes(final String text, final int precission) {

        if(!executed) {

            executed = true;
            counter = 0;
            handler = new Handler();
            ran = new Random();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    int num = ran.nextInt(10) + 1;
                    if (num > precission && counter > 1 && !misstakeFound) {
                        setText(chooseTypeOfMistake(text, counter));
                        counter--;
                    } else {
                        counter++;
                        setText(text.substring(0, counter));
                        misstakeFound = false;
                    }
                    if (text.length() > counter) {
                        postDelayed(this, getTypingSpeed());
                    } else {
                        executed = false;
                    }
                }

            }, getTypingSpeed());

        }

    }

    public void animateDecryption(final String text) {

        encryptedText = text;
        ran = new Random();
        handler = new Handler();
        cocatation = ran.nextInt(10);
        counter = 0;
        countLetter = 0;

        if(!executed) {

            executed = true;
            for(int i=0; i<text.length(); i++) {
                encryptedText = replaceCharAt(encryptedText, i, misstakeValues.charAt(ran.nextInt(misstakeValues.length())));
                setText(encryptedText);
            }
            handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if(counter <= cocatation) {
                        encryptedText = replaceCharAt(encryptedText,countLetter,misstakeValues.charAt(ran.nextInt(misstakeValues.length())));
                        setText(encryptedText);
                        counter++;
                    } else {
                        encryptedText = replaceCharAt(encryptedText, countLetter, text.charAt(countLetter));
                        setText(encryptedText);
                        countLetter++;
                        cocatation = ran.nextInt(10);
                        counter = 0;
                    }
                    if(text.length() > countLetter) {
                        postDelayed(this, getDecryptionSpeed());
                    } else {
                        executed = false;
                    }

                }

            }, getDecryptionSpeed());

        }

    }

    public void animateEncryption(final String text) {

        encryptedText = text;
        ran = new Random();
        handler = new Handler();
        cocatation = ran.nextInt(10);
        counter = 0;
        countLetter = 0;

        if(!executed) {

            executed = true;
            handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if(counter <= cocatation) {
                        encryptedText = replaceCharAt(encryptedText,countLetter,misstakeValues.charAt(ran.nextInt(misstakeValues.length())));
                        setText(encryptedText);
                        counter++;
                    } else {
                        countLetter++;
                        cocatation = ran.nextInt(10);
                        counter = 0;
                    }
                    if(text.length() > countLetter) {
                        postDelayed(this, getDecryptionSpeed());
                    } else {
                        executed = false;
                    }

                }

            }, getDecryptionSpeed());

        }

    }

    private String chooseTypeOfMistake(String text, int counter) {

        int misstake = ran.nextInt(3)+1;
        String result = text.substring(0,counter);

        switch(misstake) {
            case 1 :
                result = text.substring(0,counter-1) + randomChar();
                break;
            case 2 :
                switch (ran.nextInt(2)+1) {
                    case 1:
                        result = text.substring(0, counter - 1) + String.valueOf(text.charAt(counter)).toLowerCase();
                        break;
                    case 2:
                        result = text.substring(0, counter-1) + String.valueOf(text.charAt(counter)).toUpperCase();
                        break;
                }
                break;
            case 3 :
                result = text.substring(0, counter-1);
                break;
        }

        misstakeFound = true;
        return result;

    }

    private char randomChar() {
        return misstakeValues.charAt(ran.nextInt(misstakeValues.length()));
    }

    public static String replaceCharAt(String text, int pos, char c) {
        return text.substring(0, pos) + c + text.substring(pos + 1);
    }

    public int getTypingSpeed() {
        return typingSpeed;
    }

    public void setTypingSpeed(int typingSpeed) {
        this.typingSpeed = typingSpeed;
    }

    public int getDecryptionSpeed() {
        return decryptionSpeed;
    }

    public void setDecryptionSpeed(int decryptionSpeed) {
        this.decryptionSpeed = decryptionSpeed;
    }

    public int getEncryptionSpeed() {
        return encryptionSpeed;
    }

    public void setEncryptionSpeed(int encryptionSpeed) {
        this.encryptionSpeed = encryptionSpeed;
    }

    public boolean isRunning() {
        return executed;
    }

    /*private static final String TAG = TypewriterText.class.getSimpleName();
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 500; // Default 500ms character delay
    Handler animationCompleteCallBack;

    public TypewriterText(Context context) {
        super(context);
    }

    public TypewriterText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {

        @Override
        public void run() {

            String caracterExtra = "";

            if (mIndex > 0 ) caracterExtra = "_";

            setText(mText.subSequence(0, mIndex++) + caracterExtra);

            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            } else {
                setText(mText);
                mIndex = 0;
                mHandler.postDelayed(characterAdder, 5000);
            }

        }

    };

    public void setAnimationCompleteListener(Handler animationCompleteCallBack) {
        this.animationCompleteCallBack = animationCompleteCallBack;
    }

    public void animateText(CharSequence text) {

        mText = text;
        mIndex = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);

    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }*/

}
