package file;

import android.media.Image;
import android.widget.ImageView;


import java.io.File;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/22.
 */
public class File_bean implements Serializable {
    private Integer image_res;
    private String filename;
    public File_bean(){
    }
    public File_bean(String filename,Integer image_res ){
        this.filename=filename;
        this.image_res=image_res;
    }
    public void setImage_res(Integer image_res){
        this.image_res=image_res;
    }
    public void setFilename(String filename){
        this.filename=filename;
    }
    public Integer getImage_res(){
        return image_res;
    }

    public String getFilename() {
        return filename;
    }
    public boolean equals(Object o) {
        if (o instanceof File_bean) {
            File_bean f1 = (File_bean) o;
            return f1.getFilename().equals(this.filename) && f1.getImage_res().equals(this.image_res);
        } else {
            return super.equals(o);
        }
    }

}
