package Chapters;

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
 * Created by Administrator on 2017/4/18.
 */
public class Chapter_adapter extends RecyclerView.Adapter <Chapter_adapter.ChapterViewHolder>{
    private List<Chapter> chapters = new ArrayList<>();
    private OnItemClickListener mListener;
    private int currentChapter = -1;
    private Context mContext;
    public Chapter_adapter(Context context){
        mContext = context;
        chapters=Book_dbhelper.getInstance(mContext).getChapters(PageFactory.getInstance().getBook().getBookName());
    }

    @Override
    public void onBindViewHolder(ChapterViewHolder holder, int position) {
        holder.text.setText(chapters.get(position).getChapterName());
        if(currentChapter == position){
            holder.text.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }else{
            holder.text.setTextColor(mContext.getResources().getColor(R.color.black));
        }
    }


    public ChapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_item,parent,false);
        return new ChapterViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        public ChapterViewHolder(View view){
            super(view);
           text = (TextView)view.findViewById(R.id.Chapter_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        mListener.onItemClick(chapters.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public void addData(List<Chapter> list){
        chapters.addAll(list);
        notifyDataSetChanged();
    }
    public void clearData(){
        chapters.clear();
    }
    public void setCurrentChapter(int number){
        currentChapter = number;
    }
    public interface OnItemClickListener{
        void onItemClick(Chapter chapter);
    }
}
