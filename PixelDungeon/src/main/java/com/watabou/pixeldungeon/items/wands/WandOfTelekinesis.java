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
package com.watabou.pixeldungeon.items.wands;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.effects.MagicMissile;
import com.watabou.pixeldungeon.effects.Pushing;
import com.watabou.pixeldungeon.items.Heap;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.utils.Callback;

public class WandOfTelekinesis extends Wand {

	private static final String TXT_YOU_NOW_HAVE = Game.getVar(R.string.WandOfTelekinesis_YouNowHave);

	{
		hitChars = false;
	}

	@Override
	protected void onZap(int cell) {

		boolean mapUpdated = false;

		int maxDistance = effectiveLevel() + 4;
		Ballistica.distance = Math.min(Ballistica.distance, maxDistance);

		Char ch;
		Heap heap = null;

		for (int i = 1; i < Ballistica.distance; i++) {

			int c = Ballistica.trace[i];

			int before = Dungeon.level.map[c];

			if ((ch = Actor.findChar(c)) != null) {

				if (i == Ballistica.distance - 1) {

					ch.damage(maxDistance - 1 - i, this);

				} else {

					int next = Ballistica.trace[i + 1];
					if ((Dungeon.level.passable[next] || Dungeon.level.avoid[next]) && Actor.findChar(next) == null) {

						Actor.addDelayed(new Pushing(ch, ch.getPos(), next), -1);

						ch.setPos(next);
						Actor.freeCell(next);

						// FIXME
						if (ch instanceof Mob) {
							Dungeon.level.mobPress((Mob) ch);
						} else {
							Dungeon.level.press(ch.getPos(), ch);
						}

					} else {

						ch.damage(maxDistance - 1 - i, this);

					}
				}
			}

			if (heap == null && (heap = Dungeon.level.getHeap(c)) != null) {
				switch (heap.type) {
					case HEAP:
						transport(heap);
						break;
					case CHEST:
					case MIMIC:
						heap.open(getCurUser());
						break;
					default:
				}
			}

			Dungeon.level.press(c, null);
			if (before == Terrain.OPEN_DOOR && Actor.findChar(c) == null) {

				Dungeon.level.set(c, Terrain.DOOR);
				GameScene.updateMap(c);

			} else if (Dungeon.level.water[c]) {

				GameScene.ripple(c);

			}

			if (!mapUpdated && Dungeon.level.map[c] != before) {
				mapUpdated = true;
			}
		}

		if (mapUpdated) {
			Dungeon.observe();
		}
	}

	private void transport(Heap heap) {
		Item item = heap.pickUp();
		item = item.pick(getCurUser(), heap.pos);
		if (item != null) {
			if (item.doPickUp(getCurUser())) {
				getCurUser().itemPickedUp(item);
			} else {
				Dungeon.level.drop(item, getCurUser().getPos()).sprite.drop();
			}
		}
	}

	protected void fx(int cell, Callback callback) {
		MagicMissile.force(wandUser.getSprite().getParent(), wandUser.getPos(), cell, callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.WandOfTelekinesis_Info);
	}
}
