package Bookmarks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.Activity.R;

import java.util.ArrayList;
import java.util.List;

import Books.Book;
import DB.Book_dbhelper;
import Page.PageFactory;

/**
 * Created by Administrator on 2017/4/19.
 */
public class Bookmark_adapter extends RecyclerView.Adapter<Bookmark_adapter.BookmarkViewholder>{
    Context mcontext;
    List<Bookmark> bookmarks=new ArrayList<>();
    private OnItemClickListener onItemClicklistener;
    public Bookmark_adapter(Context context,Book book) {
        super();
        mcontext=context;
        bookmarks= Book_dbhelper.getInstance(mcontext).getBookMark(book.getBookName());
    }


    public int getBookmarkBegin(int position){
        return bookmarks.get(position).getBegin();
    }

    public void delete(int position){
        Book_dbhelper.getInstance(mcontext).deleteBookmark(bookmarks.get(position));
        bookmarks.remove(position);

    }

    public interface OnItemClickListener{
        void OnItemClick(Bookmark bookmark);
        boolean OnItemLongClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClicklistener){
       this.onItemClicklistener=onItemClicklistener;
    }
    @Override
    public BookmarkViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item,parent,false);
        return new BookmarkViewholder(view);
    }

    @Override
    public void onBindViewHolder(BookmarkViewholder holder, int position) {
        holder.bookmark_time.setText(bookmarks.get(position).getSetmark_time());
        holder.bookmark_hint.setText(bookmarks.get(position).getHint());
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }


    class BookmarkViewholder extends RecyclerView.ViewHolder{
        TextView bookmark_hint,bookmark_time;
        public BookmarkViewholder(View itemView) {
            super(itemView);
            bookmark_hint=(TextView)itemView.findViewById(R.id.bookmark_hint);
            bookmark_time=(TextView)itemView.findViewById(R.id.bookmark_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClicklistener != null) {
                        onItemClicklistener.OnItemClick(bookmarks.get(getAdapterPosition()));
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                   if(onItemClicklistener!=null){
                       return onItemClicklistener.OnItemLongClick(getAdapterPosition());
                   }
                    return false;
                }
            });
        }
    }
}
