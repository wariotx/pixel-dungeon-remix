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
package com.watabou.pixeldungeon.items.armor.glyphs;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.npcs.MirrorImage;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.armor.Armor.Glyph;
import com.watabou.pixeldungeon.items.wands.WandOfBlink;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Multiplicity extends Glyph {

	private static final String TXT_MULTIPLICITY = Game.getVar(R.string.Multiplicity_Txt);

	private static ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xCCAA88);

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		int level = Math.max(0, armor.level());

		if (Random.Int(level / 2 + 6) >= 5) {

			int imgCell = Dungeon.level.getEmptyCellNextTo(defender.getPos());

			if (Dungeon.level.cellValid(imgCell)) {
				MirrorImage mob = new MirrorImage((Hero) defender);
				Dungeon.level.spawnMob(mob);
				WandOfBlink.appear(mob, imgCell);

				defender.damage(Random.IntRange(1, defender.ht() / 6), /*attacker*/ this);
				checkOwner(defender);
			}

		}

		return damage;
	}

	@Override
	public String name(String weaponName) {
		return String.format(TXT_MULTIPLICITY, weaponName);
	}

	@Override
	public Glowing glowing() {
		return PINK;
	}
}
