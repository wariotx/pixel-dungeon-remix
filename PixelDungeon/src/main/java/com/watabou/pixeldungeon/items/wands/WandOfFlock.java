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

import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.MagicMissile;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.SheepSprite;
import com.watabou.pixeldungeon.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfFlock extends SimpleWand {

	@Override
	protected void onZap(int cell) {
		int level = effectiveLevel();

		int n = level + 2;

		if (Actor.findChar(cell) != null && Ballistica.distance > 2) {
			cell = Ballistica.trace[Ballistica.distance - 2];
		}

		boolean[] passable = BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null);
		for (Actor actor : Actor.all()) {
			if (actor instanceof Char) {
				passable[((Char) actor).getPos()] = false;
			}
		}

		PathFinder.buildDistanceMap(cell, passable, n);
		int dist = 0;

		if (Actor.findChar(cell) != null) {
			PathFinder.distance[cell] = Integer.MAX_VALUE;
			dist = 1;
		}

		float lifespan = level + 3;

		sheepLabel:
		for (int i = 0; i < n; i++) {
			do {
				for (int j = 0; j < Dungeon.level.getLength(); j++) {
					if (PathFinder.distance[j] == dist) {

						Sheep sheep = new Sheep();
						sheep.lifespan = lifespan;
						sheep.setPos(j);
						Dungeon.level.spawnMob(sheep);
						Dungeon.level.mobPress(sheep);

						CellEmitter.get(j).burst(Speck.factory(Speck.WOOL), 4);

						PathFinder.distance[j] = Integer.MAX_VALUE;

						continue sheepLabel;
					}
				}
				dist++;
			} while (dist < n);
		}
	}

	protected void fx(int cell, Callback callback) {
		MagicMissile.wool(wandUser.getSprite().getParent(), wandUser.getPos(), cell, callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.WandOfFlock_Info);
	}

	public static class Sheep extends NPC {

		Sheep() {
			super();
			spriteClass = SheepSprite.class;
		}

		private static final String[] QUOTES = Game.getVars(R.array.WandOfFlock_SheepBaa);

		public float lifespan;

		private boolean initialized = false;

		@Override
		protected boolean act() {
			if (initialized) {
				hp(0);

				destroy();
				getSprite().die();

			} else {
				initialized = true;
				spend(lifespan + Random.Float(2));
			}
			return true;
		}

		@Override
		public void damage(int dmg, Object src) {
		}

		@Override
		public boolean interact(final Hero hero) {
			say(Random.element(QUOTES));
			return false;
		}
	}
}
