package honkot.gscheduler.model;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;

/**
 * Created by hiroki on 2016-11-24.
 */
@Table
public class CompareLocale {
    @Column
    @PrimaryKey
    public String id;

    @Column
    public String displayName;

    @Column
    public String GMT;

    @Column
    public int offset;

    public CompareLocale() {

    }

    public CompareLocale(String id, String displayName, String GMT, int offset) {
        this.id = id;
        this.displayName = displayName;
        this.GMT = GMT;
        this.offset = offset;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGMT() {
        return GMT;
    }

    public void setGMT(String GMT) {
        this.GMT = GMT;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getDisplayCity() { return getDisplayName();}
    public String getDisplayDate() { return GMT;}
    public String getDisplayTime() { return id;}
}
