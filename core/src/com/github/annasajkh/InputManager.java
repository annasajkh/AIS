package com.github.annasajkh;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class InputManager implements InputProcessor
{
	
	float touchX;
	float touchY;

	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		Vector3 pos = Game.camera.unproject(new Vector3(screenX, screenY, 0));
		
		touchX = pos.x;
		touchY = pos.y;
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		Vector3 pos = Game.camera.unproject(new Vector3(screenX, screenY, 0));
		
		Game.camera.position.add(touchX - pos.x,touchY - pos.y,0);
		
		Game.camera.update();
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY)
	{
		
		if(amountY < 0)
		{
			Game.camera.zoom -= Game.zoomSpeed * Game.camera.zoom * Game.zoomFactor;
		}
		else
		{
			Game.camera.zoom += Game.zoomSpeed * Game.camera.zoom * Game.zoomFactor;
		}
		
		Game.camera.zoom = MathUtils.clamp(Game.camera.zoom,0.0001f,Game.maxZoom);
		Game.camera.update();
		
		return true;
	}

}
