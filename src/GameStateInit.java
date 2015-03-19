import java.util.LinkedList;
import java.util.Random;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

public class GameStateInit {

	private static final int HARD = 70;
	private static final int MEDIUM = 120;
	private static final int EASY = 170;
	protected Terminal term;
	private int length = 1;
	protected Random rand = new Random();
	protected int MAX_Y;
	protected int MAX_X;
	int gametimer = 40;
	protected boolean started = false;
	protected boolean end = false;
	protected boolean pause = true;
	protected int randx = 0;
	protected int randy = 0;
	private int score = 0;
	private int bonus = 50;
	protected LinkedList<Position> food;
	protected LinkedList<Position> border;
	protected LinkedList<Position> spikes;

	public static void main(String[] args) {
		new Snake();
	}

	/**
	 * Change settings of the terminal
	 */
	protected void terminalSettings() {
		term.applySGR(Terminal.SGR.ENTER_BOLD);
		term.applyForegroundColor(Terminal.Color.GREEN);
		term.setCursorVisible(false);
	}

	/**
	 * Prints Welcoming Menu
	 * @param y
	 */
	protected void printWelcomeMenu(int y) {
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
	protected void loadingScreen(Cobra snake, LinkedList<Position> food, LinkedList<Position> border) {
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
	protected void printFoodSpikes(LinkedList<Position> food, LinkedList<Position> spikes) {
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
	private void show(String str, int x, int y) {
		term.moveCursor(x, y);
		int len = str.length();
		for (int i = 0; i < len; i++){
			term.putCharacter(str.charAt(i));
		}
	}

	/**
	 * Makes borders depending on the dificulty of the game.
	 * @param snake 
	 * @return 
	 */
	protected LinkedList<Position> makeBorders(Cobra snake, LinkedList<Position> border) {
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
	protected LinkedList<Position> makeFood(LinkedList<Position> food, Cobra snake) {
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
	protected LinkedList<Position> makeSpikes(LinkedList<Position> spikes, Cobra snake) {
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

	/**
	 * Selects the dificulty of the game
	 * @param snake
	 * @param y
	 * @return
	 */
	protected Cobra selectDificulty(Cobra snake, int y) {
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
	protected void whichWay(Cobra snake, LinkedList<Position> food, LinkedList<Position> border) {
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
				
			}
			else{
				if(!pause)
					keepGoing(snake, food);
			}
		}
		printScore(snake, food);
		printSnake(snake);
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
		}
	}

	/**
	 * See if the snake hit something or itself
	 * @param snake
	 * @param spikes
	 */
	protected void checkCrashed(Cobra snake, LinkedList<Position> spikes) {
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
	protected Cobra newSnake() {
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
	protected void outOfBounds(Cobra snake, LinkedList<Position> border) {
		for(int i=0;i<border.size();i++){
			if(snake.equals(border.get(i))){
				snake.crashed=true;
				end=true;
			}
		}
	}

	/**
	 * Game Over
	 * @param snake
	 * @param food
	 * @param spikes
	 * @return
	 */
	protected Cobra gameOver(Cobra snake) {
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

	public GameStateInit() {
		super();
	}

}