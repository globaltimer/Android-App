package honkot.gscheduler.model;

import android.provider.BaseColumns;

import com.android.annotations.NonNull;
import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.Getter;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Setter;
import com.github.gfx.android.orma.annotation.Table;

/**
 * Created by hhonda_admin on 2017-02-07.
 */

@Table
public class TmpTimeZone implements ListBindingInterface {
    @Column(value = BaseColumns._ID)
    @PrimaryKey(autoincrement = true)
    private long id;

    @NonNull
    @Column(indexed = true)
    private String name;

    @NonNull
    @Column(indexed = true)
    private String gmt;

    @NonNull
    @Column(indexed = true)
    private String localeId;

    @NonNull
    @Column
    private int offset;

    public TmpTimeZone() {
        this("", "", "", 0);
    }

    public TmpTimeZone(String name, String gmt, String localeId, int offset) {
        this.name = name;
        this.gmt = gmt;
        this.localeId = localeId;
        this.offset = offset;
    }

    @Getter
    public long getId() {
        return id;
    }

    @Setter
    public void setId(long id) {
        this.id = id;
    }

    @Getter
    public String getName() {
        return name;
    }

    @Setter
    public void setName(String name) {
        this.name = name;
    }

    @Getter
    public String getGmt() {
        return gmt;
    }

    @Setter
    public void setGmt(String gmt) {
        this.gmt = gmt;
    }

    @Getter
    public String getLocaleId() {
        return localeId;
    }

    @Setter
    public void setLocaleId(String localeId) {
        this.localeId = localeId;
    }

    @Getter
    public int getOffset() {
        return offset;
    }

    @Setter
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String getDisplayCity() { return getName();}

    @Override
    public String getDisplayDate() { return getGmt();} // be used as gmt

    @Override
    public String getDisplayTime() { return "";} // does not be used.

    @Override
    public String getDisplayGMT() { return "";} // does not be used.

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TmpTimeZone{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", gmt='").append(gmt).append('\'');
        sb.append(", localeId='").append(localeId).append('\'');
        sb.append(", offset=").append(offset);
        sb.append('}');
        return sb.toString();
    }
}
