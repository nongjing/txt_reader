package com.example.administrator.Activity;

        import java.io.File;
        import java.util.List;

        import android.Manifest;
        import android.app.Activity;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.os.Environment;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import Books.Book;
        import DB.Book_dbhelper;
        import Page.SPHelper;
        import Page.Util;
        import file.File_Data;
        import file.File_adapter;
        import file.File_bean;

public class FilesActivity extends AppCompatActivity {
    static private  Context context;
     static  private Activity activity;
    private RecyclerView recyclerView;
    Integer RESULT_FILE_OK=0;
    Integer RESULT_FILE_FAIL=999;
    private File_adapter file_adapter;
    private String currentpath;
    private TextView txt1;
    private ImageButton imagebt1;
    private Integer currpos;
    private File_Data file_data;
    Book_dbhelper book_dbhelper;
    private List<File_bean> list ;
    Book book;
    mListener m1=new mListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        context=getApplicationContext();
        activity=this;
        txt1=(TextView)findViewById(R.id.txt1);
        imagebt1 = (ImageButton) findViewById(R.id.imageBt1);
        file_data=new File_Data(FilesActivity.this,this);
        recyclerView=(RecyclerView)findViewById(R.id.listFile);
        String path=SPHelper.getInstance(FilesActivity.this).getPath();
        list=file_data.init(new File(path));
        file_adapter=new File_adapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(file_adapter);
        file_adapter.setOnItemClickListener(m1);
        //回根目录

        imagebt1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File f1=new File(file_data.getCurrentpath());
                File f2=new File(f1.getParent());
                if(!f1.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                    list = file_data.init(f2);
                    file_adapter = new File_adapter(list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                    recyclerView.setAdapter(file_adapter);
                    file_adapter.setOnItemClickListener(m1);
                }
            }
        });

    }
    public static Context getContext(){
        return context;
    }
    public static Activity getActivity(){
        return activity;
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//这里重写返回键
            currentpath=file_data.getCurrentpath();
            File f1=new File(currentpath);
            File f2=new File(f1.getParent());
            String S=Environment.getExternalStorageDirectory().getPath();
            String S1=SPHelper.getInstance(FilesActivity.this).getPath();
            if(!f1.getPath().equals(S1)) {
                list=file_data.init(f2);
                file_adapter=new File_adapter(list);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(file_adapter);
                file_adapter.setOnItemClickListener(m1);
            }
            Boolean a=f1.getPath().equals(S);
            boolean b=f1.getPath().equals(S1);
            if(a||b){
                Intent myIntent = new Intent(FilesActivity.this,MainActivity.class);
                startActivity(myIntent);
                this.finish();
            }
            return true;
        }
        return false;
    }
    class mListener implements File_adapter.OnRecyclerViewItemClickListener{

        @Override
        public void onItemClick(View view) {
            String folder = ((TextView) view.findViewById(R.id.txtview))
                    .getText().toString();
            // 获取单击的文件或文件夹的名称
            currentpath=file_data.getCurrentpath();
            File filef = new File(currentpath + '/'
                    + folder);
            if(filef.isFile()){
                book=new Book(filef.getName(),filef.getPath());
                if(Book_dbhelper.getInstance(FilesActivity.this).selectbook(book)) {
                    Toast.makeText(FilesActivity.this, "已存在该书籍！", Toast.LENGTH_SHORT).show();
                }
                else {
                    SPHelper.getInstance(FilesActivity.this).setPath(file_data.getCurrentpath());
                    Book_dbhelper.getInstance(FilesActivity.this).saveBook(book);
                    Toast.makeText(FilesActivity.this, "添加书籍成功！", Toast.LENGTH_SHORT).show();
                    book.setEncoding(Util.getEncoding(book));
                    Book_dbhelper.getInstance(FilesActivity.this).updateBook(book);
                    SPHelper.getInstance(FilesActivity.this).setBookmarkStart(book.getBookName(),0);
                    SPHelper.getInstance(FilesActivity.this).setBookmarkEnd(book.getBookName(),0);
                    Intent a=new Intent(FilesActivity.this,MainActivity.class);
                    startActivity(a);
                    finish();

                }
            }
            else{
                try {
                    list=file_data.init(filef);
                    file_adapter=new File_adapter(list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    recyclerView.setAdapter(file_adapter);
                    file_adapter.setOnItemClickListener(m1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

