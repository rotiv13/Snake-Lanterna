import java.util.LinkedList;
import java.util.Random;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.input.*;

enum Direction{
	UP,DOWN,RIGHT,LEFT
}

class Position{
	int x;
	int y;
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
}

class Cobra{
	LinkedList<Position> body =new LinkedList<Position>();
	Random rand=new Random();
	Direction direction;
	Position position;
	boolean crashed=false;
	boolean eat=false;
	private int dificulty=300;
	Cobra(int x,int y,int length, Direction dir){
		if(oposite(dir)){
			setDirection(dir);
			for (int i = 0; i < length; i++) {
				int bodyX = x;
				int bodyY = y;

				if (direction == Direction.DOWN) {
					bodyY = bodyY - i;
				}
				if (direction == Direction.UP) {
					bodyY = bodyY + i;
				}
				if (direction == Direction.RIGHT) {
					bodyX = bodyY - i;
				}
				if (direction == Direction.LEFT)  {
					bodyX = bodyX + i;
				}
				addEndOfTail(bodyX, bodyY);
			}
		}
	}
	//SNAKE CONTROL   ------------------------------------------------------------

	/**
	 * Checks if the direction you provided is opposite to the direction of the snake
	 * @param dir
	 * @return
	 */
	private boolean oposite(Direction dir) {
		if(direction==Direction.LEFT && dir==Direction.RIGHT)
			return false;
		if(direction==Direction.UP && dir==Direction.DOWN)
			return false;
		if(direction==Direction.DOWN && dir==Direction.UP)
			return false;
		if(direction==Direction.RIGHT && dir==Direction.LEFT)
			return false;
		else
			return true;
	}

	/**
	 * Prevents the snake on colliding with itself when pressing an opposite direction of which the snake is going
	 * @param dir
	 */
	private void dontGoTheOppositeDirection(Direction dir) {
		if(oposite(dir)){
			this.direction=dir;
			try{
				Thread.sleep(getDificulty());
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
			for (int i = body.size() - 1; i > 0; i--) {
				body.get(i).setX(body.get(i - 1).getX());
				body.get(i).setY(body.get(i - 1).getY()); 
			} 
			if (direction == Direction.LEFT) {
				body.get(0).setX(body.get(0).getX() - 1); 
			}
			if (direction == Direction.RIGHT) {
				body.get(0).setX(body.get(0).getX() + 1); 
			}
			if (direction == Direction.UP) { 
				body.get(0).setY(body.get(0).getY() - 1);
			}
			if (direction == Direction.DOWN) { 
				body.get(0).setY(body.get(0).getY() + 1);
			}
		}
	}

	public boolean equals(Position pos){
		if(body.get(0).x==pos.x && body.get(0).y==pos.y)
			return true;
		return false;

	}

	/**
	 * Checks if the snake has eaten and makes it grow
	 */
	private void eatAndGrow() {
		if(eat){
			int auxx = 0;
			int auxy = 0;
			if (direction == Direction.LEFT) {
				auxx=body.getLast().getX()+1;
				auxy=body.getLast().getY();
			}
			if (direction == Direction.RIGHT) {
				auxx=body.getLast().getX()-1;
				auxy=body.getLast().getY();
			}
			if (direction == Direction.UP) { 
				auxx=body.getLast().getX();
				auxy=body.getLast().getY()+1;
			}
			if (direction == Direction.DOWN) { 
				auxx=body.getLast().getX();
				auxy=body.getLast().getY()-1;
			}
			addEndOfTail(auxx, auxy);
			new Cobra(auxx, auxy, 1, direction);
		}
	}

	/**
	 * Checks if the snake has eaten a fruit
	 * @param comida
	 * @param snake 
	 * @return
	 */
	public boolean hasEaten(LinkedList<Position> comida, Cobra snake){
		for(int i=0;i<comida.size();i++){
			if(equals(comida.get(i))){
				comida.set(i,new Position(rand.nextInt(90)+4, rand.nextInt(20)+4));
				return true;
			}
		}	
		return false;
	}

	private void addEndOfTail(int auxx, int auxy) {
		body.add(new Position(auxx,auxy));
	}

	/**
	 * Checks if the snake has collided with a spike
	 * @param spikes
	 * @return
	 */
	public boolean gotSpikes(LinkedList<Position> spikes){
		for(int i=0;i<spikes.size();i++){
			if(equals(spikes.get(i))){
				return true;
			}
		}	
		return false;
	}

	/**
	 * Check if it collided with itself
	 * @param snake
	 * @return
	 */
	public boolean hitMe(Cobra snake){
		for(int i=2;i<snake.body.size();i++){
			if(snake.equals(snake.body.get(i))){
				return true;
			}
		}
		return false;
	}
	//END OF SNAKE CONTROL ----------------------------------------------------------
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void makestep(){
		makeStep(direction);
	}

	/**
	 * Pushes forward in that direction dir
	 * @param dir
	 */
	public void makeStep(Direction dir){
		if(crashed)
			return;
		eatAndGrow();
		dontGoTheOppositeDirection(dir);
	}

	public int getDificulty() {
		return dificulty;
	}

	public void setDificulty(int dificulty) {
		this.dificulty = dificulty;
	}
}

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
	private int length =1;
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
	LinkedList<Position> food;
	LinkedList<Position> border;
	LinkedList<Position> spikes;
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
	
	//VISUALIZATION ---------------------------------------------------------------

	/**
	 * Change settings of the terminal
	 */
	private void terminalSettings() {
		term.applySGR(Terminal.SGR.ENTER_BOLD);
		term.applyForegroundColor(Terminal.Color.GREEN);
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
	 * @param food
	 * @param border
	 */
	private void loadingScreen(Cobra snake, LinkedList<Position> food,
			LinkedList<Position> border) {
		term.clearScreen();
		printFoodSpikes(food, spikes);
		printBorders(border);
		printSnake(snake);
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
	 * @param border
	 */

	private void printBorders(LinkedList<Position> border) {
		for(Position p:border)
			show("#",p.x,p.y);
	}

	/**
	 * Print snake
	 * @param snake
	 */
	private void printSnake(Cobra snake) {
		for(Position pos:snake.body){
			if(pos.equals(snake.body.get(0))){
				show("@", pos.x, pos.y);
			}
			else
				show("O", pos.x, pos.y);
		}

	}

	/**
	 * Print Score
	 * @param snake
	 * @param food
	 */
	private void printScore(Cobra snake, LinkedList<Position> food) {
		snake.eat=snake.hasEaten(food,snake);
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
	private void printFoodSpikes(LinkedList<Position> food,
			LinkedList<Position> spikes) {
		for(Position p:food){
			show("Q",p.x,p.y);
		}
		for(Position p:spikes){
			show("X",p.x,p.y);
		}
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
		String trya="Try Again?";
		show(trya,calcPosition(trya),20);
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
	private Cobra gameOverRestart(Cobra snake, int dificulty, int y) {
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

	//END OF VISUALIZATION -----------------------------------------------------------
	 	
	/**
	 * Makes borders depending on the dificulty of the game.
	 * @param snake 
	 * @return 
	 */
	private LinkedList<Position> makeBorders(Cobra snake,LinkedList<Position> border) {
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
	 * Makes food for the snake to eat
	 * @param food
	 * @return
	 */
	private LinkedList<Position> makeFood(LinkedList<Position> food, Cobra snake) {
		Random r=new Random();
		int size=0;
		if(snake.getDificulty()==EASY){
			size=15;
		}
		if(snake.getDificulty()==MEDIUM){
			size=10;
		}
		if(snake.getDificulty()==HARD){
			size=5;
		}
		food=produceFood(food, r, size);

		return food;
	}

	/**
	 * Makes those nasty obstacles
	 * @param spikes
	 * @return
	 */
	private LinkedList<Position> makeSpikes(LinkedList<Position> spikes, Cobra snake) {
		Random r=new Random();
		int size=0;
		if(snake.getDificulty()==EASY)
			size=10;
		if(snake.getDificulty()==MEDIUM)
			size=20;
		if(snake.getDificulty()==HARD)
			size=35;
		spikes=produceSpikes(spikes, r, size);
		return spikes;
	}

	/**
	 * Produces food on with the size, that depends on the dificulty
	 * @param food
	 * @param r
	 * @param size
	 * @return 
	 */
	private LinkedList<Position> produceFood(LinkedList<Position> food, Random r, int size) {
		Position f;
		for(int i=0;i<size;i++){
			f=new Position(r.nextInt(90)+3, r.nextInt(20)+3);
			food.add(f);
		}
		return food;
	}

	/**
	 * Produces spikes on with the size, that depends on the dificulty 
	 * @param spikes
	 * @param r
	 * @param size
	 * @return 
	 */
	private LinkedList<Position> produceSpikes(LinkedList<Position> spikes, Random r, int size) {
		Position f;
		for(int i=0;i<size;i++){
			f=new Position(r.nextInt(90)+3, r.nextInt(20)+3);
			spikes.add(f);
		}
		return spikes;
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
	private Cobra selectDificulty(Cobra snake, int y) {
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
	private void whichWay(Cobra snake, LinkedList<Position> food, LinkedList<Position> border) {
		if(!end && !snake.crashed){
			printBorders(border);
			term.flush();
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
					new Cobra(randx, randy,length, Direction.LEFT);
					snake.makeStep(Direction.LEFT);
					break;
				case ArrowRight:
					randx+=1;
					new Cobra(randx, randy,length, Direction.RIGHT);
					snake.makeStep(Direction.RIGHT);
					break;
				case ArrowDown:
					randy+=1;
					new Cobra(randx, randy,length, Direction.DOWN);
					snake.makeStep(Direction.DOWN);
					break;
				case ArrowUp:
					randy-=1;
					new Cobra(randx,randy,length, Direction.UP);
					snake.makeStep(Direction.UP);
					break;
				default:
					break;
				}
				term.clearScreen();
				printScore(snake, food);
				printSnake(snake);
			}
			else{
				if(!pause)
					keepGoing(snake, food);
			}
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
	private void keepGoing(Cobra snake, LinkedList<Position> food) {
		if(started){
			snake.makestep();
			term.clearScreen();
			printScore(snake, food);
			printSnake(snake);
		}
	}

	/**
	 * See if the snake hit something or itself
	 * @param snake
	 * @param spikes
	 */
	private void checkCrashed(Cobra snake, LinkedList<Position> spikes) {
		boolean colided=snake.hitMe(snake);
		if(snake.gotSpikes(spikes) || colided){
			snake.crashed=true;
			end=true;
		}
	}

	/**
	 * Creates new random snake
	 * @return
	 */
	private Cobra newSnake() {
		Cobra snake;
		snake=new Cobra(rand.nextInt(MAX_X-20)+10, rand.nextInt(MAX_Y-10)+5, 5, getRandomDirection());
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
	private void outOfBounds(Cobra snake, LinkedList<Position> border) {
		for(int i=0;i<border.size();i++){
			if(snake.equals(border.get(i))){
				snake.crashed=true;
				end=true;
			}
		}
	}

	//END OF SNAKE CONTROL

	/**
	 * Game Over
	 * @param snake
	 * @param food
	 * @param spikes
	 * @return
	 */
	private Cobra gameOver(Cobra snake) {
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
					snake = gameOverRestart(snake, dificulty, y);
					this.food=makeFood(new LinkedList<Position>(), snake);
					this.spikes=makeSpikes(new LinkedList<Position>(), snake);
					break;
				case ArrowDown:
					if(y>=20 && y<22){
						x=38;
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

	public static void main(String[] args){
		new Snake();
	}
}