package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import Books.Book;
import Bookmarks.Bookmark;
import Chapters.Chapter;

/**
 * Created by Administrator on 2017/3/27.
 */
public class Book_dbhelper {

        private static Book_dbhelper instance;
        private SQLiteDatabase db;
        private static Context mcontext;

        public Book_dbhelper(Context context){
            mcontext=context;
            db = new Book_database_openhelper(context).getWritableDatabase();
        }

        public static Book_dbhelper getInstance(Context context){
            if(instance == null){
                synchronized(Book_dbhelper.class){
                    if(instance == null){
                        instance = new Book_dbhelper(context);
                    }
                }
            }
            return instance;
        }
        public List<Book> getAllBook(){
            Cursor cursor = db.rawQuery("SELECT * FROM book order by access_time desc",null);
            List<Book> list = new ArrayList<>();
            Book book;
            while (cursor.moveToNext()){
                book = new Book();
                book.setBookName(cursor.getString(cursor.getColumnIndex("book_name")));
                book.setPath(cursor.getString(cursor.getColumnIndex("book_path")));
                book.setAccessTime(cursor.getLong(cursor.getColumnIndex("access_time")));
                book.setEncoding(cursor.getString(cursor.getColumnIndex("book_encoding")));
                list.add(book);
            }
            cursor.close();
            return list;
        }
        //获取某本书的所有书签
        public List<Bookmark> getBookMark(String name){
            Cursor cursor=db.rawQuery("SELECT * FROM bookmark WHERE  book_name=?",new String[]{name});
            List<Bookmark> list=new ArrayList<>();
            Bookmark bookmark;
            while (cursor.moveToNext()){
                bookmark=new Bookmark();
                bookmark.setBegin(cursor.getInt(cursor.getColumnIndex("bookmark_begin")));
                bookmark.setName(cursor.getString(cursor.getColumnIndex("book_name")));
                bookmark.setHint(cursor.getString(cursor.getColumnIndex("bookmark_hint")));
                bookmark.setSetmark_time(cursor.getString(cursor.getColumnIndex("bookmark_settime")));
                list.add(bookmark);
            }
            cursor.close();
            return list;
        }
        public void saveBookmark(Bookmark bookmark){
                ContentValues cv=new ContentValues();
                    cv.put("book_name", bookmark.getName());
                    cv.put("bookmark_begin", bookmark.getBegin());
                    cv.put("bookmark_hint", bookmark.getHint());
                    cv.put("bookmark_settime", bookmark.getSetmark_time());
                    db.insert("bookmark","book_name",cv);
        }
        public void deleteBookmark(Bookmark bookmark){
            db.delete("bookmark","book_name=? and bookmark_hint=?",new String[]{bookmark.getName(),bookmark.getHint()});
        }
        public boolean selectbook(Book book){
            Cursor a=db.rawQuery("SELECT * FROM book WHERE book_name=? and book_path=?",new String []{book.getBookName(),book.getPath()});
            if(a.moveToFirst()){
                return true;
            }
            else {
                return false;
            }
        }
    public boolean selectchapter(Chapter chapter){
        Cursor a=db.rawQuery("SELECT * FROM chapter WHERE book_name=? and chapter_name=?",new String []{chapter.getBookName(),chapter.getChapterName()});
        if(a.moveToFirst()){
            return true;
        }
        else {
            return false;
        }
    }
    public boolean selectbookmark(Bookmark bookmark){
        Cursor a=db.rawQuery("SELECT * FROM bookmark WHERE book_name=? AND bookmark_hint=?",new String []{bookmark.getName(),bookmark.getHint()});
        if(a.moveToFirst()){
            return true;
        }
        else {
            return false;
        }

    }
        public ArrayList<Chapter> getChapters(String bookName){
            Cursor cursor = db.rawQuery("SELECT * FROM Chapter WHERE book_name=?",new String[]{bookName});
            ArrayList<Chapter> list = new ArrayList<>();
            Chapter chapter;
            while (cursor.moveToNext()){
                chapter = new Chapter();
                chapter.setBookName(cursor.getString(cursor.getColumnIndex("book_name")));
                chapter.setChapterName(cursor.getString(cursor.getColumnIndex("chapter_name")));
                chapter.setChapterParagraphPosition(cursor.getInt(cursor.getColumnIndex("chapter_paragraph_position")));
                chapter.setChapterBytePosition(cursor.getInt(cursor.getColumnIndex("chapter_byte_position")));
                list.add(chapter);
            }
            cursor.close();
            return list;
        }
        public void saveChapters(final List<Chapter> list){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ContentValues cv;
                    for(Chapter chapter :list){
                        cv = new ContentValues();
                        cv.put("book_name", chapter.getBookName());
                        cv.put("chapter_name", chapter.getChapterName());
                        cv.put("chapter_paragraph_position", chapter.getChapterParagraphPosition());
                        cv.put("chapter_byte_position", chapter.getChapterBytePosition());
                        db.insert("chapter","book_name",cv);
                    }
                }
            }).start();
        }
        public void saveBook( Book book){
            ContentValues cv = new ContentValues();
            cv.put("book_name",book.getBookName());
            cv.put("book_path",book.getPath());
            cv.put("access_time",book.getAccessTime());
            cv.put("book_encoding",book.getEncoding());
            db.insert("book","book,name",cv);
        }
        public void saveBook(final List<Book> list){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(final Book book : list){
                        saveBook(book);
                    }
                }
            }).start();

        }
        public void deleteBookWithChapters(final Book book){
            db.delete("book","book_name=?",new String[]{book.getBookName()});
            db.delete("chapter","book_name=?",new String[]{book.getBookName()});
            db.delete("bookmark","book_name=?",new String[]{book.getBookName()});
            new Thread(new Runnable() {
                @Override
                public void run() {
                    db.delete("book","book_name=?",new String[]{book.getBookName()});
                    db.delete("chapter","book_name=?",new String[]{book.getBookName()});
                    db.delete("bookmark","book_name=?",new String[]{book.getBookName()});
                }
            }).start();
        }
        public void clearAllData(){
            db.delete("book",null,null);
            db.delete("chapter",null,null);
            db.delete("bookmark",null,null);
        }

        /**
         * 更新数据库中的某条数据，但不能更新路径，因为路径是文件的标识
         * @param book 要更新的条目
         */
        public void updateBook(Book book){
            ContentValues cv = new ContentValues();
            cv.put("access_time",book.getAccessTime());
            cv.put("book_name",book.getBookName());
            cv.put("book_encoding",book.getEncoding());
            db.update("book",cv,"book_path=?",new String[]{book.getPath()});
        }
        public void deleteChapters(Book book){
            db.delete("chapter","book_name=?",new String[]{book.getBookName()});
        }
}

