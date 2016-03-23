package com.watabou.pixeldungeon.items;

import com.watabou.utils.Bundle;

import java.util.HashSet;

/**
 * Created by Leopoldo on 23/03/2016.
 */
public interface Knowledge<T extends KnowableItem> {
	void init();

	void init(Bundle bundle);

	HashSet<Class<? extends T>> getKnown();

	HashSet<Class<? extends T>> getUnknown();

	boolean allKnown();

	ItemStatusHandler<T> getHandler();

	boolean isKnown(Class<? extends T> aClass);

	void setKnown(Class<? extends T> aClass);
}
