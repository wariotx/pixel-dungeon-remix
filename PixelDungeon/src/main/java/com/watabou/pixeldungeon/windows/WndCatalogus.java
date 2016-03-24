/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
package com.watabou.pixeldungeon.windows;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.noosa.Text;
import com.watabou.noosa.ui.Component;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.Knowable;
import com.watabou.pixeldungeon.items.PotionsKnowledge;
import com.watabou.pixeldungeon.items.scrolls.Scroll;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.ui.ScrollPane;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.pixeldungeon.windows.elements.LabeledTab;
import com.watabou.pixeldungeon.windows.elements.Tab;

import java.util.ArrayList;

public class WndCatalogus extends WndTabbed {
	
	private static final int WIDTH_P	= 112;
	private static final int HEIGHT_P	= 160;
	
	private static final int WIDTH_L	= 128;
	private static final int HEIGHT_L	= 128;
	
	private static final int ITEM_HEIGHT	= 18;
	
	private static final int TAB_WIDTH		= 50;
	
	private static final String TXT_POTIONS	= Game.getVar(R.string.WndCatalogus_Potions);
	private static final String TXT_SCROLLS	= Game.getVar(R.string.WndCatalogus_Scrolls);
	private static final String TXT_TITLE	= Game.getVar(R.string.WndCatalogus_Title);
	
	private Text txtTitle;
	private ScrollPane list;
	
	private ArrayList<ListItem> items = new ArrayList<>();
	
	private static boolean showPotions = true;
	
	public WndCatalogus() {
		
		super();
		
		if (PixelDungeon.landscape()) {
			resize( WIDTH_L, HEIGHT_L );
		} else {
			resize( WIDTH_P, HEIGHT_P );
		}
		
		txtTitle = PixelScene.createText( TXT_TITLE, 9 );
		txtTitle.hardlight( Window.TITLE_COLOR );
		txtTitle.measure();
		add( txtTitle );
		
		list = new ScrollPane( new Component() ) {
			@Override
			public void onClick( float x, float y ) {
				int size = items.size();
				for (int i=0; i < size; i++) {
					if (items.get( i ).onClick( x, y )) {
						break;
					}
				}
			}
		};
		add( list );
		list.setRect( 0, txtTitle.height(), width, height - txtTitle.height() );
		
		boolean showPotions = WndCatalogus.showPotions;
		Tab[] tabs = {
			new LabeledTab(this , TXT_POTIONS ) {
				public void select( boolean value ) {
					super.select( value );
					WndCatalogus.showPotions = value;
					updateList();
				}
			},
			new LabeledTab(this,  TXT_SCROLLS ) {
				public void select( boolean value ) {
					super.select( value );
					WndCatalogus.showPotions = !value;
					updateList();
				}
			}
		};
		for (Tab tab : tabs) {
			tab.setSize( TAB_WIDTH, tabHeight() );
			add( tab );
		}
		
		select( showPotions ? 0 : 1 );
	}
	
	private void updateList() {
		
		txtTitle.text( Utils.format( TXT_TITLE, showPotions ? TXT_POTIONS : TXT_SCROLLS ) );
		txtTitle.measure();
		txtTitle.x = PixelScene.align( PixelScene.uiCamera, (width - txtTitle.width()) / 2 );
		
		items.clear();
		
		Component content = list.content();
		content.clear();
		list.scrollTo( 0, 0 );
		
		float pos = 0;
		for (Class<? extends Knowable> itemClass : showPotions ? PotionsKnowledge.getInstance().getKnown() : Scroll.getKnowledge().getKnown()) {
			ListItem item = new ListItem( itemClass.asSubclass(Item.class) );
			item.setRect( 0, pos, width, ITEM_HEIGHT );
			content.add( item );
			items.add( item );
			
			pos += item.height();
		}
		
		for (Class<? extends Knowable> itemClass : showPotions ? PotionsKnowledge.getInstance().getUnknown() : Scroll.getKnowledge().getUnknown()) {
			ListItem item = new ListItem( itemClass.asSubclass(Item.class) );
			item.setRect( 0, pos, width, ITEM_HEIGHT );
			content.add( item );
			items.add( item );
			
			pos += item.height();
		}
		
		content.setSize( width, pos );
	}
	
	private static class ListItem extends Component {
		
		private Item item;
		private boolean identified;
		
		private ItemSprite sprite;
		private Text label;
		
		public ListItem( Class<? extends Item> cl ) {
			super();
			
			try {
				item = cl.newInstance();
				if (identified = item.isIdentified()) {
					sprite.view(item.imageFile(), item.image(), null );
					label.text( item.name() );
				} else {
					sprite.view(Assets.ITEMS, 127, null );
					label.text( item.trueName() );
					label.hardlight( 0xCCCCCC );
				}
			} catch (Exception e) {
				// Do nothing
			}
		}
		
		@Override
		protected void createChildren() {
			sprite = new ItemSprite();
			add( sprite );
			
			label = PixelScene.createText( 8 );
			add( label );
		}
		
		@Override
		protected void layout() {
			sprite.y = PixelScene.align( y + (height - sprite.height) / 2 );
			
			label.x = sprite.x + sprite.width;
			label.y = PixelScene.align( y + (height - label.baseLine()) / 2 );
		}
		
		public boolean onClick( float x, float y ) {
			if (identified && inside( x, y )) {
				GameScene.show( new WndInfoItem( item ) );
				return true;
			} else {
				return false;
			}
		}
	}
}
