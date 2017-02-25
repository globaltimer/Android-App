package honkot.gscheduler.dao;

import android.support.annotation.Nullable;

import com.github.gfx.android.orma.Inserter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import honkot.gscheduler.model.OrmaDatabase;
import honkot.gscheduler.model.TmpTimeZone;
import honkot.gscheduler.model.TmpTimeZone_Relation;
import honkot.gscheduler.model.TmpTimeZone_Schema;
import honkot.gscheduler.model.TmpTimeZone_Selector;

@Singleton
public class TmpTimeZoneDao {
    OrmaDatabase orma;

    @Inject
    public TmpTimeZoneDao(OrmaDatabase orma) {
        this.orma = orma;
    }

    public TmpTimeZone_Relation relation() {
        return orma.relationOfTmpTimeZone();
    }

    @Nullable
    public TmpTimeZone findByLocaleId(String localeId) {
        return relation().selector().localeIdEq(localeId).valueOrNull();
    }

    public TmpTimeZone_Selector likeQuery(String query) {
        return relation().selector().where(
                TmpTimeZone_Schema.INSTANCE.name.getEscapedName() + " LIKE ?", "%" + query + "%")
                .orderByNameAsc();
    }

    public TmpTimeZone_Selector findAll() {
        return relation().selector().idNotEq(0).orderByNameAsc();
    }

    public void insert(final List<TmpTimeZone> tmpTimeZones) {
        orma.transactionNonExclusiveSync(new Runnable() {
            @Override
            public void run() {
                Inserter<TmpTimeZone> sth = orma.prepareInsertIntoTmpTimeZone();
                for (TmpTimeZone tmpTimeZone : tmpTimeZones) {
                    sth.execute(tmpTimeZone);
                }
            }
        });
    }

    public void deleteAll() {
        orma.transactionNonExclusiveSync(new Runnable() {
            @Override
            public void run() {
                relation().deleter().execute();
            }
        });
    }
}
