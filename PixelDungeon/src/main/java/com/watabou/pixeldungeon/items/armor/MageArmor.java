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
package com.watabou.pixeldungeon.items.armor;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Burning;
import com.watabou.pixeldungeon.actors.buffs.Roots;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.effects.particles.ElmoParticle;
import com.watabou.pixeldungeon.utils.GLog;

public class MageArmor extends ClassArmor {

	private static final String AC_SPECIAL = Game.getVar(R.string.MageArmor_ACSpecial);

	private static final String TXT_NOT_MAGE = Game.getVar(R.string.MageArmor_NotMage);

	{
		image = 11;
	}

	@Override
	public String special() {
		return AC_SPECIAL;
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.MageArmor_Desc);
	}

	@Override
	public void doSpecial() {

		for (Mob mob : Dungeon.level.mobs) {
			if (Dungeon.level.fieldOfView[mob.getPos()]) {
				Buff.affect(mob, Burning.class).reignite(mob);
				Buff.prolong(mob, Roots.class, 3);
			}
		}

		getCurUser().hp(getCurUser().hp() - (getCurUser().hp() / 3));

		getCurUser().spend(Actor.TICK);
		getCurUser().getSprite().operate(getCurUser().getPos());
		getCurUser().busy();

		getCurUser().getSprite().centerEmitter().start(ElmoParticle.FACTORY, 0.15f, 4);
		Sample.INSTANCE.play(Assets.SND_READ);
	}

	@Override
	public boolean doEquip(Hero hero) {
		if (hero.heroClass == HeroClass.MAGE) {
			return super.doEquip(hero);
		} else {
			GLog.w(TXT_NOT_MAGE);
			return false;
		}
	}
}