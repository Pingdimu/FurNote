package com.fur.furnote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> notesList;
    private ArrayAdapter<String> notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //导航栏沉浸
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.setNavigationBarColor(Color.TRANSPARENT);

        // 初始化笔记列表和适配器
        notesList = new ArrayList<>();
        notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesList);

        // 设置RecyclerView
        ListView notesListView = findViewById(R.id.notesListView);
        notesListView.setAdapter(notesAdapter);

        // 点击笔记项进入编辑页面
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedNote = notesList.get(position);
                openEditActivity(selectedNote);
            }
        });

        // 长按笔记项弹出删除对话框
        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });

        // 加载保存的笔记
        loadNotes();
    }

    private void loadNotes() {
        try {
            FileInputStream fis = openFileInput("notes.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                notesList.add(line);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 点击新建笔记按钮
    public void createNewNote(View view) {
        openEditActivity("");
    }

    // 打开编辑页面
    private void openEditActivity(String noteContent) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("noteContent", noteContent);
        startActivityForResult(intent, 1);
    }

    // 接收编辑页面返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String editedNote = data.getStringExtra("editedNote");

            // 更新笔记列表
            if (!editedNote.isEmpty()) {
                notesList.add(editedNote);
                notesAdapter.notifyDataSetChanged();
            }
        }
    }

    // 显示删除对话框
    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure you want to delete this note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    // 删除笔记
    private void deleteNote(int position) {
        String deletedNote = notesList.get(position);

        // 从笔记列表和适配器中删除
        notesList.remove(position);
        notesAdapter.notifyDataSetChanged();

        // 更新文件中的笔记
        updateFileAfterDeletion(deletedNote);
    }

    // 从文件中删除笔记
    private void updateFileAfterDeletion(String deletedNote) {
        try {
            FileInputStream fis = openFileInput("notes.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder fileContents = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.equals(deletedNote)) {
                    fileContents.append(line).append("\n");
                }
            }

            br.close();

            // 重新写入文件
            FileOutputStream fos = openFileOutput("notes.txt", Context.MODE_PRIVATE);
            fos.write(fileContents.toString().getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}