package com.neet.bitbeat.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.neet.bitbeat.handlers.Content;
import com.neet.bitbeat.handlers.GameStateManager;
import com.neet.bitbeat.handlers.MyInput;
import com.neet.bitbeat.handlers.MyInputProcessor;

public class Game implements ApplicationListener{
	
	public static final String TITLE = "Bit Beat";
	public static final int V_WIDTH = 640;
	public static final int V_HEIGHT = 480;
	public static final int SCALE = 2;
	
	public static final float STEP = 1 / 120f;
	private float accum;
	
	private SpriteBatch sb;
	private Texture bg;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;
	
	private GameStateManager gsm;
	
	public static Content res;
	
	public void create() {
		
		Gdx.graphics.setVSync(true);
		Gdx.input.setInputProcessor(new MyInputProcessor());
		
		res = new Content();
		res.loadTexture("res/character/character4.png", "normal1");
		res.loadTexture("res/character/character3.png", "easy1");
		res.loadTexture("res/object/coin.png", "crystal");
		res.loadTexture("res/object/number.png", "hub");
		res.loadTexture("res/map/start.png", "start");
		res.loadTexture("res/map/op2.png", "bitbeat");

		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		
		gsm = new GameStateManager(this);
		
	}
	
	public void render() {
		
		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP) {
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
		}
		
	}
	
	public void dispose() {
		
	}
	
	public SpriteBatch getSpriteBatch() { return sb; }
	public OrthographicCamera getCamera() { return cam; }
	public OrthographicCamera getHUDCamera() { return hudCam; }
	
	public void resize(int w, int h) {}
	public void pause() {}
	public void resume() {}

}
