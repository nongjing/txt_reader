package com.example.administrator.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

import Bookmarks.Bookmark;
import Bookmarks.Bookmark_adapter;
import Books.Book;
import Chapters.Chapter;
import Chapters.ChapterFactory;
import Chapters.Chapter_adapter;
import Page.PageFactory;
import Page.SPHelper;

public class ChapterActivity extends AppCompatActivity implements ChapterFactory.LoadCallback {
    private RecyclerView recyclerView;
    private Chapter_adapter chapter_adapter;
    private Bookmark_adapter bookmark_adapter;
    private ChapterFactory chapterFactory;
    private ProgressDialog progressDialog;
    private Integer RESULT_CHAPTER=2;
    private Integer RESULT_BOOKMARK=3;
    private SwitchCompat switchCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        recyclerView=(RecyclerView)findViewById(R.id.Chapter_recyclerview);
        chapter_adapter=new Chapter_adapter(this);
        Book book=PageFactory.getInstance().getBook();
        bookmark_adapter=new Bookmark_adapter(ChapterActivity.this,book);
        recyclerView.setAdapter(chapter_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chapterFactory = new ChapterFactory(this);
        switchCompat=(SwitchCompat)findViewById(R.id.Chapter_Switch);
        List<Chapter> data = chapterFactory.getChapterFromDB();
        if(data.size() > 0){
           chapter_adapter.addData(data);
            int chapterNumber = getChapterNumber(PageFactory.getInstance().getCurrentEnd(),data);
            chapter_adapter.setCurrentChapter(chapterNumber);
            chapter_adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chapterNumber);
        }else{
            loadChapters(ChapterFactory.KEYWORD_ZHANG);
        }

       chapter_adapter.setOnItemClickListener(new Chapter_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Chapter chapter) {
                Intent intent = new Intent();
                intent.putExtra("position",chapter.getChapterBytePosition());
                setResult(RESULT_CHAPTER,intent);
                finish();
            }
        });
        bookmark_adapter.setOnItemClickListener(new Bookmark_adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Bookmark bookmark) {
                Intent intent = new Intent();
                intent.putExtra("position",bookmark.getBegin());
                setResult(RESULT_CHAPTER,intent);
                finish();
            }

            @Override
            public boolean OnItemLongClick(final int position) {
                AlertDialog alertDialog=new AlertDialog.Builder(ChapterActivity.this).setTitle("删除确认框").setMessage("确认删除该书签？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bookmark_adapter.delete(position);
                        bookmark_adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
                alertDialog.show();
                return true;
            }


        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.Chapter_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  onBackPressed();
            }
        });
        switchCompat.setOnCheckedChangeListener(new CheckChanged());
    }
    class CheckChanged implements SwitchCompat.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if(checked){
                recyclerView=(RecyclerView)findViewById(R.id.Chapter_recyclerview);
                recyclerView.setAdapter(bookmark_adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ChapterActivity.this));

            }
            else {
                recyclerView=(RecyclerView)findViewById(R.id.Chapter_recyclerview);
                recyclerView.setAdapter(chapter_adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ChapterActivity.this));
                List<Chapter> data = chapterFactory.getChapterFromDB();
                int chapterNumber = getChapterNumber(PageFactory.getInstance().getCurrentEnd(),data);
                chapter_adapter.setCurrentChapter(chapterNumber);
                chapter_adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chapterNumber);
            }
        }
    }

    private void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在加载章节中...");
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void loadChapters(String key){
        chapter_adapter.clearData();
        chapter_adapter.notifyDataSetChanged();
        showDialog();
        chapterFactory.setProgressCallback(new ChapterFactory.ProgressCallback() {
            @Override
            public void currentPercentage(int percent) {
                if(progressDialog.getProgress() != percent){
                    progressDialog.setProgress(percent);
                    progressDialog.setMessage("正在加载章节中...");
                }
            }
        });
        chapterFactory.setKeyword(key);
        chapterFactory.getChapterFromFile(this);
    }

    public void onFinishLoad(List<Chapter> list) {
        int chapterNumber = getChapterNumber(PageFactory.getInstance().getCurrentEnd(),list);
        chapter_adapter.setCurrentChapter(chapterNumber);
        chapter_adapter.clearData();
        chapter_adapter.addData(list);
        recyclerView.scrollToPosition(chapterNumber);
        progressDialog.dismiss();
    }


    public void onNotFound() {
       Toast.makeText(this,"未发现章节",Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    private int getChapterNumber(int position,List<Chapter> list){
        int begin = 0;
        int end = list.size()-1;
        while (begin <= end){
            int middle = begin + (end-begin)/2;
            if(middle == 0 && list.get(middle).getChapterBytePosition() >= position){
                return 0;
            }
            if(middle == list.size()-1 && list.get(list.size()-1).getChapterBytePosition() <= position){
                return list.size()-1;
            }
            if(list.get(middle).getChapterBytePosition() <= position  && list.get(middle+1).getChapterBytePosition() > position){
                return middle;
            }else if (list.get(middle).getChapterBytePosition() > position && list.get(middle-1).getChapterBytePosition() <= position){
                return middle -1;
            }else if(list.get(middle).getChapterBytePosition() < position && list.get(middle+1).getChapterBytePosition() < position){
                begin = middle+1;
            }else if(list.get(middle).getChapterBytePosition() > position && list.get(middle-1).getChapterBytePosition() > position){
                end = middle-1;
            }
        }
        return 0;
    }
}