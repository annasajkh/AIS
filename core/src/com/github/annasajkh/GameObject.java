package com.github.annasajkh;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class GameObject
{
	public float x, y;
	
	public GameObject(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public abstract void update(float delta);
	
	public abstract void draw(ShapeRenderer shapeRenderer);
	
	
	

}
