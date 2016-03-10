import java.util.LinkedList;
import java.util.Random;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.input.*;

/**
 * @author Vitor Afonso up200908303
 * @author Jorge Silva up201002605 
 */
public class Snake
{
	private static final int HARD = 70;
	private static final int MEDIUM = 120;
	private static final int EASY = 170;
	private Terminal term;
	Random rand=new Random();
	private int MAX_Y;
	private int MAX_X;
	int gametimer=40;
	boolean started=false;
	boolean end=false;
	boolean pause=true;
	private int randx=0;
	private int randy=0;
	private int score=0;
	private int bonus=50;
	LinkedList<Position> border;
	SnakeModel snake;
	public Snake(){
		term = TerminalFacade.createTerminal();
		term.enterPrivateMode();
		MAX_X=term.getTerminalSize().getColumns();
		MAX_Y=term.getTerminalSize().getRows();
		randy=rand.nextInt(MAX_Y-10)+3;
		randx=rand.nextInt(MAX_X-10)+5;
		snake=newSnake();

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
							snake = selectDificulty(y);
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


				border=makeBorders(new LinkedList<Position>());
			}
			term.clearScreen();
			printFoodSpikes();
			printBorders();
			//Loading Screen
			if(pause)
				loadingScreen();

			printScore();
			printSnake();
			whichWay();
			checkCrashed();
			if(snake.crashed){
				snake=gameOver();
			}
			if (end)
				break;
		}
	}

	//VISUALIZATION ---------------------------------------------------------------

	/**
	 * Change settings of the terminal
	 */
	private void terminalSettings() {
		term.applySGR(Terminal.SGR.ENTER_BOLD);
		term.applyForegroundColor(Terminal.Color.GREEN);
		term.applyBackgroundColor(Color.BLACK);
		term.setCursorVisible(false);
	}

	/**
	 * Prints Welcoming Menu
	 * @param y
	 */
	private void printWelcomeMenu(int y) {
		String welcome="Welcome to Snake!";
		show(welcome,calcPosition(welcome),9);
		String easy = "Easy";
		show(easy, 48,13);
		String medium = "Medium";
		show(medium,48,15);
		String hard = "Hard";
		show(hard, 48,17);
		show("->",44,y);
		try{
			Thread.sleep(200);
		}
		catch (InterruptedException ie){
			ie.printStackTrace();
		}
	}

	/**
	 * Prints the waiting sign on top of the terminal and sets the start of the game
	 * @param snake
	 */
	private void loadingScreen() {
		term.clearScreen();
		printFoodSpikes();
		printBorders();
		printSnake();
		String game="Game Starts in "+(gametimer/10);
		String getready="Get Ready!";
		if(gametimer>0 && gametimer<40){
			show(game, calcPosition(game),0);
		}
		if(gametimer==40)
			show(getready, calcPosition(getready), 0);
		if(gametimer==0)
			pause=false;
		gametimer-=10;
		bonus=50;
		try{
			Thread.sleep(800);
		}
		catch (InterruptedException ie){
			ie.printStackTrace();
		}
	}
	/**
	 * Prints borders
	 */

	private void printBorders() {
		term.applyBackgroundColor(Color.RED);
		for(Position p:border)
			show(" ",p.x,p.y);
		terminalSettings();
	}

	/**
	 * Print snake
	 * @param i 
	 * @param snake
	 */
	private void printSnake() {
		term.applyBackgroundColor(Color.YELLOW);
		for(Position pos:snake.body){
			if(pos.equals(snake.body.get(0))){
				show(" ", pos.x, pos.y);
			}
			else
				show(" ", pos.x, pos.y);
		}
		terminalSettings();
	}

	/**
	 * Print Score
	 * @param snake
	 * @param food
	 */
	private void printScore() {
		snake.eat=snake.hasEaten();
		if(snake.eat){
			score+=100+bonus;
			bonus=50;
		}
		String scorePlusBonus = "Score: "+Integer.toString(score)+"      Bonus: "+Integer.toString(bonus);
		show(scorePlusBonus,calcPosition(scorePlusBonus),0);
	}

	/**
	 * Prints all the obstacles/objectives on the board
	 * @param food
	 * @param spikes
	 */
	private void printFoodSpikes() {
		if(gametimer%2==0)
			applyColor(Color.CYAN);
		else
			term.applyBackgroundColor(Color.BLUE);
		for(Position p:snake.food){
			show(" ",p.x,p.y);
		}
		term.applyBackgroundColor(Color.RED);
		for(Position p:snake.spikes){
			show(" ",p.x,p.y);
		}
		terminalSettings();
	}

	/**
	 * 
	 */
	private void applyColor(Color cor) {
		term.applyBackgroundColor(cor);
	}

	//END OF SNAKE CONTROL

	/**
	 * Game Over
	 * @param snake
	 * @param food
	 * @param spikes
	 * @return
	 */
	private SnakeModel gameOver() {
		int dificulty=snake.getDificulty();
		int y=20;
		int x=40;
		while(end){
			term.clearScreen();
			Key j=term.readInput();
			if(j!=null){
				switch (j.getKind()) {
				case Escape:
					term.exitPrivateMode();
					end=true;
					return null;
				case Enter:
					snake = gameOverRestart(dificulty, y);
					snake.produceObstaclesTargets();
					break;
				case ArrowDown:
					if(y>=20 && y<22){
						x=37;
						y+=2;
					}
					break;
				case ArrowUp:
					if( y>20 && y<=22){
						x=40;
						y-=2;
					}
					break;
				default:
					break;					
				}
			}
			gameOverScreen(y, x);
		}
		return snake;
	}

	/**
	 * Prints the game over sign
	 * @param y
	 * @param x
	 */
	private void gameOverScreen(int y, int x) {
		String scores="Your Score: "+Integer.toString(score);
		String[] gameover={" ____    ____   _   _   ___  ",
				"|  __|  |  _ | | | | | |  _| ",
				"| | __  | |_|| |  |  | | |_  ",
				"| |_| | |  _ | |  _  | |  _| ",
				"|_____| |_| || |_| |_| |___| ",
				"                             ",
				" ____    _     _    ___   _____ ",
				"|  _ |  | |   | |  |  _| |  _  |",
				"| | ||  |  | |  |  | |_  | |_| |",
				"| |_||   |     |   |  _| |     /",
		"|____|    |___|    |___| |_| \\_\\"};
		show(scores,calcPosition(scores),18);
		for(int i=0;i<gameover.length;i++){
			show(gameover[i],calcPosition(gameover[i]),5+i);
		}
		String tryagain="Try Again?";
		show(tryagain,calcPosition(tryagain),20);
		String menugo="Go to Main Menu";
		show(menugo,calcPosition(menugo),22);
		show("->",x,y);
		try{
			Thread.sleep(200);
		}
		catch (InterruptedException ie){
			ie.printStackTrace();
		}
	}

	/**
	 * Check which of the selections on the game over screen has been selected
	 * @param snake
	 * @param dificulty
	 * @param y
	 * @return
	 */
	private SnakeModel gameOverRestart(int dificulty, int y) {
		if(y==20){
			started=true;
			snake=newSnake();
			snake.setDificulty(dificulty);

			gametimer=40;
			pause=true;
			snake.crashed=false;
			end=false;

			score=0;
		}
		if(y==22){
			started=false;
			pause=true;
			snake.crashed=false;
			end=false;
		}
		return snake;
	}

	/**
	 * Gets every character on the terminal
	 * @param str
	 * @param x
	 * @param y
	 */
	private void show(String str, int x, int y){
		term.moveCursor(x, y);
		int len = str.length();
		for (int i = 0; i < len; i++){
			term.putCharacter(str.charAt(i));
		}
	}


	/**
	 * Makes borders depending on the dificulty of the game.
	 * @return 
	 */
	private LinkedList<Position> makeBorders(LinkedList<Position> border) {
		Position aux=null;

		for(int i=2;i<MAX_X-2;i++){
			for(int r=2;r<MAX_Y-2;r++){
				if(i==2 || i==97){
					aux= new Position(i, r);
					border.add(aux);
				}
				if(r==2 || r==27){
					aux= new Position(i, r);
					border.add(aux);
				}
			}
		}

		if(snake.getDificulty()==MEDIUM){
			for(int i=2;i<MAX_X-2;i++){
				for(int r=2;r<MAX_Y-2;r++){
					if(i==MAX_X/2 && r>(MAX_Y/2)-5 && r<(MAX_Y/2)+5){
						aux= new Position(i, r);
						border.add(aux);						
					}
					if(r==MAX_Y/2 && i>(MAX_X/2)-25 && i<(MAX_X/2)+25){
						aux= new Position(i, r);
						border.add(aux);
					}
				}
			}
		}
		if(snake.getDificulty()==HARD){
			for(int i=2;i<MAX_X-2;i++){
				for(int r=2;r<MAX_Y-2;r++){
					if(i==MAX_X/2){
						if(r>2 && r<(MAX_Y/2)-5){
							aux= new Position(i, r);
							border.add(aux);						
						}
						if(r>(MAX_Y/2)+5 && r<(MAX_Y-2)){
							aux= new Position(i, r);
							border.add(aux);	
						}

					}

					if(r==MAX_Y/2){
						if(i>2 && i<(MAX_X/2)-10){
							aux= new Position(i, r);
							border.add(aux);
						}
						if(i>(MAX_X/2)+10 && i<MAX_X-2){
							aux= new Position(i, r);
							border.add(aux);
						}
					}
				}
			}
		}
		return border;
	}



	/**
	 * Determines position so that the string can be at the middle of the screen
	 * @param easy2
	 * @return
	 */
	private int calcPosition(String easy2) {
		int mid=0;
		mid=(MAX_X-easy2.length())/2;
		return mid;
	}

	//END OF VISUALIZATION -----------------------------------------------------------


	//SNAKE CONTROL
	/**
	 * Selects the dificulty of the game
	 * @param snake
	 * @param y
	 * @return
	 */
	private SnakeModel selectDificulty(int y) {
		gametimer=40;
		if(y==13){
			snake = newSnake();
			snake.setDificulty(EASY);
			started=true;
			end=false;
		}
		if(y==15){
			snake=newSnake();
			snake.setDificulty(MEDIUM);
			started=true;
			end=false;
		}
		if(y==17){	
			snake=newSnake();
			snake.setDificulty(HARD);
			started=true;
			end=false;
		}

		score=0;
		return snake;
	}

	/**
	 * Moving snake same direction or change direction
	 * @param snake
	 * @param food
	 * @param border 
	 */
	private void whichWay() {
		if(!end && !snake.crashed){
			Key k = term.readInput();
			if (k != null && !pause) {
				started=true;
				switch (k.getKind()) {
				case Escape:
					term.exitPrivateMode();
					end=true;
					return;
				case ArrowLeft: 
					randx-=1;
					snake.makeStep(Direction.LEFT);
					break;
				case ArrowRight:
					randx+=1;
					snake.makeStep(Direction.RIGHT);
					break;
				case ArrowDown:
					randy+=1;
					snake.makeStep(Direction.DOWN);
					break;
				case ArrowUp:
					randy-=1;
					snake.makeStep(Direction.UP);
					break;
				default:
					break;
				}	
			}
			else{
				if(!pause)
					keepGoing();
			}
			printSnake();

		}
		try{
			Thread.sleep(snake.getDificulty());
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

	
		if(bonus<=0)
			bonus=0;
		else{
			bonus-=1;
		}

	}

	/**
	 * Snake keeps moving on the direction of the last step
	 * @param snake
	 * @param food
	 */
	private void keepGoing() {
		if(started){
			snake.makestep();
			printSnake();

		}
	}

	/**
	 * See if the snake hit something or itself
	 * @param snake
	 * @param spikes
	 */
	private void checkCrashed() {
		if(snake.gotSpikes() || snake.hitMe() || outOfBounds(snake, border)){
			snake.crashed=true;
			end=true;
		}
	}

	/**
	 * Creates new random snake
	 * @return
	 */
	private SnakeModel newSnake() {
		SnakeModel snake;
		snake=new SnakeModel(rand.nextInt(MAX_X-20)+10, rand.nextInt(MAX_Y-10)+5, 5, getRandomDirection());
		snake.produceObstaclesTargets();
		return snake;
	}

	/**
	 * Gives a random direction
	 * @return 
	 */
	private Direction getRandomDirection() {
		Random r= new Random();
		int j= r.nextInt(4);
		switch (j) {
		case 0:
			return Direction.DOWN;
		case 1:
			return Direction.UP;
		case 2:
			return Direction.RIGHT;
		case 3:
			return Direction.LEFT;

		default:
			return null;
		}
	}

	/**
	 * Check if the snake is out of the arena
	 * @param snake
	 */
	private boolean outOfBounds(SnakeModel snake,LinkedList<Position> border) {
		for(int i=0;i<border.size();i++){
			if(snake.equals(border.get(i))){
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args){
		new Snake();
	}
}
