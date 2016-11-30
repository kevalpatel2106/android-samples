package example.calandersync.pojo;

import android.support.annotation.ColorInt;

/**
 * Created by multidots on 6/30/2016.
 */
public class CalenderPojo {

    private String name;
    private long id;
    private int color;
    private String displayName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CalenderPojo(){
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
