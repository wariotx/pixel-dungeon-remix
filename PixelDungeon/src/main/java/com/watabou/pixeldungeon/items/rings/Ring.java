/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.items.rings;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.items.*;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Ring extends Artifact implements Knowable{

	private static final String TXT_IDENTIFY = Game.getVar(R.string.Ring_Identify);

	private String gem;

	private int ticksToKnow = 200;

	@SuppressWarnings("unchecked")
	public static void initGems() {
		getKnowledge().init();
	}

	public static void save(Bundle bundle) {
		getKnowledge().getHandler().save(bundle);
	}

	@SuppressWarnings("unchecked")
	public static void restore(Bundle bundle) {
		getKnowledge().init(bundle);
	}

	public Ring() {
		super();
		syncGem();
	}

	public void syncGem() {
		image = getKnowledge().getHandler().image(this);
		gem = getKnowledge().getHandler().label(this);
	}

	@Override
	public Item upgrade() {

		super.upgrade();

		if (buff != null) {

			Char owner = buff.target;
			buff.detach();
			if ((buff = buff()) != null) {
				buff.attachTo(owner);
			}
		}

		return this;
	}

	@Override
	public boolean isKnown() {
		return getKnowledge().isKnown( this.getClass() );
	}

	@Override
	public void setKnown() {
		if (!isKnown()) {
			getKnowledge().setKnown( this.getClass() );
		}

		Badges.validateAllRingsIdentified();
	}

	@Override
	public String name() {
		return isKnown() ? name : String.format(Game.getVar(R.string.Ring_Name), gem);
	}

	@Override
	public String desc() {
		return String.format(Game.getVar(R.string.Ring_Info), gem);
	}

	@Override
	public String info() {
		if (isEquipped(Dungeon.hero)) {
			return String.format(Game.getVar(R.string.Ring_Info3a), desc(), name(), (cursed ? Game.getVar(R.string.Ring_Info3b) : "."));
		} else if (cursed && cursedKnown) {
			return String.format(Game.getVar(R.string.Ring_Info4), desc(), name());
		} else {
			return desc();
		}
	}

	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown();
	}

	@Override
	public boolean isUpgradable() {
		return true;
	}

	@Override
	public Item identify() {
		setKnown();
		return super.identify();
	}

	@Override
	public Item random() {
		level(Random.Int(1, 3));
		if (Random.Float() < 0.3f) {
			level(-level());
			cursed = true;
		}
		return this;
	}

	@Override
	public int price() {
		int price = 80;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level() > 0) {
				price *= (level() + 1);
			} else if (level() < 0) {
				price /= (1 - level());
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public class RingBuff extends ArtifactBuff {

		private final String TXT_KNOWN = Game.getVar(R.string.Ring_BuffKnown);

		public int level;

		public RingBuff() {
			level = Ring.this.level();
		}

		@Override
		public boolean attachTo(Char target) {

			if (target instanceof Hero && ((Hero) target).heroClass == HeroClass.ROGUE && !isKnown()) {
				setKnown();
				GLog.i(TXT_KNOWN, name());
				Badges.validateItemLevelAquired(Ring.this);
			}

			return super.attachTo(target);
		}

		@Override
		public boolean act() {

			if (!isIdentified() && --ticksToKnow <= 0) {
				String gemName = name();
				identify();
				GLog.w(TXT_IDENTIFY, gemName, Ring.this.toString());
				Badges.validateItemLevelAquired(Ring.this);
			}

			spend(TICK);

			return true;
		}
	}

	private static RingKnowledge INSTANCE = null;
	static {
		INSTANCE = new RingKnowledge();
	}
	@SuppressWarnings("unchecked")
	public static <T extends Knowable> Knowledge<T> getKnowledge() {
		return (Knowledge<T>) INSTANCE;
	}

	public static class RingKnowledge extends ItemKnowledge<Ring> {

		private ItemStatusHandler<Ring> handler;

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
			return handler.known().size() == rings.length - 2;
		}
	}
}
