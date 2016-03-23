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
package com.watabou.pixeldungeon.items.scrolls;

import java.util.ArrayList;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Badges;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.buffs.Blindness;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.KnowableItem;
import com.watabou.pixeldungeon.items.ScrollsKnowledge;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public abstract class Scroll extends KnowableItem {

	private static final String TXT_BLINDED = Game.getVar(R.string.Scroll_Blinded);

	public static final String AC_READ = Game.getVar(R.string.Scroll_ACRead);

	protected static final float TIME_TO_READ = 1f;

	private static final Class<?>[] inscribableScrolls = {
			ScrollOfIdentify.class,
			ScrollOfMagicMapping.class,
			ScrollOfRecharging.class,
			ScrollOfRemoveCurse.class,
			ScrollOfTeleportation.class,
			ScrollOfUpgrade.class,
			ScrollOfChallenge.class,
			ScrollOfTerror.class,
			ScrollOfLullaby.class,
			ScrollOfPsionicBlast.class,
			ScrollOfMirrorImage.class,
			ScrollOfDomination.class,
			ScrollOfCurse.class
	};

	private String rune;

	@SuppressWarnings("unchecked")
	public static void initLabels() {
		ScrollsKnowledge.getInstance().init();
	}

	public static void save(Bundle bundle) {
		ScrollsKnowledge.getInstance().getHandler().save(bundle);
	}

	@SuppressWarnings("unchecked")
	public static void restore(Bundle bundle) {
		ScrollsKnowledge.getInstance().init(bundle);
	}

	public Scroll() {
		stackable = true;
		defaultAction = AC_READ;

		if (this instanceof BlankScroll) {
			return;
		}

		image = ScrollsKnowledge.getInstance().getHandler().image(this);
		rune = ScrollsKnowledge.getInstance().getHandler().label(this);
	}

	static public Scroll createRandomScroll() {
		try {
			return (Scroll) Random.element(inscribableScrolls).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_READ);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_READ)) {

			if (hero.buff(Blindness.class) != null) {
				GLog.w(TXT_BLINDED);
			} else {
				setCurUser(hero);
				curItem = detach(hero.belongings.backpack);

				doRead();
			}

		} else {

			super.execute(hero, action);

		}
	}

	abstract protected void doRead();

	public boolean isKnown() {
		return ScrollsKnowledge.getInstance().isKnown(this.getClass());
	}

	public void setKnown() {
		if (!isKnown()) {
			ScrollsKnowledge.getInstance().setKnown(this.getClass());
		}

		Badges.validateAllScrollsIdentified();
	}

	@Override
	public String name() {
		return isKnown() ? name : String.format(Game.getVar(R.string.Scroll_Name), rune);
	}

	@Override
	public String info() {
		return isKnown() ? desc() : String.format(Game.getVar(R.string.Scroll_Info), rune);
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int price() {
		return 15 * quantity();
	}

	@Override
	public Item burn(int cell) {
		return null;
	}
}
