package com.github.annasajkh;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.github.annasajkh.entities.Cell;

public class Game extends ApplicationAdapter
{
	ShapeRenderer shapeRenderer;

	public static OrthographicCamera camera;
	
	public static float zoomSpeed = 1;
	public static float zoomFactor = 0.1f;
	public static float maxZoom = 100f;
	public static List<Cell> cells;
	public static float boundryWidth = 2000;
	
	@Override
	public void create()
	{
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.update();
		
		cells = new ArrayList<>();
		
		for(int i = 0; i < 1000; i++)
		{
			cells.add(new Cell(MathUtils.random(-1000,1000),MathUtils.random(-1000, 1000)));
		}
		
		
		shapeRenderer = new ShapeRenderer();
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new InputManager());
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	public void update(float delta)
	{
		for(Cell cell : cells)
		{
			cell.update(delta);
		}
		
		
		List<Cell> cellsToRemove = new ArrayList<>();
		
		for(Cell cell : cells)
		{
			if(cell.x > boundryWidth)
			{
				cellsToRemove.add(cell);
			}
			else if(cell.x < -boundryWidth)
			{
				cellsToRemove.add(cell);
			}
			else if(cell.y > boundryWidth)
			{

				cellsToRemove.add(cell);
			}
			else if(cell.y < -boundryWidth)
			{
				cellsToRemove.add(cell);
			}
			
			for(Cell other : cells)
			{
				if(!cell.equals(other))
				{
					if(cell.intersects(other))
					{
						if(cell.velocity.len2() > other.velocity.len2())
						{
							cellsToRemove.add(other);
						}
						else
						{
							cellsToRemove.add(cell);
						}
						
					}
				}
			}
		}
		
		cells.removeAll(cellsToRemove);
		
		if(cells.size() <= 10)
		{
			List<Cell> temp = new ArrayList<>();
			
			for(int k = 0; k < cells.size(); k++)
			{
				for(int l = 0; l < 50; l++)
				{
					temp.add(new Cell(	MathUtils.random(-1000,1000),
										MathUtils.random(-1000, 1000),
										cells.get(k).brain.mutate(0.1f)));
				}
			}
			
			for(int i = 0; i < 500; i++)
			{
				temp.add(new Cell(MathUtils.random(-1000,1000), MathUtils.random(-1000, 1000)));
			}
			cells = temp;
		}

	}
	
	@Override
	public void render()
	{
		update(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		
		for(Cell cell : cells)
		{
			cell.draw(shapeRenderer);
		}
		
		shapeRenderer.end();
	}

	@Override
	public void dispose()
	{
		shapeRenderer.dispose();
	}
}
