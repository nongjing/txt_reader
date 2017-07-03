package DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/3/27.
 */
public class Book_database_openhelper extends SQLiteOpenHelper {
        private static final int VERSION = 1;
        private static final String NAME = "book.db";
        public static final String CREATE_BOOK_LIST = "create table book (" +
                "id integer primary key autoincrement," +
                "book_name text," +
                "book_path text," +
                "book_encoding text,"+
                "access_time long)";
        public static final String CREATE_CHAPTER = "create table chapter (" +
                "id integer primary key autoincrement," +
                "book_name text," +
                "chapter_paragraph_position integer," +
                "chapter_byte_position integer," +
                "chapter_name)";
        public static final String CREATE_BOOKMARK="create table bookmark("+
                "id integer primary key autoincrement,"+
                "book_name text,"+
                "bookmark_begin integer,"+
                "bookmark_hint text,"+
                "bookmark_settime text)";
         Book_database_openhelper(Context context) {
        super(context,NAME,null,VERSION);
    }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_BOOK_LIST);
            db.execSQL(CREATE_CHAPTER);
            db.execSQL(CREATE_BOOKMARK);
        }

        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            if(newVersion > oldVersion){
                db.execSQL("drop table if exists book");
                db.execSQL("drop table if exists chapter");
                db.execSQL("drop table if exists bookmark");
                onCreate(db);
            }
        }


}
