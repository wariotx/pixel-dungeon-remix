package com.watabou.pixeldungeon.items;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.items.scrolls.*;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

import java.util.HashSet;

/**
 * Created by Leopoldo on 23/03/2016.
 */
public final class ScrollsKnowledge extends ItemKnowledge<Scroll> {
	private static ScrollsKnowledge INSTANCE = null;

	static {
		INSTANCE = new ScrollsKnowledge();
	}

	private ItemStatusHandler<Scroll> handler;

	private final Class<?>[] scrolls = {
			ScrollOfIdentify.class,
			ScrollOfMagicMapping.class,
			ScrollOfRecharging.class,
			ScrollOfRemoveCurse.class,
			ScrollOfTeleportation.class,
			ScrollOfUpgrade.class,
			ScrollOfChallenge.class,
			ScrollOfTerror.class,
			ScrollOfLullaby.class,
			ScrollOfWeaponUpgrade.class,
			ScrollOfPsionicBlast.class,
			ScrollOfMirrorImage.class,
			ScrollOfDomination.class,
			ScrollOfCurse.class
	};
	private String[] runes = null;
	private final Integer[] images = {
			ItemSpriteSheet.SCROLL_KAUNAN,
			ItemSpriteSheet.SCROLL_SOWILO,
			ItemSpriteSheet.SCROLL_LAGUZ,
			ItemSpriteSheet.SCROLL_YNGVI,
			ItemSpriteSheet.SCROLL_GYFU,
			ItemSpriteSheet.SCROLL_RAIDO,
			ItemSpriteSheet.SCROLL_ISAZ,
			ItemSpriteSheet.SCROLL_MANNAZ,
			ItemSpriteSheet.SCROLL_NAUDIZ,
			ItemSpriteSheet.SCROLL_BERKANAN,
			ItemSpriteSheet.SCROLL_ODAL,
			ItemSpriteSheet.SCROLL_TIWAZ,
			ItemSpriteSheet.SCROLL_ANSUZ,
			ItemSpriteSheet.SCROLL_IWAZ,
			ItemSpriteSheet.SCROLL_ALGIZ,
			ItemSpriteSheet.SCROLL_DAGAZ
	};

	@SuppressWarnings("unchecked")
	public void init() {
		handler = new ItemStatusHandler<>((Class<? extends Scroll>[]) scrolls, getRunes(), images);
	}

	@SuppressWarnings("unchecked")
	public void init(Bundle bundle) {
		handler = new ItemStatusHandler<>((Class<? extends Scroll>[]) scrolls, getRunes(), images, bundle);
	}

	public boolean allKnown() {
		return handler.known().size() == scrolls.length;
	}

	private String[] getRunes() {
		if (runes == null) {
			runes = Game.getVars(R.array.Scroll_Runes);
		}
		return runes;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Knowable> Knowledge<T> getInstance() {
		return (Knowledge<T>) INSTANCE;
	}
}
