package honkot.gscheduler.dao;

import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.model.CompareLocale_Deleter;
import honkot.gscheduler.model.CompareLocale_Relation;
import honkot.gscheduler.model.CompareLocale_Selector;
import honkot.gscheduler.model.OrmaDatabase;

@Singleton
public class CompareLocaleDao {
    OrmaDatabase orma;

    @Inject
    public CompareLocaleDao(OrmaDatabase orma) {
        this.orma = orma;
    }

    public CompareLocale_Relation relation() {
        return orma.relationOfCompareLocale();
    }

    @Nullable
    public CompareLocale findById(long id) {
        return relation().selector().idEq(id).valueOrNull();
    }

    public CompareLocale_Selector findAll() {
        return relation().selector();
    }

    public void changeBasis(final CompareLocale basisLocale) {
        if (basisLocale.getId() > 0) {
            relation().updater().basis(false).execute();
            relation().updater().idEq(basisLocale.getId()).basis(true).execute();
        }
    }

    public CompareLocale getBasisLocale() {
        return relation().selector().basisEq(true).value();
    }

    public void insert(final CompareLocale favorite) {
        orma.transactionNonExclusiveSync(new Runnable() {
            @Override
            public void run() {
                orma.insertIntoCompareLocale(favorite);
            }
        });
    }

    public void remove(final CompareLocale favorite) {
        orma.transactionNonExclusiveSync(new Runnable() {
            @Override
            public void run() {
                CompareLocale_Deleter deleter = relation().deleter();
                deleter.idEq(favorite.getId()).execute();
            }
        });
    }

    public void update(final CompareLocale favorite) {
        orma.transactionNonExclusiveSync(new Runnable() {
            @Override
            public void run() {
                orma.updateCompareLocale()
                        .idEq(favorite.getId())
                        .basis(favorite.isBasis())
                        .gmtId(favorite.getGmtId())
                        .taskName(favorite.getTaskName())
                        .locationName(favorite.getLocationName())
                        .zonedDateTime(favorite.getZonedDateTime())
                        .execute();
            }
        });
    }
}
