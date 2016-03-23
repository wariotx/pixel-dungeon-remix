package com.watabou.pixeldungeon.items;

/**
 * Created by Leopoldo on 23/03/2016.
 */
public abstract class KnowableItem extends Item implements Knowable{

	public abstract boolean isKnown();
	public abstract void setKnown();
	@Override
	public boolean isIdentified() {
		return isKnown();
	}

	@Override
	public Item identify() {
		setKnown();
		return this;
	}
}
