package Books;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/27.
 */
public class Book implements Serializable {//书的bean类，Implements Serializable类是为了intent之间传递Book对象。
    private String name, path, encoding;
    private long accessTime = 0;

    public Book() {
    }

    public Book(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void setBookName(String name) {
        this.name = name;
    }

    public String getBookName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }


    public void setAccessTime(long time) {
        accessTime = time;
    }

    public long getAccessTime() {
        return accessTime;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Book) {
            Book book = (Book) o;
            return book.getBookName().equals(this.name) && book.getPath().equals(this.path);
        } else {
            return super.equals(o);
        }
    }
}
