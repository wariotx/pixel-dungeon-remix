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
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.effects.SpellSprite;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.levels.TerrainFlags;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.utils.GLog;

public class ScrollOfMagicMapping extends Scroll {

	private static final String TXT_LAYOUT = Game.getVar(R.string.ScrollOfMagicMapping_Layout);

	@Override
	protected void doRead() {

		int length = Dungeon.level.getLength();
		int[] map = Dungeon.level.map;
		boolean[] mapped = Dungeon.level.mapped;
		boolean[] discoverable = Dungeon.level.discoverable;

		boolean noticed = false;

		for (int i = 0; i < length; i++) {

			int terr = map[i];

			if (discoverable[i]) {

				mapped[i] = true;
				if ((TerrainFlags.flags[terr] & TerrainFlags.SECRET) != 0) {

					Dungeon.level.set(i, Terrain.discover(terr));
					GameScene.updateMap(i);

					if (Dungeon.visible[i]) {
						GameScene.discoverTile(i, terr);
						discover(i);

						noticed = true;
					}
				}
			}
		}
		Dungeon.observe();

		GLog.i(TXT_LAYOUT);
		if (noticed) {
			Sample.INSTANCE.play(Assets.SND_SECRET);
		}

		SpellSprite.show(getCurUser(), SpellSprite.MAP);
		Sample.INSTANCE.play(Assets.SND_READ);
		Invisibility.dispel(getCurUser());

		setKnown();

		getCurUser().spendAndNext(TIME_TO_READ);
	}

	@Override
	public int price() {
		return isKnown() ? 25 * quantity() : super.price();
	}

	public static void discover(int cell) {
		CellEmitter.get(cell).start(Speck.factory(Speck.DISCOVER), 0.1f, 4);
	}
}
