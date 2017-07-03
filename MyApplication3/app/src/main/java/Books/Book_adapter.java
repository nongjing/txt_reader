package Books;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.Activity.R;

import DB.*;
import Page.SPHelper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/27.
 */
public class Book_adapter extends RecyclerView.Adapter<Book_adapter.BookViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private  Context mcontext;
    public   List<Book> books;
    private boolean allowMove;
    private boolean issshowbox=false;
    private Map<Integer,Boolean> map=new HashMap<>();
    private int Size;
    private  RecyclerViewOnItemClickListener onItemClickListener;
    public Book_adapter(Context context) {
        super();
        mcontext=context;
        books=Book_dbhelper.getInstance(mcontext).getAllBook();
        Size=books.size();
    }
    public  void initMap(){
        for (int i=0;i<Size;i++){
            if(i<books.size()) {
                map.put(i, false);
            }
            else{
                map.remove(i);
            }
        }

    }
    public void remove(){
        for(int i=0;i<map.size();i++) {
            if(map.get(i)) {
                Book_dbhelper.getInstance(mcontext).deleteBookWithChapters(books.get(i));
                SPHelper.getInstance(mcontext).setBookprogress(books.get(i).getBookName(),0);
                SPHelper.getInstance(mcontext).setBookReadHint(books.get(i).getBookName(),"尚未阅读");
            }
        }
        books=Book_dbhelper.getInstance(mcontext).getAllBook();

        initMap();
    }
    public void removeAll(){
        Book_dbhelper.getInstance(mcontext).clearAllData();
        SPHelper.getInstance(mcontext).clearAllBookMarkData();
        books=Book_dbhelper.getInstance(mcontext).getAllBook();
        initMap();
    }
    @Override
    public Book_adapter.BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,null);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new BookViewHolder(view);
    }
    public interface RecyclerViewOnItemClickListener{
        void onItemClickListener(View v,int position);
        boolean onItemLongClickListener(View v,int position);
    }
    public void  setMap(int position,Boolean bool){
        map.put(position,bool);
    }
    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
    public void onClick(View v){
        if(onItemClickListener!=null){
            onItemClickListener.onItemClickListener(v,(Integer)v.getTag());
        }
    }
    public boolean onLongClick(View v){
        initMap();
        return onItemClickListener!=null&&onItemClickListener.onItemLongClickListener(v,(Integer) v.getTag());
    }
    public void setShowBox(){
        issshowbox=!issshowbox;
    }
    public void setSelectItem(int position){
        if(map.get(position)){
            map.put(position,false);
        }else{
            map.put(position,true);
        }
        notifyItemChanged(position);
    }
    @Override
    public void onBindViewHolder(BookViewHolder holder, final int position) {
        Book book=books.get(position);
        if(issshowbox){
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        else{
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        holder.view.setTag(position);
        String bookName=book.getBookName().substring(0,book.getBookName().lastIndexOf("."));
        holder.title.setText(bookName);
        holder.preview.setText(SPHelper.getInstance(mcontext).getBookReadHint(book.getBookName()));
        holder.progressbar.setMax(100);
        float progress=SPHelper.getInstance(mcontext).getBookprogress(book.getBookName());
        DecimalFormat format = new DecimalFormat("#0.00");
        String readingProgress = format.format(progress) + "%";
        holder.progressbar.setProgress((int)progress);
        holder.progressText.setText(readingProgress);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                map.put(position,ischecked);
            }
        });
        if(map.get(position)==null){
            map.put(position,false);
        }
        holder.checkBox.setChecked(map.get(position));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
    public boolean getissShowbox(){return  issshowbox;}
    class  BookViewHolder extends RecyclerView.ViewHolder{
        public TextView title,preview,progressText;
        public ProgressBar progressbar;
        public CardView cardview;
        public CheckBox checkBox;
        public View view;
        public BookViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            title=(TextView)itemView.findViewById(R.id.main_book_item_title);
            preview=(TextView)itemView.findViewById(R.id.main_book_item_preview);
            progressbar=(ProgressBar)itemView.findViewById(R.id.main_book_item_progress_bar);
            progressText=(TextView)itemView.findViewById(R.id.main_book_item_progress_text);
            checkBox=(CheckBox)itemView.findViewById(R.id.cb);
        }
    }

}
