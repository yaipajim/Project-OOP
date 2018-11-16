package com.neet.bitbeat.states;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.neet.bitbeat.handlers.GameStateManager;

public class Play extends GameState {
	
	private BitmapFont font = new BitmapFont();
	
	public Play(GameStateManager gsm) {
		super(gsm);
	}
	
	public void handleInput() {}
	
	public void update(float dt) {}
	
	public void render() {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		font.draw(sb, "Welcome beat master", 100, 100);
		sb.end();
	}
	
	public void dispose() {}
}
