package com.fur.furnote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class EditActivity extends AppCompatActivity {

    private EditText editText;
    private String originalNote;  // 保存原始笔记内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //导航栏沉浸
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.setNavigationBarColor(Color.TRANSPARENT);

        editText = findViewById(R.id.editText);

        // 获取从主界面传递过来的笔记内容
        originalNote = getIntent().getStringExtra("noteContent");
        editText.setText(originalNote);
    }

    // 点击保存按钮
    public void saveNote(View view) {
        String editedNote = editText.getText().toString();

        // 将编辑的笔记保存到文件
        saveNoteToFile(editedNote);

        // 将编辑的笔记返回给主界面
        Intent resultIntent = new Intent();
        resultIntent.putExtra("editedNote", editedNote);
        setResult(RESULT_OK, resultIntent);

        // 关闭编辑页面
        finish();
    }

    private void saveNoteToFile(String note) {
        try {
            FileOutputStream fos = openFileOutput("notes.txt", Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(note + "\n");
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 点击返回按钮
    public void goBack(View view) {
        // 不保存当前编辑的内容，直接返回
        setResult(RESULT_CANCELED);
        finish();
    }
}