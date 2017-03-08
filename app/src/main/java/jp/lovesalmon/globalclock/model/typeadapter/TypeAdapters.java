package jp.lovesalmon.globalclock.model.typeadapter;

import com.github.gfx.android.orma.annotation.StaticTypeAdapter;
import com.github.gfx.android.orma.annotation.StaticTypeAdapters;

import org.threeten.bp.ZonedDateTime;

@StaticTypeAdapters({
        @StaticTypeAdapter(
                targetType = ZonedDateTime.class,
                serializedType = String.class,
                serializer = "serializeZonedDateTime",
                deserializer = "deserializeZonedDateTime"
        )
})
public class TypeAdapters {

    public static String serializeZonedDateTime(ZonedDateTime time) {
        return time.toString();
    }

    public static ZonedDateTime deserializeZonedDateTime(String serialized) {
        return ZonedDateTime.parse(serialized);
    }

}
