package com.example.zuoye.utils;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.example.zuoye.R;

import java.text.BreakIterator;

public class KeyBoardUtils {

    private final Keyboard k1;
    private KeyboardView keyboardView;
    private EditText editText;



   //接口回调
    public interface OnEnsureListener{
        public void onEnsure();
    }
    OnEnsureListener onEnsureListener;
    public void setOnEnsureListener(OnEnsureListener onEnsureListener){
        this.onEnsureListener = onEnsureListener;
    }



    public KeyBoardUtils(KeyboardView keyboardView, EditText editText) {
        this.keyboardView = keyboardView;
        this.editText = editText;
        this.editText.setInputType(InputType.TYPE_NULL);//取消弹出系统键盘
        k1 = new Keyboard(this.editText.getContext(), R.xml.key);
        this.keyboardView.setKeyboard(k1);//设置要显示键盘的样式
        this.keyboardView.setEnabled(true);
        this.keyboardView.setPreviewEnabled(true);
        this.keyboardView.setOnKeyboardActionListener(listener);//设置键盘按钮被点击了的监听
    }



    KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {}
        @Override
        public void onRelease(int primaryCode) {}
        @Override
        //主要写onkey
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = editText.getText();
            int start = editText.getSelectionStart();
            switch (primaryCode){
                case Keyboard.KEYCODE_DELETE://点击了删除键
                    if(editable!=null && editable.length()>0){
                        if(start>0){
                            editable.delete(start-1,start);
                        }
                    }
                    break;
                case Keyboard.KEYCODE_CANCEL://点击了清零
                    editable.clear();
                    break;
                case Keyboard.KEYCODE_DONE://点击了完成
                    onEnsureListener.onEnsure();//接口回调方法，当点击确定，可调用此方法
                    break;
                default:
                    editable.insert(start,Character.toString((char)primaryCode));
                    break;
            }
        }
        @Override
        public void onText(CharSequence text) {
        }
        @Override
        public void swipeLeft() {}
        @Override
        public void swipeRight() {}
        @Override
        public void swipeDown() {}
        @Override
        public void swipeUp() {}
    };



    //显示键盘
    public void showKeyboard(){
        int visibility = keyboardView.getVisibility();
        if(visibility == View.INVISIBLE ||visibility == View.GONE){
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

}


