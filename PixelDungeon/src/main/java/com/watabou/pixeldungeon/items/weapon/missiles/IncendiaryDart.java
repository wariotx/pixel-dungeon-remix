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
import com.watabou.pixeldungeon.Dungeon;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.blobs.Fire;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Burning;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class IncendiaryDart extends MissileWeapon {

	public IncendiaryDart() {
		this(1);
	}

	public IncendiaryDart(int number) {
		super();

		image = ItemSpriteSheet.INCENDIARY_DART;

		STR = 12;

		MIN = 1;
		MAX = 2;

		quantity(number);
	}

	@Override
	protected void onThrow(int cell) {
		Char enemy = Actor.findChar(cell);
		if (enemy == null || enemy == getCurUser()) {
			if (Dungeon.level.flammable[cell]) {
				GameScene.add(Blob.seed(cell, 4, Fire.class));
			} else {
				super.onThrow(cell);
			}
		} else {
			if (!getCurUser().shoot(enemy, this)) {
				Dungeon.level.drop(this, cell).sprite.drop();
			}
		}
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Burning.class).reignite(defender);
		super.proc(attacker, defender, damage);
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.IncendiaryDart_Info);
	}

	@Override
	public Item random() {
		quantity(Random.Int(3, 6));
		return this;
	}

	@Override
	public int price() {
		return 10 * quantity();
	}
}
