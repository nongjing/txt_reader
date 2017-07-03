package Page;

import android.content.Context;
import android.widget.Toast;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import Books.Book;

/**
 * Created by Administrator on 2017/4/11.
 */
public class Util {
    private static Toast mToast;
    public static String getEncoding(Book book){
        UniversalDetector detector = new UniversalDetector(null);
        byte[] bytes = new byte[1024];
        try{
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(book.getPath())));
            int length;
            while ((length = bufferedInputStream.read(bytes)) > 0){
                detector.handleData(bytes,0,length);
            }
            detector.dataEnd();
            bufferedInputStream.close();
        }catch (FileNotFoundException f){
            f.printStackTrace();
        }catch (IOException i){
            i.printStackTrace();
        }
        return detector.getDetectedCharset();
    }
    public static int getPXWithDP(int dp, Context context){
        float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int)(dp*density);
    }
}
