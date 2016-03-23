package com.watabou.pixeldungeon.items.weapon.melee;

import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;

public class WoodenBow extends Bow {

	public WoodenBow() {
		super(1, 0.8f, 1.5f);
		image = ItemSpriteSheet.BOW_WOODEN;
	}

	@Override
	public Item burn(int cell) {
		return null;
	}

	@Override
	public double acuFactor() {
		return 1 + level() * 0.1;
	}

	@Override
	public double dmgFactor() {
		return 1 + level() * 0.25;
	}
}
