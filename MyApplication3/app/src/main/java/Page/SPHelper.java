package Page;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

import Books.Book;

/**
 * Created by Administrator on 2017/4/11.
 */
public class SPHelper {
    private SharedPreferences config ;
    private SharedPreferences.Editor configEditor;
    private SharedPreferences bookmark;
    private SharedPreferences.Editor bookmarkEditor;
    private static SPHelper instance;
    private Context mcontext;
    private SPHelper(Context context) {
        mcontext = context;
        config = mcontext.getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        configEditor = config.edit();
        bookmark = mcontext.getApplicationContext().getSharedPreferences("bookmark", Context.MODE_PRIVATE);
        bookmarkEditor = bookmark.edit();
    }

    public static SPHelper getInstance(Context context){
        if(instance == null){
            synchronized(SPHelper.class){
                if(instance == null){
                    instance = new SPHelper(context);
                }
            }
        }
        return instance;
    }
    public int getFontSize(){
        return config.getInt("font_size",45);
    }
    public void setFontSize(int size){
        configEditor.putInt("font_size",size).apply();
    }
    public void setPath(String path){configEditor.putString("Path",path).apply();}
    public String getPath(){return config.getString("Path", String.valueOf(Environment.getExternalStorageDirectory()));}
    public void setNightMode(boolean which){
        configEditor.putBoolean("night_mode",which).apply();
    }
    public boolean isNightMode(){
        return config.getBoolean("night_mode",false);
    }


    public void setBookmarkStart(String bookName,int position){
        bookmarkEditor.putInt(bookName+"start",position).apply();
    }
    public int getBookmarkStart(String bookName){
        return bookmark.getInt(bookName+"start",0);
    }
    public void setBookmarkEnd(String bookName,int position){
        bookmarkEditor.putInt(bookName+"end",position).apply();
    }
    public int getBookmarkEnd(String bookName){
        return bookmark.getInt(bookName+"end",0);
    }
    public float getBookprogress(String bookName){return bookmark.getFloat(bookName+"Progress",0);}
    public void setBookprogress(String bookName,float progress){
        bookmarkEditor.putFloat(bookName+"Progress",progress).apply();
    }
    public String getBookReadHint(String bookName){return bookmark.getString(bookName+"ReadHint","尚未阅读");}
    public void setBookReadHint(String bookName,String readhint){
        bookmarkEditor.putString(bookName+"ReadHint",readhint).apply();
    }
    public void clearAllBookMarkData(){
        bookmarkEditor.clear().apply();
    }

    public String getBookEncoding(Book book){
        return config.getString(book.getPath(),"");
    }
    public void setBookEncoding(Book book,String encoding){
        configEditor.putString(book.getPath(),encoding).apply();
    }
    public void deleteBookMark(String bookName){
        bookmarkEditor.remove(bookName).apply();
    }
}
