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
package com.watabou.pixeldungeon.actors.mobs;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.blobs.Fire;
import com.watabou.pixeldungeon.actors.blobs.ToxicGas;
import com.watabou.pixeldungeon.actors.buffs.Amok;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Burning;
import com.watabou.pixeldungeon.actors.buffs.Charm;
import com.watabou.pixeldungeon.actors.buffs.Ooze;
import com.watabou.pixeldungeon.actors.buffs.Poison;
import com.watabou.pixeldungeon.actors.buffs.Sleep;
import com.watabou.pixeldungeon.actors.buffs.Terror;
import com.watabou.pixeldungeon.effects.Pushing;
import com.watabou.pixeldungeon.effects.particles.ShadowParticle;
import com.watabou.pixeldungeon.items.keys.SkeletonKey;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.watabou.pixeldungeon.items.weapon.enchantments.Death;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.BurningFistSprite;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.sprites.LarvaSprite;
import com.watabou.pixeldungeon.sprites.RottingFistSprite;
import com.watabou.pixeldungeon.sprites.YogSprite;
import com.watabou.utils.Random;

public class Yog extends Boss {

	private static final String TXT_DESC = Game.getVar(R.string.Yog_Desc);

	public Yog() {
		spriteClass = YogSprite.class;

		hp(ht(300));

		EXP = 50;

		state = PASSIVE;

		IMMUNITIES.add(Death.class);
		IMMUNITIES.add(Terror.class);
		IMMUNITIES.add(Amok.class);
		IMMUNITIES.add(Charm.class);
		IMMUNITIES.add(Sleep.class);
		IMMUNITIES.add(Burning.class);
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(ScrollOfPsionicBlast.class);
	}

	public void spawnFists() {
		RottingFist fist1 = new RottingFist();
		BurningFist fist2 = new BurningFist();

		do {
			fist1.setPos(getPos() + Level.NEIGHBOURS8[Random.Int(8)]);
			fist2.setPos(getPos() + Level.NEIGHBOURS8[Random.Int(8)]);
		} while (!Dungeon.level.passable[fist1.getPos()] || !Dungeon.level.passable[fist2.getPos()] || fist1.getPos() == fist2.getPos());

		Dungeon.level.spawnMob(fist1);
		Dungeon.level.spawnMob(fist2);
	}

	@Override
	public void damage(int dmg, Object src) {

		int damageShift = 0;
		for (Mob mob : Dungeon.level.mobs) {
			if (mob instanceof BurningFist || mob instanceof RottingFist) {
				mob.beckon(getPos());
				damageShift++;
			}
		}

		dmg >>= damageShift;

		super.damage(dmg, src);
	}

	@Override
	public int defenseProc(Char enemy, int damage) {

		int larvaPos = Dungeon.level.getEmptyCellNextTo(getPos());
		
		if (Dungeon.level.cellValid(larvaPos)) {
			Larva larva = new Larva();
			larva.setPos(larvaPos);
			Dungeon.level.spawnMob(larva, 0);
			Actor.addDelayed(new Pushing(larva, getPos(), larva.getPos()), -1);
		}

		return super.defenseProc(enemy, damage);
	}

	@Override
	public void beckon(int cell) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void die(Object cause) {

		for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
			if (mob instanceof BurningFist || mob instanceof RottingFist) {
				mob.die(cause);
			}
		}

		GameScene.bossSlain();
		Dungeon.level.drop(new SkeletonKey(), getPos()).sprite.drop();
		super.die(cause);

		yell(Game.getVar(R.string.Yog_Info1));
	}

	@Override
	public void notice() {
		super.notice();
		yell(Game.getVar(R.string.Yog_Info2));
	}

	@Override
	public String description() {
		return TXT_DESC;
	}

	public static class RottingFist extends Boss {

		private static final int REGENERATION = 4;

		{
			spriteClass = RottingFistSprite.class;

			hp(ht(300));
			defenseSkill = 25;

			EXP = 0;

			state = WANDERING;

			RESISTANCES.add(ToxicGas.class);

			IMMUNITIES.add(Amok.class);
			IMMUNITIES.add(Sleep.class);
			IMMUNITIES.add(Terror.class);
			IMMUNITIES.add(Poison.class);
		}

		public RottingFist() {
		}

		@Override
		public void die(Object cause) {
			super.die(cause);
		}

		@Override
		public int attackSkill(Char target) {
			return 36;
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(24, 36);
		}

		@Override
		public int dr() {
			return 15;
		}

		@Override
		public int attackProc(Char enemy, int damage) {
			if (Random.Int(3) == 0) {
				Buff.affect(enemy, Ooze.class);
				enemy.getSprite().burst(0xFF000000, 5);
			}

			return damage;
		}

		@Override
		public boolean act() {

			if (Dungeon.level.water[getPos()] && hp() < ht()) {
				getSprite().emitter().burst(ShadowParticle.UP, 2);
				hp(hp() + REGENERATION);
			}

			return super.act();
		}

		@Override
		public String description() {
			return TXT_DESC;
		}
	}

	public static class BurningFist extends Boss {

		{
			spriteClass = BurningFistSprite.class;

			hp(ht(200));
			defenseSkill = 25;

			EXP = 0;

			state = WANDERING;

			RESISTANCES.add(ToxicGas.class);

			IMMUNITIES.add(Amok.class);
			IMMUNITIES.add(Sleep.class);
			IMMUNITIES.add(Terror.class);
			IMMUNITIES.add(Burning.class);
		}

		public BurningFist() {
		}

		@Override
		public void die(Object cause) {
			super.die(cause);
		}

		@Override
		public int attackSkill(Char target) {
			return 36;
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(20, 32);
		}

		@Override
		public int dr() {
			return 15;
		}

		@Override
		protected boolean canAttack(Char enemy) {
			return Ballistica.cast(getPos(), enemy.getPos(), false, true) == enemy.getPos();
		}

		@Override
		public boolean attack(Char enemy) {

			if (!Dungeon.level.adjacent(getPos(), enemy.getPos())) {
				spend(attackDelay());

				if (hit(this, enemy, true)) {

					int dmg = damageRoll();
					enemy.damage(dmg, this);

					enemy.getSprite().bloodBurstA(getSprite().center(), dmg);
					enemy.getSprite().flash();

					return true;
				} else {

					enemy.getSprite().showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
					return false;
				}
			} else {
				return super.attack(enemy);
			}
		}

		@Override
		public boolean act() {

			for (int i = 0; i < Level.NEIGHBOURS9.length; i++) {
				GameScene.add(Blob.seed(getPos() + Level.NEIGHBOURS9[i], 2, Fire.class));
			}

			return super.act();
		}

		@Override
		public String description() {
			return TXT_DESC;
		}
	}

	public static class Larva extends Mob {

		{
			spriteClass = LarvaSprite.class;

			hp(ht(25));
			defenseSkill = 20;

			EXP = 0;

			state = HUNTING;
		}

		@Override
		public int attackSkill(Char target) {
			return 30;
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(15, 20);
		}

		@Override
		public int dr() {
			return 8;
		}

		@Override
		public String description() {
			return TXT_DESC;

		}
	}
}
