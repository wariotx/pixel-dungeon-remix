package com.watabou.pixeldungeon.items.weapon.melee;

import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;

public class CompoundBow extends Bow {

	public CompoundBow() {
		super(3, 0.8f, 1.5f);
		image = ItemSpriteSheet.BOW_COMPOUND;
	}

	@Override
	public Item burn(int cell) {
		return null;
	}

	@Override
	public double acuFactor() {
		return 1 + level() * 0.2;
	}

	@Override
	public double dmgFactor() {
		return 1 + level() * 0.5;
	}

	public double dlyFactor() {
		return 1.1;
	}
}
