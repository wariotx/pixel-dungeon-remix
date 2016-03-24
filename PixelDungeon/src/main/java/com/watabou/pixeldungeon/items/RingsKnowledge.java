package com.watabou.pixeldungeon.items;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.items.rings.*;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

/**
 * Created by Leopoldo on 24/03/2016.
 */
public class RingsKnowledge extends ItemKnowledge<Ring>{
	private static PotionsKnowledge INSTANCE = null;

	static {
		INSTANCE = new PotionsKnowledge();
	}

	private static final Class<?>[] rings = {
		RingOfMending.class,
		RingOfDetection.class,
		RingOfShadows.class,
		RingOfPower.class,
		RingOfHerbalism.class,
		RingOfAccuracy.class,
		RingOfEvasion.class,
		RingOfSatiety.class,
		RingOfHaste.class,
		RingOfHaggler.class,
		RingOfElements.class,
		RingOfThorns.class
	};
	private static final String[] gems = Game.getVars(R.array.Ring_Gems);
	private static final Integer[] images = {
		ItemSpriteSheet.RING_DIAMOND,
		ItemSpriteSheet.RING_OPAL,
		ItemSpriteSheet.RING_GARNET,
		ItemSpriteSheet.RING_RUBY,
		ItemSpriteSheet.RING_AMETHYST,
		ItemSpriteSheet.RING_TOPAZ,
		ItemSpriteSheet.RING_ONYX,
		ItemSpriteSheet.RING_TOURMALINE,
		ItemSpriteSheet.RING_EMERALD,
		ItemSpriteSheet.RING_SAPPHIRE,
		ItemSpriteSheet.RING_QUARTZ,
		ItemSpriteSheet.RING_AGATE
	};

	private ItemStatusHandler<Ring> handler;

	@Override
	public void init() {
		handler = new ItemStatusHandler<>((Class<? extends Ring>[]) rings, gems, images);
	}

	@Override
	public void init(Bundle bundle) {
		handler = new ItemStatusHandler<>((Class<? extends Ring>[]) rings, gems, images, bundle);
	}

	@Override
	public boolean allKnown() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Knowable> Knowledge<T> getInstance() {
		return (Knowledge<T>) INSTANCE;
	}
}
