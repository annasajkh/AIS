package com.github.annasajkh.entities;

import com.badlogic.gdx.math.Vector2;
import com.github.annasajkh.GameObject;

public abstract class Entity extends GameObject
{
	public Vector2 velocity;
	float speed = 100;
	

	public Entity(float x, float y)
	{
		super(x, y);
		velocity = new Vector2();
	}
	
	@Override
	public void update(float delta)
	{
		x += velocity.x * delta;
		y += velocity.y * delta;
		

	}
	
	

}
