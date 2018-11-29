package com.neet.bitbeat.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.neet.bitbeat.handlers.GameStateManager;
import com.neet.bitbeat.main.Game;

public abstract class GameState {
	
	protected Texture background, background2, background3;
	
	protected GameStateManager gsm;
	protected Game game;
	
	protected SpriteBatch sb;
	protected OrthographicCamera cam;
	protected OrthographicCamera hudCam;
	protected Music levelSong = Gdx.audio.newMusic(Gdx.files.internal("res/music/Eye Of the Tiger.mp3"));
	protected int currentSong = 0;
	protected TiledMap tileMap;
	
	protected GameState(GameStateManager gsm) {
		
		setState();
		setSong(gsm.PLAY);
		
		this.gsm = gsm;
		game = gsm.game();
		sb = game.getSpriteBatch();
		cam = game.getCamera();
		hudCam = game.getHUDCamera();
	}

	public abstract void handleInput();
	public abstract void update(float dt);
	public abstract void render();
	public abstract void dispose();
	
	public void setSong(int i) {
        //nothing to do, the song we want to load is already loaded
		if (currentSong == i) return;
		if (levelSong != null && currentSong != i){ //a different song is loaded
	        levelSong.stop(); //stop it
	        levelSong.dispose();
	        levelSong = null;
		}

        //load the appropriate file
		switch (i){
			case 0:
				levelSong = Gdx.audio.newMusic(Gdx.files.internal("res/music/Eye Of the Tiger.mp3"));
				break;
			case 1:
				levelSong = Gdx.audio.newMusic(Gdx.files.internal("res/music/Eye Of the Tiger.mp3"));
				break;
			case 6:
				levelSong = Gdx.audio.newMusic(Gdx.files.internal("res/music/Very.mp3"));
				break;
			case 7:
				levelSong = Gdx.audio.newMusic(Gdx.files.internal("res/music/4walls.mp3"));
				break;
			case 8:
				levelSong = Gdx.audio.newMusic(Gdx.files.internal("res/music/Silence.mp3"));
		}
		levelSong.setVolume(0.3f);

        //remember the song we loaded
		currentSong = i;
	}
	
	public void playMusic() {
		levelSong.play();
		
	}
	
	public void setState() {
		switch (gsm.PLAY){
			case 0:
				background = new Texture("res/map/itf.jpg");
				background2 = new Texture("res/map/itf.jpg");
				background3 = new Texture("res/map/itf.jpg");
				tileMap = new TmxMapLoader().load("res/map/itf.tmx");
				break;
			case 1:
				background = new Texture("res/map/itf.jpg");
				background2 = new Texture("res/map/itf.jpg");
				background3 = new Texture("res/map/itf.jpg");
				tileMap = new TmxMapLoader().load("res/map/itf.tmx");
				break;
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				background = new Texture("res/map/normal1.jpg");
				background2 = new Texture("res/map/normal2.jpg");
				background3 = new Texture("res/map/normal3.jpg");
				tileMap = new TmxMapLoader().load("res/map/normal1.tmx");
				break;
			case 7:
				background = new Texture("res/map/easy1.jpg");
				background2 = new Texture("res/map/easy2.jpg");
				background3 = new Texture("res/map/easy3.jpg");
				tileMap = new TmxMapLoader().load("res/map/easy1.tmx");
				break;
			case 8:
				background = new Texture("res/map/6-1.jpg");
				background2 = new Texture("res/map/6-2.jpg");
				background3 = new Texture("res/map/6-3.jpg");
				tileMap = new TmxMapLoader().load("res/map/hard1.tmx");
				break;
		}
	}
	
}
