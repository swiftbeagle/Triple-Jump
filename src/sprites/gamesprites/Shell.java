package sprites.gamesprites;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import main.Game;
import main.LevelManager;
import main.Main;
import main.ScaleManager;
import main.SpriteSheetController;
import main.resources.images.ResourcePath;
import sprites.GameSprite;
import sprites.Player;
import sprites.Tile;
import sprites.blocks.particles.EnemyDeath;
import util.DelayTrigger;

public class Shell extends GameSprite implements Hitable {

	protected double vX;
	protected double vY;
	boolean dead;
	boolean active;
	BufferedImage img = ResourcePath.getImage(t);
	
	public Shell(String type, int x, int y) {
		super(type, x, y);
		vX = -2;
		vY = 0;
		animationSetup(6,1);
		frame = 2;
		frameDelay = 10;
		dead = true;
		active = true;
		displacementY = -15;
		size = 40;
		// TODO Auto-generated constructor stub
	}
	
	public void update() {
		super.update();
		
		if (dead) {
			
			if (!Game.currentPlayer.deathAnimationPlaying && screenX + size > Game.currentPlayer.getX() && screenX < Game.currentPlayer.getX() + Game.currentPlayer.width && screenY + size > Game.currentPlayer.getY() && screenY < Game.currentPlayer.getY() + Game.currentPlayer.length) {
				if (Player.buffType == 6 && Game.currentPlayer.mode == 10 && Game.currentPlayer.velX != 0) {
					hitUp();
				} else
				if (Game.currentPlayer.getY() + Game.currentPlayer.length <= screenY + 10) {
					if (Game.currentPlayer.mode == 9) {
						hitUp();
					} else {
						Player.velY = -2;
						Player.onGround = true;
						Game.gameComponentList.add(new DelayTrigger(() -> Player.onGround = false, 20));
						Game.gameComponentList.add(new DelayTrigger(() -> dead = false, 15));
					}
				} else {
					if (Game.currentPlayer.x + Game.currentPlayer.width/2 > screenX + size/2) {
						vX = -2;
					} else {
						vX = 2;
					}
					Game.gameComponentList.add(new DelayTrigger(() -> dead = false, 15));
					//Game.currentPlayer.y = ScaleManager.getHeight();
				}
			}
		} else {
			x += vX;
			for (int i = 0; i < LevelManager.spriteList.size(); i++) {
				if (LevelManager.spriteList.get(i) instanceof Hitable) {
					GameSprite target = (GameSprite)LevelManager.spriteList.get(i);
					if (target != this && screenX + size > target.screenX && screenX < target.screenX + target.size && screenY + size > target.screenY && screenY < target.screenY + target.size) {
						if (((Hitable)target).inflictor()) {
							hitSide(false);
						}
						if (vX > 0) {
							((Hitable)target).hitSide(true);
						} else {
							((Hitable)target).hitSide(false);
						}
						
					}
				}
			}
		}
		boolean grounded = false;
		int collisionGapX = 5;
		int collisionGapY = 5;
		for (int i = 0; i < LevelManager.map.size(); i++) {
			for (int j = 0; j < LevelManager.map.get(i).size(); j++) {
				int testX = LevelManager.map.get(i).get(j).getWorldX();
				int testY = LevelManager.map.get(i).get(j).getWorldY();
				if (LevelManager.map.get(i).get(j).canCollide) { //Collision Detection
					 if (x > testX - size - collisionGapX && x < testX - size && y + size > testY && y < testY + Tile.size) {
						//Collision Right
						if (vX > 0) {
							vX = -2;
						}
						LevelManager.map.get(i).get(j).collideUp();
					}
					
					 if (x < testX + Tile.size + collisionGapX && x > testX + Tile.size && y + size > testY && y < testY + Tile.size) {
						//Collision Left
						if (vX < 0) {
							vX = 2;
						}
						LevelManager.map.get(i).get(j).collideUp();
					}
					
					 if (y > testY - size - collisionGapY && y < testY && x + size > testX && x < testX + Tile.size) {
						//Grounded
						grounded = true;
						y = testY - size;
						if (!this.interactingTiles.contains(LevelManager.map.get(i).get(j))) {
							this.interactingTiles.add(LevelManager.map.get(i).get(j));
						}
						
					} else {
						if (this.interactingTiles.contains(LevelManager.map.get(i).get(j))) {
							this.interactingTiles.remove(LevelManager.map.get(i).get(j));
						}
					}
				}
			}
		}
		
		if (!Game.currentPlayer.deathAnimationPlaying && screenX + size > Game.currentPlayer.getX() && screenX < Game.currentPlayer.getX() + Game.currentPlayer.width && screenY + size > Game.currentPlayer.getY() && screenY < Game.currentPlayer.getY() + Game.currentPlayer.length) {
			if (Player.buffType == 6 && Game.currentPlayer.mode == 10 && Game.currentPlayer.velX != 0) {
				hitUp();
			} else
			if (Game.currentPlayer.getY() + Game.currentPlayer.length <= screenY + 10) {
				if (Game.currentPlayer.mode == 9) {
					hitUp();
				} else {
					Player.velY = -2;
					Player.onGround = true;
					Game.gameComponentList.add(new DelayTrigger(() -> Player.onGround = false, 20));
					dead = true;
				}
			} else {
				if (vX < 0) {
					if (Game.currentPlayer.x + Game.currentPlayer.width/2 < screenX + size/2) {
						Player.die2();
					}
				}
				
				if (vX > 0) {
					if (Game.currentPlayer.x + Game.currentPlayer.width/2 > screenX + size/2) {
						Player.die2();
					}
				}
				
				//Game.currentPlayer.y = ScaleManager.getHeight();
			}
		}
		
		//if (active) {
		if (grounded) {
			vY = 0;
		} else {
			if (vY < 2) {
				vY += 0.05;
			}
			y += vY;
			if (y > LevelManager.mapHeight * Tile.rawsize) {
				LevelManager.spriteList.remove(this);
			}
		}
			
		//}
	}
	
	public void draw(Graphics g) {
		screenX = (int) (x + moveX);
		screenY = (int) (y + moveY);
		Graphics2D p = (Graphics2D) g;
		AffineTransform Tx = new AffineTransform();
		
		  
		p.setTransform(Tx);
		int dX = displacementX;
		int dY = displacementY;
		int scaleX = 40;
		int scaleY = 40;
		dX = displacementX;
		dY = displacementY + 15;
		if (dead) {
			int[] coord = SpriteSheetController.spriteSheetCoord(ResourcePath.getImage(t), 2, 6, 1);
			int displayX = screenX + ScaleManager.screenOffsetX;
			int displayY = screenY + ScaleManager.screenOffsetY;
			if (displayX+size+dX + scaleX/2 > ScaleManager.screenOffsetX && displayX + dX - scaleX/2 < Main.c.getWidth() - ScaleManager.screenOffsetX && displayY+size+dY + scaleY/2 > ScaleManager.screenOffsetY && displayY+dY - scaleY/2 < Main.c.getHeight() - ScaleManager.screenOffsetY) {
				p.drawImage(ResourcePath.getImage(t), displayX + dX - scaleX/2, displayY+dY - scaleY/2, displayX+size+dX + scaleX/2, displayY+size+dY + scaleY/2,coord[0],coord[1],coord[2],coord[3], null);
				active = true;
			} else {
				active = false;
			}
		} else {
			int[] coord = SpriteSheetController.spriteSheetCoord(ResourcePath.getImage(t), frame, this.sheetrow, this.sheetcol);
			int displayX = screenX + ScaleManager.screenOffsetX;
			int displayY = screenY + ScaleManager.screenOffsetY;
			if (displayX+size+dX + scaleX/2 > ScaleManager.screenOffsetX && displayX + dX - scaleX/2 < Main.c.getWidth() - ScaleManager.screenOffsetX && displayY+size+dY + scaleY/2 > ScaleManager.screenOffsetY && displayY+dY - scaleY/2 < Main.c.getHeight() - ScaleManager.screenOffsetY) {
				if (vX < 0) {
				//
				p.drawImage(ResourcePath.getImage(t), displayX + dX - scaleX/2, displayY+dY - scaleY/2, displayX+size+dX + scaleX/2, displayY+size+dY + scaleY/2,coord[0],coord[1],coord[2],coord[3], null);
				} else {
					
					p.drawImage(ResourcePath.getImage(t), displayX+size+dX + scaleX/2, displayY+dY - scaleY/2, displayX + dX - scaleX/2, displayY+size+dY + scaleY/2,coord[0],coord[1],coord[2],coord[3], null);
				}
				active = true;
			} else {
				active = false;
			}
			if (this.frame >= 5) {
				frame = 2;
			}
		}
	}
	
	public void hitUp() {
		int displayX = screenX + ScaleManager.screenOffsetX;
		int displayY = screenY + ScaleManager.screenOffsetY;
		int[] coord = SpriteSheetController.spriteSheetCoord(ResourcePath.getImage(t), 3, this.sheetrow, this.sheetcol);
		Game.gameComponentList.add(new EnemyDeath((int)displayX,(int)displayY,ResourcePath.getImage("k"),-vX * 2,-0.5, coord,size * 2,size * 2));
		LevelManager.spriteList.remove(this);
	}

	public boolean inflictor() {
		return false;
	}

	@Override
	public void hitSide(boolean direction) {
		hitUp();
		
	}
}
