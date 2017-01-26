package honkot.gscheduler.dao;

import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.model.CompareLocale_Deleter;
import honkot.gscheduler.model.CompareLocale_Relation;
import honkot.gscheduler.model.CompareLocale_Selector;
import honkot.gscheduler.model.OrmaDatabase;

/**
 * Created by hiroki on 2016-11-25.
 */
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
    public CompareLocale findById(String id) {
        return relation().selector().idEq(id).getOrNull(0);
    }

    public CompareLocale_Selector findAll() {
        return relation().selector();
    }

    public void insert(final CompareLocale favorite) {
        if (findById(favorite.id) == null) {
            orma.transactionSync(new Runnable() {
                @Override
                public void run() {
                    orma.insertIntoCompareLocale(favorite);
                }
            });
        }
    }

    public void remove(final CompareLocale favorite) {
        orma.transactionSync(new Runnable() {
            @Override
            public void run() {
                CompareLocale_Deleter deleter = relation().deleter();
                deleter.idEq(favorite.id).execute();
            }
        });
    }

    /**
     * Does not be used. just practice.
     * @param favorite
     */
    public void update(final CompareLocale favorite) {
        orma.transactionSync(new Runnable() {
            @Override
            public void run() {
                orma.updateCompareLocale()
                        .idEq(favorite.id)
                        .displayName(favorite.displayName)
                        .GMT(favorite.GMT)
                        .offset(favorite.offset)
                        .execute();
            }
        });
    }
}
