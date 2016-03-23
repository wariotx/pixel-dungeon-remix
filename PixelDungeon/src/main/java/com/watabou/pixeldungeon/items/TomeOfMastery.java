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
package com.watabou.pixeldungeon.items;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.actors.buffs.Blindness;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroSubClass;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.effects.SpellSprite;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.pixeldungeon.windows.WndChooseWay;

import java.util.ArrayList;

public class TomeOfMastery extends Item {

	private static final String TXT_BLINDED = Game.getVar(R.string.TomeOfMastery_Blinded);

	public static final float TIME_TO_READ = 10;

	public static final String AC_READ = Game.getVar(R.string.TomeOfMastery_ACRead);

	{
		stackable = false;
		image = ItemSpriteSheet.MASTERY;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_READ);

		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_READ)) {

			if (hero.buff(Blindness.class) != null) {
				GLog.w(TXT_BLINDED);
				return;
			}

			setCurUser(hero);

			HeroSubClass way1 = null;
			HeroSubClass way2 = null;
			switch (hero.heroClass) {
				case WARRIOR:
					way1 = HeroSubClass.GLADIATOR;
					way2 = HeroSubClass.BERSERKER;
					break;
				case MAGE:
					way1 = HeroSubClass.BATTLEMAGE;
					way2 = HeroSubClass.WARLOCK;
					break;
				case ROGUE:
					way1 = HeroSubClass.FREERUNNER;
					way2 = HeroSubClass.ASSASSIN;
					break;
				case HUNTRESS:
					way1 = HeroSubClass.SNIPER;
					way2 = HeroSubClass.WARDEN;
					break;
				case ELF:
					way1 = HeroSubClass.SCOUT;
					way2 = HeroSubClass.SHAMAN;
					break;
			}
			GameScene.show(new WndChooseWay(this, way1, way2));

		} else {

			super.execute(hero, action);

		}
	}

	@Override
	public boolean doPickUp(Hero hero) {
		Badges.validateMastery();
		return super.doPickUp(hero);
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	public void choose(HeroSubClass way) {

		detach(getCurUser().belongings.backpack);

		getCurUser().subClass = way;

		getCurUser().getSprite().operate(getCurUser().getPos());
		Sample.INSTANCE.play(Assets.SND_MASTERY);

		SpellSprite.show(getCurUser(), SpellSprite.MASTERY);
		getCurUser().getSprite().emitter().burst(Speck.factory(Speck.MASTERY), 12);
		GLog.w(Game.getVar(R.string.TomeOfMastery_Choose), Utils.capitalize(way.title()));

		getCurUser().checkIfFurious();
		GameScene.updateHeroSprite(getCurUser());

		getCurUser().spendAndNext(TomeOfMastery.TIME_TO_READ);
		getCurUser().busy();


	}
}
