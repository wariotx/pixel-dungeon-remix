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
package com.watabou.pixeldungeon.items.weapon.missiles;

import com.watabou.noosa.Game;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Cripple;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Javelin extends MissileWeapon {

	public Javelin() {
		this(1);
	}

	public Javelin(int number) {
		super();

		image = ItemSpriteSheet.JAVELIN;

		STR = 15;

		MIN = 2;
		MAX = 15;

		quantity(number);
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		super.proc(attacker, defender, damage);
		Buff.prolong(defender, Cripple.class, Cripple.DURATION);
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.Javelin_Info);
	}

	@Override
	public Item random() {
		quantity(Random.Int(5, 15));
		return this;
	}

	@Override
	public int price() {
		return 15 * quantity();
	}
}
