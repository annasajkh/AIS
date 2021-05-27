package com.github.annasajkh.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.annasajkh.Game;
import com.github.annasajkh.neuralnetwork.NeuralNetwork;

public class Cell extends Entity
{
	float radius = 10;
	float angleRad;
	float speed;
	float maxSpeed = 1000;
	float minSpeed = 500;

	Color color;
	public NeuralNetwork brain;
	
	public Cell(float x, float y)
	{
		super(x, y);
		color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
		this.brain = new NeuralNetwork(4,5,2,2);
	}
	
	public Cell(float x, float y, NeuralNetwork brain)
	{
		super(x, y);
		color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
		this.brain = brain;
	}
	
	public boolean intersects(Cell other)
	{
		final float dx = other.x - x;
		final float dy = other.y - y;
		
		return (radius + other.radius )* (radius + other.radius) > (dx * dx) + (dy * dy);
	}
	
	@Override
	public void update(float delta)
	{
		float closestDistance2 = Float.MAX_VALUE;
		
		for(Cell cell : Game.cells)
		{
			float distance2 = Vector2.dst2(x,y,cell.x,cell.y);
			
			if(distance2 < closestDistance2)
			{
				closestDistance2 = distance2;
			}	
		}
		
		float[] output = brain.process(new float[]{	NeuralNetwork.sigmoid(closestDistance2), 
													NeuralNetwork.sigmoid(x), 
													NeuralNetwork.sigmoid(y),
													NeuralNetwork.sigmoid(velocity.len2())});
		
		angleRad = MathUtils.map(0, 1, -MathUtils.PI2, MathUtils.PI2, output[0]);
		speed = MathUtils.map(0, 1, minSpeed, maxSpeed, output[1]);
		
		velocity.x = MathUtils.cos(angleRad) * speed;
		velocity.y = MathUtils.sin(angleRad) * speed;
		
		super.update(delta);
	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer)
	{
		shapeRenderer.setColor(color);
		shapeRenderer.circle(x, y, radius);
	}

}
