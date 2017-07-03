package file;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.Activity.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/22.
 */
public class File_adapter extends RecyclerView.Adapter<File_adapter.file_items> implements
        View.OnClickListener {
    private List<File_bean> items;
    private OnRecyclerViewItemClickListener mORVC = null;

    public File_adapter(List<File_bean> mdata) {
        this.items = mdata;
    }

    @Override
    public File_adapter.file_items onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.files_item, null);
        itemView.setOnClickListener(this);
        return new file_items(itemView);
    }
    public void onClick(View v) {
        if (mORVC != null) {
            mORVC.onItemClick(v);
        }
    }

    @Override
    public void onBindViewHolder(File_adapter.file_items holder, int position) {
        final File_bean f1 = items.get(position);
        holder.image.setImageResource(f1.getImage_res());
        holder.filename.setText(f1.getFilename());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mORVC = listener;
    }

    public final class file_items extends RecyclerView.ViewHolder {
        ImageView image;
        TextView filename;

        public file_items(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.images);
            filename = (TextView) itemView.findViewById(R.id.txtview);
        }
    }
}

