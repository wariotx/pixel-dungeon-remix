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
package com.watabou.pixeldungeon.items.food;

import com.watabou.noosa.Game;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Burning;
import com.watabou.pixeldungeon.actors.buffs.Hunger;
import com.watabou.pixeldungeon.actors.buffs.Paralysis;
import com.watabou.pixeldungeon.actors.buffs.Poison;
import com.watabou.pixeldungeon.actors.buffs.Roots;
import com.watabou.pixeldungeon.actors.buffs.Slow;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class MysteryMeat extends Food {

	{
		image = ItemSpriteSheet.MEAT;
		energy = Hunger.STARVING - Hunger.HUNGRY;
		message = Game.getVar(R.string.MysteryMeat_Message);
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_EAT)) {

			switch (Random.Int(5)) {
				case 0:
					GLog.w(Game.getVar(R.string.MysteryMeat_Info1));
					Buff.affect(hero, Burning.class).reignite(hero);
					break;
				case 1:
					GLog.w(Game.getVar(R.string.MysteryMeat_Info2));
					Buff.prolong(hero, Roots.class, Paralysis.duration(hero));
					break;
				case 2:
					GLog.w(Game.getVar(R.string.MysteryMeat_Info3));
					Buff.affect(hero, Poison.class).set(Poison.durationFactor(hero) * hero.ht() / 5);
					break;
				case 3:
					GLog.w(Game.getVar(R.string.MysteryMeat_Info4));
					Buff.prolong(hero, Slow.class, Slow.duration(hero));
					break;
			}
		}
	}

	public int price() {
		return 5 * quantity();
	}

	@Override
	public Item burn(int cell) {
		return morphTo(ChargrilledMeat.class);
	}

	@Override
	public Item freeze(int cell) {
		return morphTo(FrozenCarpaccio.class);
	}

	@Override
	public Item poison(int cell) {
		return morphTo(RottenMeat.class);
	}
}
