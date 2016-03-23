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
import com.watabou.noosa.Text;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.weapon.Weapon;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.pixeldungeon.windows.IconTitle;
import com.watabou.pixeldungeon.windows.WndBag;

import java.util.ArrayList;

public class Weightstone extends Item {

	private static final String TXT_SELECT_WEAPON = Game.getVar(R.string.Weightstone_Select);
	private static final String TXT_FAST = Game.getVar(R.string.Weightstone_Fast);
	private static final String TXT_ACCURATE = Game.getVar(R.string.Weightstone_Accurate);

	private static final float TIME_TO_APPLY = 2;

	private static final String AC_APPLY = Game.getVar(R.string.Weightstone_ACApply);

	{
		name = Game.getVar(R.string.Weightstone_Name);
		image = ItemSpriteSheet.WEIGHT;

		stackable = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action == AC_APPLY) {

			setCurUser(hero);
			GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, TXT_SELECT_WEAPON);

		} else {

			super.execute(hero, action);

		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	private void apply(Weapon weapon, boolean forSpeed) {

		detach(getCurUser().belongings.backpack);

		if (forSpeed) {
			weapon.imbue = Weapon.Imbue.SPEED;
			GLog.p(TXT_FAST, weapon.name());
		} else {
			weapon.imbue = Weapon.Imbue.ACCURACY;
			GLog.p(TXT_ACCURATE, weapon.name());
		}

		getCurUser().getSprite().operate(getCurUser().getPos());
		Sample.INSTANCE.play(Assets.SND_MISS);

		getCurUser().spend(TIME_TO_APPLY);
		getCurUser().busy();
	}

	@Override
	public int price() {
		return 40 * quantity();
	}

	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {
				GameScene.show(new WndBalance((Weapon) item));
			}
		}
	};

	public class WndBalance extends Window {

		private final String TXT_CHOICE = Game.getVar(R.string.Weightstone_WndChoice);

		private final String TXT_SPEED = Game.getVar(R.string.Weightstone_WndSpeed);
		private final String TXT_ACCURACY = Game.getVar(R.string.Weightstone_WndAccuracy);
		private final String TXT_CANCEL = Game.getVar(R.string.Weightstone_WndCancel);

		private static final int WIDTH = 120;
		private static final int MARGIN = 2;
		private static final int BUTTON_WIDTH = WIDTH - MARGIN * 2;
		private static final int BUTTON_HEIGHT = 20;

		public WndBalance(final Weapon weapon) {
			super();

			IconTitle titlebar = new IconTitle(weapon);
			titlebar.setRect(0, 0, WIDTH, 0);
			add(titlebar);

			Text tfMesage = PixelScene.createMultiline(Utils.format(TXT_CHOICE, weapon.name()), 8);
			tfMesage.maxWidth(WIDTH - MARGIN * 2);
			tfMesage.measure();
			tfMesage.x = MARGIN;
			tfMesage.y = titlebar.bottom() + MARGIN;
			add(tfMesage);

			float pos = tfMesage.y + tfMesage.height();

			if (weapon.imbue != Weapon.Imbue.SPEED) {
				RedButton btnSpeed = new RedButton(TXT_SPEED) {
					@Override
					protected void onClick() {
						hide();
						Weightstone.this.apply(weapon, true);
					}
				};
				btnSpeed.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
				add(btnSpeed);

				pos = btnSpeed.bottom();
			}

			if (weapon.imbue != Weapon.Imbue.ACCURACY) {
				RedButton btnAccuracy = new RedButton(TXT_ACCURACY) {
					@Override
					protected void onClick() {
						hide();
						Weightstone.this.apply(weapon, false);
					}
				};
				btnAccuracy.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
				add(btnAccuracy);

				pos = btnAccuracy.bottom();
			}

			RedButton btnCancel = new RedButton(TXT_CANCEL) {
				@Override
				protected void onClick() {
					hide();
				}
			};
			btnCancel.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
			add(btnCancel);

			resize(WIDTH, (int) btnCancel.bottom() + MARGIN);
		}

		protected void onSelect(int index) {
		}
	}
}
