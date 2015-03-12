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
	private static final int HARD = 50;
	private static final int MEDIUM = 100;
	private static final int EASY = 150;
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

	public boolean equals(Position pos){
		if(body.get(0).x==pos.x && body.get(0).y==pos.y)
			return true;
		return false;

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

	private void addEndOfTail(int auxx, int auxy) {
		body.add(new Position(auxx,auxy));
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
 *
 */
public class Snake
{
	private static final int HARD = 50;
	private static final int MEDIUM = 100;
	private static final int EASY = 150;
	private Terminal term;
	private int length =1;
	Random rand=new Random();
	private int MAX_Y;
	private int MAX_X;
	boolean started=false;
	boolean end=false;
	private int randx=0;
	private int randy=0;
	private int score=0;

	public Snake(){
		term = TerminalFacade.createTerminal();
		term.enterPrivateMode();
		MAX_X=term.getTerminalSize().getColumns();
		MAX_Y=term.getTerminalSize().getRows();
		randy=rand.nextInt(MAX_Y-10)+3;
		randx=rand.nextInt(MAX_X-10)+5;
		Cobra snake=new Cobra(randx,randy,5,Direction.LEFT);
		LinkedList<Position> food = new LinkedList<Position>();
		LinkedList<Position> spikes = new LinkedList<Position>();
		while (true){
			terminalSettings();
			//MENU
			if(!started){
				if(snake.eat && snake.getDificulty()==HARD){
					food=makeFood(new LinkedList<Position>());
				}
				else
					food=makeFood(new LinkedList<Position>());
				spikes=makeSpikes(new LinkedList<Position>());
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
			}
			//MENU FIM
			printFoodSpikes(food, spikes);
			//MUDANÇAS DE DIRECÇÃO
			whichWay(snake, food);
			checkCrashed(snake, spikes);
			outOfBounds(snake);
			//GAME OVER
			if(snake.crashed){
				snake=gameOver(snake,food,spikes);
			}
			if (end)
				break;
		}
	}

	/**
	 * Selects the dificulty of the game
	 * @param snake
	 * @param y
	 * @return
	 */
	private Cobra selectDificulty(Cobra snake, int y) {
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
	 * Change settings of the menu
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
	 * Determines postion so that the string can be at the middle of the screen
	 * @param easy2
	 * @return
	 */
	private int calcPosition(String easy2) {
		int mid=0;
		mid=50-easy2.length()/2;
		return mid;
	}

	/**
	 * Moving snake same direction or change direction
	 * @param snake
	 * @param food
	 */
	private void whichWay(Cobra snake, LinkedList<Position> food) {
		if(!end && !snake.crashed){
			printBorders();
			term.flush();
			Key k = term.readInput();
			if (k != null) {
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
				keepGoing(snake, food);
			}
		}
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
		term.flush();
	}
	/**
	 * Print Score to Terminal
	 * @param snake
	 * @param food
	 */
	private void printScore(Cobra snake, LinkedList<Position> food) {
		snake.eat=snake.hasEaten(food,snake);
		if(snake.eat)
			score+=10;
		show("Score: "+Integer.toString(score),50,0);
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
	 * Printing Borders
	 */
	private void printBorders() {
		for(int i=2;i<MAX_X-2;i++){
			for(int r=2;r<MAX_Y-2;r++){
				if(i==2 || i==97)
					show("#",i,r);
				if(r==2 || r==27)
					show("#",i,r);
			}
		}
		try{
			Thread.sleep(50);
		}
		catch (InterruptedException ie){
			ie.printStackTrace();
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
		snake=new Cobra(rand.nextInt(MAX_X-10)+5, rand.nextInt(MAX_Y-10)+3, 5, getRandomDirection());
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
	private void outOfBounds(Cobra snake) {
		if(snake.body.getFirst().x<3 || snake.body.getFirst().y<3 || snake.body.getFirst().x>96 || snake.body.getFirst().y>26){
			snake.crashed=true;
			end=true;
		}
	}
	/**
	 * Game Over
	 * @param snake
	 * @param food
	 * @param spikes
	 * @return
	 */
	private Cobra gameOver(Cobra snake, LinkedList<Position> food, LinkedList<Position> spikes) {
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
				"| |_||   |     |   |  _| |    / ",
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
			snake.crashed=false;
			end=false;
			score=0;
		}
		if(y==22){
			started=false;
			snake.crashed=false;
			end=false;
		}
		return snake;
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
	 * Makes food for the snake to eat
	 * @param food
	 * @return
	 */
	private LinkedList<Position> makeFood(LinkedList<Position> food) {
		Random r=new Random();
		Position f;
		for(int i=0;i<4;i++){
			f=new Position(r.nextInt(90)+3, r.nextInt(20)+3);
			food.add(f);
		}
		return food;
	}
	/**
	 * Makes those nasty obstacles
	 * @param spikes
	 * @return
	 */
	private LinkedList<Position> makeSpikes(LinkedList<Position> spikes) {
		Random r=new Random();
		Position f;
		for(int i=0;i<4;i++){
			f=new Position(r.nextInt(90)+3, r.nextInt(20)+3);
			spikes.add(f);
		}
		return spikes;
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

	public static void main(String[] args){
		new Snake();
	}
}

