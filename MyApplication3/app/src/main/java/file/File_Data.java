package file;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.administrator.Activity.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/20.
 */
public class File_Data {
    private final Context context;
    private final Activity activity;
    private RecyclerView listfile;
    private File[] files;
    private TextView txt1;
    private String currentpath;
    private File_adapter file_adapter;

    public File_Data(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        txt1 = (TextView) activity.findViewById(R.id.txt1);
    }

    public List init(File f) {
        files = f.listFiles();
        if (files != null) {
            currentpath = f.getPath();
            txt1.setText("当前目录为:" + f.getPath());
            List<File_bean> list = new ArrayList<File_bean>();
            for (int i = 0; i < files.length; i++) {
                File_bean f1 = new File_bean();
                if (files[i].isFile() && files[i].getName().endsWith(".txt")) {
                    f1.setImage_res(R.drawable.file);
                    f1.setFilename(files[i].getName());
                    list.add(f1);
                }
                if (!files[i].isFile()) {
                    f1.setImage_res(R.drawable.folder);
                    f1.setFilename(files[i].getName());
                    list.add(f1);
                }

            }
            return list;
        }
        return null;
    }
    public String getCurrentpath(){
        return  currentpath;
    }
    public void getauthority(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                }
            }

        }
    }

}
