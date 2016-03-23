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
import com.watabou.pixeldungeon.actors.buffs.Barkskin;
import com.watabou.pixeldungeon.actors.buffs.Bleeding;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Cripple;
import com.watabou.pixeldungeon.actors.buffs.Hunger;
import com.watabou.pixeldungeon.actors.buffs.Invisibility;
import com.watabou.pixeldungeon.actors.buffs.Poison;
import com.watabou.pixeldungeon.actors.buffs.Weakness;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class FrozenCarpaccio extends Food {

	{
		image = ItemSpriteSheet.CARPACCIO;
		energy = Hunger.STARVING - Hunger.HUNGRY;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_EAT)) {

			switch (Random.Int(5)) {
				case 0:
					GLog.i(Game.getVar(R.string.FrozenCarpaccio_Info1));
					Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
					break;
				case 1:
					GLog.i(Game.getVar(R.string.FrozenCarpaccio_Info2));
					Buff.affect(hero, Barkskin.class).level(hero.ht() / 4);
					break;
				case 2:
					GLog.i(Game.getVar(R.string.FrozenCarpaccio_Info3));
					Buff.detach(hero, Poison.class);
					Buff.detach(hero, Cripple.class);
					Buff.detach(hero, Weakness.class);
					Buff.detach(hero, Bleeding.class);
					break;
				case 3:
					GLog.i(Game.getVar(R.string.FrozenCarpaccio_Info4));
					if (hero.hp() < hero.ht()) {
						hero.hp(Math.min(hero.hp() + hero.ht() / 4, hero.ht()));
						hero.getSprite().emitter().burst(Speck.factory(Speck.HEALING), 1);
					}
					break;
			}
		}
	}

	public int price() {
		return 10 * quantity();
	}

	@Override
	public Item burn(int cell) {
		return morphTo(MysteryMeat.class);
	}
}
