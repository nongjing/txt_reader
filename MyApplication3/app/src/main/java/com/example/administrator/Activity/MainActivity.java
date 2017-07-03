package com.example.administrator.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import Books.Book;
import Books.Book_adapter;
import Page.SPHelper;
import file.File_Data;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Book_adapter book_adapter;
    Integer REQUEST_READ =1;
    Integer REQUEST_FILE=0;
    private static boolean toorbar_meau_state=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File_Data file_data=new File_Data(MainActivity.this,this);
        file_data.getauthority();
        recyclerView=(RecyclerView)findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        book_adapter=new Book_adapter(MainActivity.this);
        recyclerView.setAdapter(book_adapter);
        toolbar=(Toolbar)findViewById(R.id.main_toolbar);
        toolbar.setTitle("我的书架");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.file);
        toolbar.setOnMenuItemClickListener(onMeauItemClick);
        book_adapter.setRecyclerViewOnItemClickListener(new Book_adapter.RecyclerViewOnItemClickListener(){

            @Override
            public void onItemClickListener(View v, int position) {
               if(book_adapter.getissShowbox()==true){
                   book_adapter.setSelectItem(position);
               }
               if(book_adapter.getissShowbox()==false){
                   Intent intent = new Intent(MainActivity.this,ReadActivity.class);
                   Book book=book_adapter.books.get(position);
                   intent.putExtra("book",book);
                   startActivityForResult(intent, REQUEST_READ);
               }

            }

            @Override
            public boolean onItemLongClickListener(View v, int position) {
                book_adapter.setShowBox();
                book_adapter.setSelectItem(position);
                book_adapter.notifyDataSetChanged();
                toorbar_meau_state=!toorbar_meau_state;
                toolbar.getMenu().findItem(R.id.main_meau_yes_delete).setVisible(toorbar_meau_state);
                toolbar.getMenu().findItem(R.id.main_meau_no_delete).setVisible(toorbar_meau_state);
                return  true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:
                book_adapter.notifyDataSetChanged();
                break;
        }
    }

    private Toolbar.OnMenuItemClickListener onMeauItemClick=new Toolbar.OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.main_menu_add:
                    toorbar_meau_state=false;
                    toolbar.getMenu().findItem(R.id.main_meau_yes_delete).setVisible(toorbar_meau_state);
                    toolbar.getMenu().findItem(R.id.main_meau_no_delete).setVisible(toorbar_meau_state);;
                    Intent a=new Intent(MainActivity.this,FilesActivity.class);
                    startActivity(a);
                    finish();
                    break;
                case R.id.main_menu_management:
                    book_adapter.setShowBox();
                    book_adapter.notifyDataSetChanged();
                    toorbar_meau_state=!toorbar_meau_state;
                    toolbar.getMenu().findItem(R.id.main_meau_yes_delete).setVisible(toorbar_meau_state);
                    toolbar.getMenu().findItem(R.id.main_meau_no_delete).setVisible(toorbar_meau_state);
                    break;
                case R.id.main_menu_delete_all:
                    toorbar_meau_state=false;
                    toolbar.getMenu().findItem(R.id.main_meau_yes_delete).setVisible(toorbar_meau_state);
                    toolbar.getMenu().findItem(R.id.main_meau_no_delete).setVisible(toorbar_meau_state);
                    new AlertDialog.Builder(MainActivity.this).setTitle("删除确认框").setMessage("是否确定删除全部书籍？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            book_adapter.removeAll();
                            book_adapter.notifyDataSetChanged();
                        }
                    }).setNegativeButton("取消",null).show();

                    break;
                case R.id.main_meau_yes_delete:
                    book_adapter.remove();
                    book_adapter.notifyDataSetChanged();
                    break;
                case R.id.main_meau_no_delete:
                    book_adapter.setShowBox();
                    book_adapter.notifyDataSetChanged();
                    toorbar_meau_state=!toorbar_meau_state;
                    toolbar.getMenu().findItem(R.id.main_meau_yes_delete).setVisible(toorbar_meau_state);
                    toolbar.getMenu().findItem(R.id.main_meau_no_delete).setVisible(toorbar_meau_state);
                    break;
            }
            return true;
        }
    };
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_meau,menu);
        toolbar.getMenu().findItem(R.id.main_meau_yes_delete).setVisible(toorbar_meau_state);
        toolbar.getMenu().findItem(R.id.main_meau_no_delete).setVisible(toorbar_meau_state);
        return true;
    }

}
