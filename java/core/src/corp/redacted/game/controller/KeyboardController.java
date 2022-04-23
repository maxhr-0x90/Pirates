package corp.redacted.game.controller;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class KeyboardController  implements InputProcessor {
	public boolean left,right,up,down;
	public boolean leftB,rightB,upB,downB;
	public boolean isMouseDown;
	public Vector2 mouseLocation = new Vector2(0,0);

	@Override
	public boolean keyDown(int keycode) {
		boolean keyProcessed = false;
		switch (keycode) // switch code base on the variable keycode
        {
	        case Keys.LEFT:  	// if keycode is the same as Keys.LEFT a.k.a 21
	            left = true;	// do this
	            keyProcessed = true;	// we have reacted to a keypress
	            break;
	        case Keys.RIGHT: 	// if keycode is the same as Keys.LEFT a.k.a 22
	            right = true;	// do this
	            keyProcessed = true;	// we have reacted to a keypress
	            break;
				  case Keys.UP: 		// if keycode is the same as Keys.LEFT a.k.a 19
			            up = true;		// do this
			            keyProcessed = true;	// we have reacted to a keypress
			            break;
			    case Keys.DOWN: 	// if keycode is the same as Keys.LEFT a.k.a 20
			            down = true;	// do this
			            keyProcessed = true;	// we have reacted to a keypress
									break;
				  case Keys.A:  	// if keycode is the same as Keys.LEFT a.k.a 21
				  				leftB = true;	// do this
				  				keyProcessed = true;	// we have reacted to a keypress
									break;
				   case Keys.D: 	// if keycode is the same as Keys.LEFT a.k.a 22
									rightB = true;	// do this
									keyProcessed = true;	// we have reacted to a keypress
									break;
					  case Keys.W: 		// if keycode is the same as Keys.LEFT a.k.a 19
									upB = true;		// do this
									keyProcessed = true;	// we have reacted to a keypress
									break;
						case Keys.S: 	// if keycode is the same as Keys.LEFT a.k.a 20
									downB = true;	// do this
									keyProcessed = true;	// we have reacted to a keypress
					default:
					break;
        }
		return keyProcessed;	//  return our peyProcessed flag
	}

	@Override
	public boolean keyUp(int keycode) {
		boolean keyProcessed = false;
		switch (keycode) // switch code base on the variable keycode
        {
	        case Keys.LEFT:  	// if keycode is the same as Keys.LEFT a.k.a 21
	            left = false;	// do this
	            keyProcessed = true;	// we have reacted to a keypress
	            break;
	        case Keys.RIGHT: 	// if keycode is the same as Keys.LEFT a.k.a 22
	            right = false;	// do this
	            keyProcessed = true;	// we have reacted to a keypress
	            break;
				case Keys.UP: 		// if keycode is the same as Keys.LEFT a.k.a 19
			            up = false;		// do this
			            keyProcessed = true;	// we have reacted to a keypress
			            break;
			   case Keys.DOWN: 	// if keycode is the same as Keys.LEFT a.k.a 20
			            down = false;	// do this
			            keyProcessed = true;	// we have reacted to a keypress
					break;
					case Keys.A:  	// if keycode is the same as Keys.LEFT a.k.a 21
	            leftB = false;	// do this
	            keyProcessed = true;	// we have reacted to a keypress
	            break;
	        case Keys.D: 	// if keycode is the same as Keys.LEFT a.k.a 22
	            rightB = false;	// do this
	            keyProcessed = true;	// we have reacted to a keypress
	            break;
				 case Keys.W: 		// if keycode is the same as Keys.LEFT a.k.a 19
			        upB = false;		// do this
			        keyProcessed = true;	// we have reacted to a keypress
			        break;
			   case Keys.S: 	// if keycode is the same as Keys.LEFT a.k.a 20
			        downB = false;	// do this
			        keyProcessed = true;	// we have reacted to a keypress
	        default:
					break;
        }
		return keyProcessed;	//  return our peyProcessed flag
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == 0){
			isMouseDown = true;
		}
		mouseLocation.x = screenX;
		mouseLocation.y = screenY;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		if(button == 0){
			isMouseDown = false;
		}
		mouseLocation.x = screenX;
		mouseLocation.y = screenY;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

	public void reset(){
		left = false; right = false; up = false; down = false;
		leftB = false; rightB = false; upB = false; downB = false;
		isMouseDown = false;
		mouseLocation.set(0, 0);
	}
}
