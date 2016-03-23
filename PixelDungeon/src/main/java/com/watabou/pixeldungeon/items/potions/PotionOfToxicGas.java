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
package com.watabou.pixeldungeon.items.potions;

import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.blobs.ToxicGas;
import com.watabou.pixeldungeon.items.weapon.missiles.Arrow;
import com.watabou.pixeldungeon.items.weapon.missiles.PoisonArrow;
import com.watabou.pixeldungeon.scenes.GameScene;

public class PotionOfToxicGas extends Potion {


	@Override
	public void shatter(int cell) {

		setKnown();

		splash(cell);
		Sample.INSTANCE.play(Assets.SND_SHATTER);

		ToxicGas gas = Blob.seed(cell, 1000, ToxicGas.class);
		Actor.add(gas);
		GameScene.add(gas);
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.PotionOfToxicGas_Info);
	}

	@Override
	public int price() {
		return isKnown() ? 40 * quantity() : super.price();
	}

	@Override
	protected void moistenArrow(Arrow arrow) {
		int quantity = reallyMoistArrows(arrow);

		PoisonArrow moistenArrows = new PoisonArrow(quantity);
		getCurUser().collect(moistenArrows);
	}
}
