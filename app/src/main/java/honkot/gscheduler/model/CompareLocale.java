package honkot.gscheduler.model;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.Getter;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Setter;
import com.github.gfx.android.orma.annotation.Table;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.TimeZone;

import honkot.gscheduler.utils.AdapterGenerater;

@Table
public class CompareLocale implements ListBindingInterface {

    @Column(value = BaseColumns._ID)
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    @NonNull
    private boolean basis;

    @Column
    @NonNull
    private String gmtId;

    @Column
    @NonNull
    private String taskName;

    @Column
    @NonNull
    private String locationName;

    @Column
    @NonNull
    private ZonedDateTime zonedDateTime;

    public CompareLocale() {

    }

    public CompareLocale(Context context) {
        this.basis = false;
        String gmt = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        this.zonedDateTime = ZonedDateTime.now(ZoneId.of(gmt))
                    .withSecond(0).withNano(0);
        this.gmtId = AdapterGenerater.getIdByGMT(context, gmt);
        this.taskName = "";
        this.locationName = gmtId;
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
    @NonNull
    public boolean isBasis() {
        return basis;
    }

    @Setter
    public void setBasis(@NonNull boolean basis) {
        this.basis = basis;
    }

    @Getter
    @NonNull
    public String getGmtId() {
        return gmtId;
    }

    @Setter
    public void setGmtId(@NonNull String gmtId) {
        this.gmtId = gmtId;
    }

    @Getter
    @NonNull
    public String getTaskName() {
        return taskName;
    }

    @Setter
    public void setTaskName(@NonNull String taskName) {
        this.taskName = taskName;
    }

    @Getter
    @NonNull
    public String getLocationName() {
        return locationName;
    }

    @Setter
    public void setLocationName(@NonNull String locationName) {
        this.locationName = locationName;
    }

    @Getter
    @NonNull
    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    @Setter
    public void setZonedDateTime(@NonNull ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    @Override
    public String getDisplayCity() { return getLocationName();}

    @Override
    public String getDisplayDate() { return zonedDateTime.toLocalDate().toString();}

    @Override
    public String getDisplayTime() { return zonedDateTime.toLocalTime().toString();}

    @Override
    public String getDisplayGMT() { return zonedDateTime.getZone().toString();}

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CompareLocale{");
        sb.append("id=").append(id);
        sb.append(", basis=").append(basis);
        sb.append(", gmtId='").append(gmtId).append('\'');
        sb.append(", taskName='").append(taskName).append('\'');
        sb.append(", locationName='").append(locationName).append('\'');
        sb.append(", zonedDateTime=").append(zonedDateTime);
        sb.append('}');
        return sb.toString();
    }
}
