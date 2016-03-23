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
import com.watabou.pixeldungeon.ResultDescriptions;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.blobs.Fire;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Burning;
import com.watabou.pixeldungeon.effects.MagicMissile;
import com.watabou.pixeldungeon.effects.particles.FlameParticle;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandOfFirebolt extends SimpleWand {

	@Override
	protected void onZap(int cell) {

		int level = effectiveLevel();

		for (int i = 1; i < Ballistica.distance - 1; i++) {
			int c = Ballistica.trace[i];
			if (Dungeon.level.flammable[c]) {
				GameScene.add(Blob.seed(c, 1, Fire.class));
			}
		}

		GameScene.add(Blob.seed(cell, 1, Fire.class));

		Char ch = Actor.findChar(cell);
		if (ch != null) {

			ch.damage(Random.Int(1, 8 + level * level), this);
			Buff.affect(ch, Burning.class).reignite(ch);

			ch.getSprite().emitter().burst(FlameParticle.FACTORY, 5);

			if (ch == getCurUser() && !ch.isAlive()) {
				Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth));
				GLog.n(Game.getVar(R.string.WandOfFirebolt_Info1));
			}
		}
	}

	protected void fx(int cell, Callback callback) {
		MagicMissile.fire(wandUser.getSprite().getParent(), wandUser.getPos(), cell, callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.WandOfFirebolt_Info);
	}
}
