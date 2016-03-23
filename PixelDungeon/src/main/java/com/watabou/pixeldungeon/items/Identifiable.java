package com.watabou.pixeldungeon.items;

public interface Identifiable<T> {
	boolean isIdentified();
	T identify();
}
