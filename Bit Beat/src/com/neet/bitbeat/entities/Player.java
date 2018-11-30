package com.neet.bitbeat.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.neet.bitbeat.main.Game;

public class Player extends B2DSprite {
	
	private int numCrystals = 0;
	private int totalCrystals;
	
	public Player(Body body, int i) {
		
		super(body);
		
		Texture tex = Game.res.geTexture("easy1");
		TextureRegion[] sprites = TextureRegion.split(tex, 32, 32)[0];
		
		setAnimation(sprites, 1 / 9f);
	}
	
	public void cellectCrystal() { numCrystals++; }
	public int getNumCrystals() { return numCrystals; }
	public void setTotalCrystals(int i) { totalCrystals = i; }
	public int getTotalCrystals() { return totalCrystals; }

}
