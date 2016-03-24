package com.watabou.pixeldungeon.items;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.items.wands.*;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

/**
 * Created by Leopoldo on 24/03/2016.
 */
public final class WandsKnowledge extends ItemKnowledge<Wand> {
	private static WandsKnowledge INSTANCE = null;

	static {
		INSTANCE = new WandsKnowledge();
	}

	private static final Class<?>[] wands = {
		WandOfTeleportation.class,
		WandOfSlowness.class, WandOfFirebolt.class, WandOfPoison.class,
		WandOfRegrowth.class, WandOfBlink.class, WandOfLightning.class,
		WandOfAmok.class, WandOfTelekinesis.class, WandOfFlock.class,
		WandOfDisintegration.class, WandOfAvalanche.class
	};
	private static final String[] woods = Game.getVars(R.array.Wand_Wood_Types);
	private static final Integer[] images = {
		ItemSpriteSheet.WAND_HOLLY,
		ItemSpriteSheet.WAND_YEW, ItemSpriteSheet.WAND_EBONY,
		ItemSpriteSheet.WAND_CHERRY, ItemSpriteSheet.WAND_TEAK,
		ItemSpriteSheet.WAND_ROWAN, ItemSpriteSheet.WAND_WILLOW,
		ItemSpriteSheet.WAND_MAHOGANY, ItemSpriteSheet.WAND_BAMBOO,
		ItemSpriteSheet.WAND_PURPLEHEART, ItemSpriteSheet.WAND_OAK,
		ItemSpriteSheet.WAND_BIRCH
	};

	private ItemStatusHandler<Wand> handler;

	@Override
	public void init() {
		handler = new ItemStatusHandler<>((Class<? extends Wand>[]) wands, woods, images);
	}

	@Override
	public void init(Bundle bundle) {
		handler.save(bundle);
	}

	@Override
	public boolean allKnown() {
		return handler.known().size() == wands.length;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Knowable> Knowledge<T> getInstance() {
		return (Knowledge<T>) INSTANCE;
	}
}
