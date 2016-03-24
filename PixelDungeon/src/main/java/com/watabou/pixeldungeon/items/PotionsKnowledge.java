package com.watabou.pixeldungeon.items;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.items.potions.*;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

import java.util.HashSet;

/**
 * Created by Leopoldo on 23/03/2016.
 */
public final class PotionsKnowledge extends ItemKnowledge<Potion> {
	private static PotionsKnowledge INSTANCE = null;

	static {
		INSTANCE = new PotionsKnowledge();
	}

	private static final Class<?>[] potions = {
			PotionOfHealing.class,
			PotionOfExperience.class,
			PotionOfToxicGas.class,
			PotionOfLiquidFlame.class,
			PotionOfStrength.class,
			PotionOfParalyticGas.class,
			PotionOfLevitation.class,
			PotionOfMindVision.class,
			PotionOfPurity.class,
			PotionOfInvisibility.class,
			PotionOfMight.class,
			PotionOfFrost.class
	};

	private static String[] colors = null;

	private static final Integer[] images = {
			ItemSpriteSheet.POTION_TURQUOISE,
			ItemSpriteSheet.POTION_CRIMSON,
			ItemSpriteSheet.POTION_AZURE,
			ItemSpriteSheet.POTION_JADE,
			ItemSpriteSheet.POTION_GOLDEN,
			ItemSpriteSheet.POTION_MAGENTA,
			ItemSpriteSheet.POTION_CHARCOAL,
			ItemSpriteSheet.POTION_IVORY,
			ItemSpriteSheet.POTION_AMBER,
			ItemSpriteSheet.POTION_BISTRE,
			ItemSpriteSheet.POTION_INDIGO,
			ItemSpriteSheet.POTION_SILVER
	};

	@SuppressWarnings("unchecked")
	public void init() {
		handler = new ItemStatusHandler<>((Class<? extends Potion>[]) potions, getColors(), images);
	}

	@SuppressWarnings("unchecked")
	public void init(Bundle bundle) {
		handler = new ItemStatusHandler<>((Class<? extends Potion>[]) potions, getColors(), images, bundle);
	}

	public boolean allKnown() {
		return handler.known().size() == potions.length;
	}

	private String[] getColors() {
		if (colors == null) {
			colors = Game.getVars(R.array.Potion_Colors);
		}
		return colors;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Knowable> Knowledge<T> getInstance() {
		return (Knowledge<T>) INSTANCE;
	}
}
