package com.example.administrator.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import Books.Book;
import Page.PageFactory;
import Page.PageView;

public class ReadActivity extends AppCompatActivity {
    PageView pageView;
    private View actionBar;
    private View statusBar;
    private TextView progressText;
    PageFactory mPageFactory;
    Integer RESULT_READ=1;
    int begin=0;
    Book book;
    private int fontsize;
    Toolbar toolbar;
    Integer REQUEST_CHAPTER=2;
    Boolean isMeau=false,isSetview=false;
    LinearLayout l1,l2;
    Button mulu,set_view,pre_ch,next_ch,decrease,increase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        book=(Book)getIntent().getSerializableExtra("book");
        toolbar=(Toolbar)findViewById(R.id.read_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(MyMeauListener);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent();
                setResult(RESULT_READ,myIntent);
                PageFactory.close();
                finish();
            }
        });

        l1=(LinearLayout)findViewById(R.id.read_meau);
        l2=(LinearLayout)findViewById(R.id.set_view_meau);
        mulu=(Button)findViewById(R.id.Chapter_list);
        set_view=(Button)findViewById(R.id.set_view);
        pre_ch=(Button)findViewById(R.id.pre_Chapter);
        next_ch=(Button)findViewById(R.id.next_Chapter);
        decrease=(Button)findViewById(R.id.textsize_decrease);
        increase=(Button)findViewById(R.id.textsize_increase);
        pageView=(PageView)findViewById(R.id.reading_activity_view);
        pageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        mPageFactory = PageFactory.getInstance(pageView,book);
        mPageFactory.nextPage();
        pageView.setOnPositionClickListener(new PageView.OnPositionClickListener() {
            @Override
            public void onLeftClick() {
                if(!isMeau&&!isSetview) {
                    mPageFactory.prePage();
                }
                else {
                    isMeau=false;
                    isSetview=false;
                    showMeau();
                }
            }

            @Override
            public void onRightClick() {
                if(!isMeau&&!isSetview) {
                    mPageFactory.nextPage();
                }
                else {
                    isMeau=false;
                    isSetview=false;
                    showMeau();
                }
            }

            @Override
            public void onMiddleClick() {
                if(!isMeau&&!isSetview) {
                   isMeau=true;
                    showMeau();
                }
                else {
                    isMeau=false;
                    isSetview=false;
                    showMeau();
                }
            }

            @Override
            public void onScrollLeft() {//向左滑动
                if(!isMeau&&!isSetview) {
                    mPageFactory.nextPage();
                }
                else {
                    isMeau=false;
                    isSetview=false;
                    showMeau();
                }
            }

            @Override
            public void onScrollRight() {//向右滑动
                if(!isMeau&&!isSetview) {
                    mPageFactory.prePage();
                }
                else {
                    isMeau=false;
                    isSetview=false;
                    showMeau();
                }
            }
        });
        set_view.setOnClickListener(new MyOnClickListener(set_view.getId()));
        decrease.setOnClickListener(new MyOnClickListener(decrease.getId()));
        increase.setOnClickListener(new MyOnClickListener(increase.getId()));
        next_ch.setOnClickListener(new MyOnClickListener(next_ch.getId()));
        pre_ch.setOnClickListener(new MyOnClickListener(pre_ch.getId()));
        mulu.setOnClickListener(new MyOnClickListener(mulu.getId()));
    }
    private Toolbar.OnMenuItemClickListener MyMeauListener=new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.add_Bookmark:
                    if(mPageFactory.addBookmark()){
                        Toast.makeText(ReadActivity.this, "添加书签成功！", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ReadActivity.this, "已存在该书签！", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
           return true;
        }
    };
    class MyOnClickListener implements View.OnClickListener{
        Integer id;
        public MyOnClickListener(int id) {
            super();
            this.id=id;
        }
        @Override
        public void onClick(View view) {
            switch (id){
                case R.id.Chapter_list://目录
                    isSetview=false;
                    isMeau=false;
                    showMeau();
                    Intent intent=new Intent(ReadActivity.this,ChapterActivity.class);
                    startActivityForResult(intent,REQUEST_CHAPTER);
                    break;
                case R.id.set_view://设置页面字体大小等
                    isSetview=true;
                    isMeau=false;
                    showMeau();
                    break;
                case R.id.pre_Chapter://上一章
                    break;
                case R.id.next_Chapter://下一章
                    break;
                case R.id.textsize_decrease://字体减小
                    fontsize=pageView.getFontSize();
                    if(fontsize>30) {
                        fontsize -=5;
                    }
                    else {
                        Toast.makeText(ReadActivity.this,"当前已为最小字号！",Toast.LENGTH_SHORT).show();
                    }
                    pageView.setFontSize(fontsize);
                    break;
                case R.id.textsize_increase://字体增大
                    fontsize=pageView.getFontSize();
                    if(fontsize<75){
                        fontsize+=5;
                    }
                    else {
                        Toast.makeText(ReadActivity.this,"当前已为最大字号！",Toast.LENGTH_SHORT).show();
                    }
                    pageView.setFontSize(fontsize);
                    break;
            }
        }
    }
    public void showMeau(){
      if(isMeau) {
          l1.setVisibility(View.VISIBLE);
      }
        else{
          l1.setVisibility(View.INVISIBLE);
      }
        if(isSetview) {
            l2.setVisibility(View.VISIBLE);
        }
        else{
            l2.setVisibility(View.INVISIBLE);
        }
        if(isMeau||isSetview){
            toolbar.setVisibility(View.VISIBLE);
        }
        else{
            toolbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 2://从章节目录跳转回来的，且准备跳转到点击的章节去。
                int position=data.getIntExtra("position",-1);
                if(position==-1){
                    Toast.makeText(this,"跳转异常，请重试！",Toast.LENGTH_SHORT).show();
                }
                else{
                    mPageFactory.setPosition(position);
                }
                break;
            case 3:
                position=data.getIntExtra("bookmark",-1);
                if(position==-1){
                    Toast.makeText(this,"跳转异常，请重试！",Toast.LENGTH_SHORT).show();
                }
                else{
                    mPageFactory.setPosition(position);
                }
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//这里重写返回键
                mPageFactory.saveBookmark();
                Intent myIntent = new Intent(ReadActivity.this, MainActivity.class);
                setResult(RESULT_READ,myIntent);
                PageFactory.close();
                this.finish();
            return true;
        }
        return false;
    }

    protected void onPause() {
        super.onPause();
       mPageFactory.saveBookmark();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_meau,menu);
        return true;
    }
}
