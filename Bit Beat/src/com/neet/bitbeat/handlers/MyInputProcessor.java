package com.neet.bitbeat.handlers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter{
	
	public boolean keyDown(int k) {
		if (k == Keys.SPACE) {
			MyInput.setKey(MyInput.BUTTON1, true);
		}
		return true;
	}
	
	public boolean keyUp(int k) {
		if (k == Keys.SPACE) {
			MyInput.setKey(MyInput.BUTTON1, false);
		}
		return true;
	}
	

}
