import java.util.LinkedList;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.input.*;

/**
 * @author Vitor Afonso up200908303
 * @author Jorge Silva up201002605 
 */
public class Snake extends GameStateInit{
	public Snake(){
		term = TerminalFacade.createTerminal();
		term.enterPrivateMode();
		MAX_X=term.getTerminalSize().getColumns();
		MAX_Y=term.getTerminalSize().getRows();
		randy=rand.nextInt(MAX_Y-10)+3;
		randx=rand.nextInt(MAX_X-10)+5;
		Cobra snake=newSnake();
		food = new LinkedList<Position>();
		spikes = new LinkedList<Position>();
		border = new LinkedList<Position>();
		while (true){
			terminalSettings();
			//MENU
			if(!started){

				int y=13;
				while(!started){
					term.clearScreen();
					Key j=term.readInput();
					if(j!=null){
						switch (j.getKind()) {
						case Escape:
							term.exitPrivateMode();
							return;
						case Enter:
							snake = selectDificulty(snake, y);
							break;
						case ArrowDown:
							if(y>=13 && y<17)
								y+=2;
							break;
						case ArrowUp:
							if( y>13 && y<=17)
								y-=2;
							break;
						default:
							break;					
						}
					}
					printWelcomeMenu(y);
				}

				food=makeFood(new LinkedList<Position>(), snake);
				spikes=makeSpikes(new LinkedList<Position>(), snake);
				border=makeBorders(snake, new LinkedList<Position>());
			}

			printFoodSpikes(food, spikes);
			//Loading Screen
			if(pause)
				loadingScreen(snake, food, border);
			whichWay(snake, food,border);
			checkCrashed(snake, spikes);
			outOfBounds(snake, border);

			if(snake.crashed){
				snake=gameOver(snake);
			}
			if (end)
				break;
		}
	}
}