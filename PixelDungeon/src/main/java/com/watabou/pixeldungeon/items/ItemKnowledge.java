package com.watabou.pixeldungeon.items;

import java.util.HashSet;

/**
 * Created by Leopoldo on 24/03/2016.
 */
public abstract class ItemKnowledge<T extends Knowable> implements Knowledge<T>{
	private ItemStatusHandler<T> handler;
	protected ItemKnowledge(){}

	public HashSet<Class<? extends T>> getKnown() {
		return handler.known();
	}
	public HashSet<Class<? extends T>> getUnknown() {
		return handler.unknown();
	}

	public ItemStatusHandler<T> getHandler() {
		return handler;
	}

	public boolean isKnown(Class<? extends T> aClass) {
		return handler.isKnown(aClass);
	}
	public void setKnown(Class<? extends T> aClass) {
		handler.know(aClass);
	}
}
