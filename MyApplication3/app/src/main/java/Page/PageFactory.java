package Page;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import Books.Book;
import Bookmarks.Bookmark;
import DB.Book_dbhelper;

/**
 * Created by Administrator on 2017/4/11.
 */
public class PageFactory {

    private int fileLength;//映射到内存中Book的字节数


    private int begin;//当前阅读的字节数_开始
    private int end;//当前阅读的字节数_结束
    private MappedByteBuffer mappedFile;//映射到内存中的文件
    private RandomAccessFile randomFile;//关闭Random流时使用
    private String encoding;
    private Context mContext;
    private PageView mView;
    private ArrayList<String> content = new ArrayList<>();
    private static PageFactory instance;
    private Book book;
    private SPHelper spHelper;
    private Paint mPaint;
    private int lineNumber,pageWidth;
    public static PageFactory getInstance(PageView view,Book book){
        if(instance == null){
            synchronized (PageFactory.class){
                if(instance == null){
                    instance = new PageFactory(view);
                    instance.openBook(book);
                }
            }
        }
        return instance;
    }
    public static PageFactory getInstance(){
        return instance;
    }
    private PageFactory(PageView view){
        mContext = view.getContext();
        mView = view;
        spHelper=SPHelper.getInstance(mContext);
    }
    private void openBook(final Book book){
        this.book = book;
        encoding = book.getEncoding();
        begin = spHelper.getBookmarkStart(book.getBookName());
        end = spHelper.getBookmarkEnd(book.getBookName());
        File file = new File(book.getPath());
        fileLength = (int) file.length();
        try {
            randomFile = new RandomAccessFile(file, "r");
            mappedFile = randomFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, (long) fileLength);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"打开失败！",Toast.LENGTH_SHORT).show();
        }
    }
    private byte[] readParagraphForward(int end){
        byte b0;
        int before =0;
        int i = end;
        while(i < fileLength){
            b0 = mappedFile.get(i);
            if(encoding.equals("UTF-16LE")) {
                if (b0 == 0 && before == 10) {
                    break;
                }
            }else{
                if(b0==10&&before==13&&i!=end+1){
                    break;
                }
                if(b0==10&&before!=13&&i!=end){
                    break;
                }
            }
            before = b0;
            i++;
        }

        i = Math.min(fileLength-1,i);
        before=mappedFile.get(i-1);
        b0=mappedFile.get(i);
        if(b0==10&&before==13){
        int nParaSize = i - end-1  ;

        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {

            buf[i] =  mappedFile.get(end + i);
        }
        return buf;
        }
        else {
            int nParaSize = i - end  ;

            byte[] buf = new byte[nParaSize];
            for (i = 0; i < nParaSize; i++) {

                buf[i] =  mappedFile.get(end + i);
            }
            return buf;
        }
    }
    //向前读取一个段落
    private byte[] readParagraphBack(int begin){
        byte b0 ;
        byte behind = 1;
        int i = begin -1 ;
        while(i > 0){
            b0 = mappedFile.get(i);
            if(encoding.equals("UTF-16LE")){
                if(b0 == 10 && behind==0 && i != begin-2){
                    i+=2;
                    break;
                }
            }
            else{
                if(behind==10 &&b0!=13){
                    i++;
                    break;
                }
                if(behind==10&&b0==13){
                    break;
                }
            }
            i--;
            behind = b0;
        }
        int nParaSize = begin -i ;
        byte[] buf = new byte[nParaSize];
        for (int j = 0; j < nParaSize; j++) {
            buf[j] = mappedFile.get(i + j);
        }
        return buf;

    }
    //获取后一页的内容
    private void pageDown(){
        String strParagraph = "";
        while((content.size()<lineNumber) && (end< fileLength)){
            byte[] byteTemp = readParagraphForward(end);
            end += byteTemp.length;
            try{
                strParagraph = new String(byteTemp, encoding);
            }catch(Exception e){
                e.printStackTrace();
            }
            if(encoding.equals("UTF-8")) {
                strParagraph = strParagraph.replaceAll("\r\n", "        ");
                strParagraph = strParagraph.replaceAll("\n", "        ");
            }
            else{
                strParagraph = strParagraph.replaceAll("\r\n", "    ");
                strParagraph = strParagraph.replaceAll("\n", "");
            }
            while(strParagraph.length() >  0){
                int size = mPaint.breakText(strParagraph,true,pageWidth,null);
                content.add(strParagraph.substring(0,size));
                strParagraph = strParagraph.substring(size);
                if(content.size() >= lineNumber){
                    break;
                }
            }
            if(strParagraph.length()>0){
                try{
                    end -= (strParagraph).getBytes(encoding).length;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }
        spHelper.setBookmarkStart(book.getBookName(),begin);
        spHelper.setBookmarkEnd(book.getBookName(),end);
    }
    //获取后一页的内容
    private  void pageUp(){
        String strParagraph = "",mstr="";//str为段落的字节转换为对应encoding的字符串，mstr为为进行分行前的字符串，用于正确设置begin的位置，保证分页是相同的。
        int str_true_length = 0;//记录某段的总字节数，用于辅助完成翻页。
        int n;//记录在某分页显示的段落在本页应该显示的行数。
        List<String> tempList = new ArrayList<>();//用于倒着存储每一行的数据。
        while(tempList.size()<lineNumber && begin>0){
            byte[] byteTemp = readParagraphBack(begin);
            begin -= byteTemp.length;
            n=0;
            str_true_length=byteTemp.length;
            try{
                strParagraph = new String(byteTemp, encoding);

            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
            if(encoding.equals("UTF-8")) {
                strParagraph = strParagraph.replaceAll("\r\n", "        ");
                strParagraph = strParagraph.replaceAll("\n", "        ");
            }
            else{
                strParagraph = strParagraph.replaceAll("\r\n", "    ");
                strParagraph = strParagraph.replaceAll("\n", "");
            }
            mstr=strParagraph;
            while(strParagraph.length() > 0){
                int size = mPaint.breakText(strParagraph,true,pageWidth,null);
                tempList.add(strParagraph.substring(0,size));
                strParagraph = strParagraph.substring(size);
                if(tempList.size() >= lineNumber){
                    break;
                }
            }
            /*当该段为分页时的段落，此时先得到该段在本页的前一页应该显示的行数n，
            然后再将字符串mstr分出行数n，再把begin加上该段应在前一页显示的字节数，即该段字节总数减去mstr分掉行数n之后的字节总数。*/
            if(strParagraph.length() > 0){
                while (strParagraph.length()>0){
                    int size = mPaint.breakText(strParagraph,true,pageWidth,null);
                    tempList.add(strParagraph.substring(0,size));
                    n++;
                    strParagraph = strParagraph.substring(size);
                }

                try{
                   for(int i=0;i<n;i++){
                       int size = mPaint.breakText(mstr,true,pageWidth,null);
                       tempList.add(mstr.substring(0,size));
                      mstr= mstr.substring(size);
                   }
                    begin+=str_true_length-mstr.getBytes(encoding).length;
                }catch (UnsupportedEncodingException u){
                    u.printStackTrace();
                }
            }
        }
        spHelper.setBookmarkStart(book.getBookName(),begin);
        spHelper.setBookmarkEnd(book.getBookName(),end);
    }
    //获取某书签的提示信息。即为当前书页的前两行。
    private String bookmarkHint(){
        String bookmark_hint="";
        bookmark_hint=content.get(0)+content.get(1);
        return bookmark_hint;
    }
    public void printPage(){
        if(content.size()>0){
            mView.initProperties(this);
        }
    }

    public void nextPage(){
        mPaint=mView.getmPaint();
        lineNumber=mView.getLineNumber();
        pageWidth=mView.getPageWidth();
        if(end >= fileLength){
            return;
        }else{
            content.clear();
            begin = spHelper.getBookmarkStart(book.getBookName());
            end = spHelper.getBookmarkEnd(book.getBookName());
            begin = end;
            pageDown();
        }
        printPage();
    }
    public void prePage(){
        mPaint=mView.getmPaint();
        lineNumber=mView.getLineNumber();
        pageWidth=mView.getPageWidth();
        if(begin <= 0){
            return;
        }else{
            content.clear();
            pageUp();
            end=begin;
            pageDown();
        }
        printPage();
    }
    public void saveBookmark(){
        SPHelper.getInstance(mContext).setBookmarkEnd(book.getBookName(),begin);
        SPHelper.getInstance(mContext).setBookmarkStart(book.getBookName(),begin);
        SPHelper.getInstance(mContext).setBookprogress(book.getBookName(),mView.getProgress());
        SPHelper.getInstance(mContext).setBookReadHint(book.getBookName(),bookmarkHint());
    }
    public boolean addBookmark(){
        String bookmark_hint=bookmarkHint();
        begin=spHelper.getBookmarkStart(book.getBookName());
        SimpleDateFormat sDateFormat=  new    SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
        String    date =    sDateFormat.format(new    java.util.Date());
        Bookmark bookmark=new Bookmark(book.getBookName(),bookmark_hint,begin,date);
        if(!Book_dbhelper.getInstance(mContext).selectbookmark(bookmark)) {
           Book_dbhelper.getInstance(mContext).saveBookmark(bookmark);
           return true;
        }else
        {
            return false;
        }
    }
    public ArrayList<String> getContent(){return content;}
    public int getFileLength(){
        return fileLength;
    }
    public MappedByteBuffer getMappedFile(){
        return mappedFile;
    }
    public void setPosition(int position){//用于目录跳转和书签跳转
        spHelper.setBookmarkEnd(book.getBookName(),position);
        nextPage();
    }
    public Book getBook(){
        return book;
    }
    public String getEncoding(){
        return encoding;
    }
    public int getCurrentEnd(){
        return end;
    }

    public int getCurrentBegin(){
        return begin;
    }
    public static void close(){
        if(instance != null){
            try{
                instance.randomFile.close();
            }catch (IOException i){
                i.printStackTrace();
            }
            instance = null;
        }
    }
}
