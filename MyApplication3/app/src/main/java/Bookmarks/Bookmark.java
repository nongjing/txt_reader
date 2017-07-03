package Bookmarks;

/**
 * Created by Administrator on 2017/4/17.
 */
public class Bookmark {//标签的bean类。
    private String bookname, hint;
    Integer begin;
    String setmark_time;

    public Bookmark() {
        super();
    }
    public Bookmark(String name,String hint,Integer begin,String setmark_time){
        this.bookname=name;
        this.hint=hint;
        this.begin=begin;
        this.setmark_time=setmark_time;
    }

    public Integer getBegin() {
        return begin;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getSetmark_time() {
        return setmark_time;
    }

    public void setSetmark_time(String setmark_time) {
        this.setmark_time = setmark_time;
    }

    public String getName() {
        return bookname;
    }

    public void setName(String name) {
        this.bookname = name;
    }
}
