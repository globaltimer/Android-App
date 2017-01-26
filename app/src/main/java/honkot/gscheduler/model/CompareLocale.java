package honkot.gscheduler.model;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;

import java.util.HashMap;

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
}
