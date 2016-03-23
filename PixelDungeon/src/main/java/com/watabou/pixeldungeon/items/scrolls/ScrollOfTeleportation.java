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

import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.buffs.Invisibility;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.wands.WandOfBlink;
import com.watabou.pixeldungeon.utils.GLog;

public class ScrollOfTeleportation extends Scroll {

	public static final String TXT_TELEPORTED = Game.getVar(R.string.ScrollOfTeleportation_Teleport);
	public static final String TXT_NO_TELEPORT = Game.getVar(R.string.ScrollOfTeleportation_NoTeleport);

	@Override
	protected void doRead() {

		Sample.INSTANCE.play(Assets.SND_READ);
		Invisibility.dispel(getCurUser());

		teleportHero(getCurUser());
		setKnown();

		getCurUser().spendAndNext(TIME_TO_READ);
	}

	public static void teleportHero(Hero hero) {

		int pos = Dungeon.level.randomRespawnCell();

		if (!Dungeon.level.cellValid(pos)) {

			GLog.w(TXT_NO_TELEPORT);

		} else {

			WandOfBlink.appear(hero, pos);
			Dungeon.level.press(pos, hero);
			Dungeon.observe();

			GLog.i(TXT_TELEPORTED);

		}
	}

	@Override
	public int price() {
		return isKnown() ? 40 * quantity() : super.price();
	}
}
