

import java.util.LinkedList;
import java.util.Random;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.input.Key.Kind;

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
			direction = dir;
			//		System.out.println(direction);

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

				body.add(new Position(bodyX, bodyY));
			}
		}
	}
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
	public boolean hasEaten(LinkedList<Position> comida){

		for(int i=0;i<comida.size();i++){
			if(equals(comida.get(i))){

				comida.set(i,new Position(rand.nextInt(90)+4, rand.nextInt(20)+4));
				return true;
			}
		}	
		return false;

	}
	public boolean gotSpikes(LinkedList<Position> spikes){
		for(int i=0;i<spikes.size();i++){
			if(equals(spikes.get(i))){
				return true;
			}
		}	
		return false;

	}
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

	public void makeStep(Direction dir){
		if(crashed)
			return;
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
			body.add(new Position(auxx,auxy));
			new Cobra(auxx, auxy, 1, direction);
		}
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
	public int getDificulty() {
		return dificulty;
	}
	public void setDificulty(int dificulty) {
		this.dificulty = dificulty;
	}
}
public class Snake
{
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
			term.applySGR(Terminal.SGR.ENTER_BOLD);
			term.applyForegroundColor(Terminal.Color.GREEN);
			term.setCursorVisible(false);
			//MENU

			if(!started){
				System.out.println("MENU");
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
							if(y==13){
								snake = newSnake();
								snake.setDificulty(150);
								started=true;
								end=false;
							}
							if(y==15){
								snake=newSnake();
								snake.setDificulty(100);
								started=true;
								end=false;
							}
							if(y==17){	
								snake=newSnake();
								snake.setDificulty(50);
								started=true;
								end=false;
							}
							score=0;
							break;
						case ArrowDown:
							if(y>=13 && y<17)
								y+=2;
							break;
						case ArrowUp:
							if( y>13 && y<=17)
								y-=2;
							break;					
						}
					}
					String welcome="Welcome to Snake!";
					show(welcome,50-welcome.length()/2,9);
					show("Easy", 48,13);
					show("Medium", 48,15);
					show("Hard", 48,17);
					show("->",45,y);
					try{
						Thread.sleep(200);
					}
					catch (InterruptedException ie){
						ie.printStackTrace();
					}
				}
			}
			//MENU FIM
			printFoodSpikes(food, spikes);
			//MUDANÇAS DE DIRECÇÃO
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
					}

					term.clearScreen();
					snake.eat=snake.hasEaten(food);
					if(snake.eat)
						score+=10;
					show("Score: "+Integer.toString(score),50,0);
					for(Position pos:snake.body){
						if(pos.equals(snake.body.get(0))){
							show("ȣ", pos.x, pos.y);
						}
						else
							show("〇", pos.x, pos.y);
					}
					term.flush();
				}
				//ANDA NA MESMA DIRECÇÃO
				else{

					if(started){
						snake.makestep();
						term.clearScreen();
						snake.eat=snake.hasEaten(food);
						if(snake.eat)
							score+=10;
						show("Score: "+Integer.toString(score),50,0);
						for(Position pos:snake.body){
							if(pos.equals(snake.body.get(0))){
								show("ȣ", pos.x, pos.y);
							}
							else
								show("〇", pos.x, pos.y);
						}
					}
				}
			}
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

	private void checkCrashed(Cobra snake, LinkedList<Position> spikes) {
		boolean colided=snake.hitMe(snake);
		if(snake.gotSpikes(spikes) || colided){
			System.out.println(""+colided);
			snake.crashed=true;
			end=true;
		}
	}

	private Cobra newSnake() {
		Cobra snake;
		snake=new Cobra(rand.nextInt(MAX_X-10)+5, rand.nextInt(MAX_Y-10)+3, 5, getDirection());
		return snake;
	}

	private Direction getDirection() {
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

	private void outOfBounds(Cobra snake) {
		if(snake.body.getFirst().x<3 || snake.body.getFirst().y<3 || snake.body.getFirst().x>96 || snake.body.getFirst().y>26){
			snake.crashed=true;
			end=true;
		}

	}

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
				}
			}
			String scores="Your Score: "+Integer.toString(score);
			String[] gameover={" ____    ____   _   _   ___  ",
					           "|  __|  | __ | | | / | |  _| ",
					           "| | __  | |_|| |  |  | | |_  ",
					           "| |_| | |  _ | |  _  | |  _| ",
					           "|_____| |_| || |_| |_| |___| ",
					           "                             ",
					           " ____    _     _    ___   _____ ",
					           "| __ |  | |   | |  |  _| | __  |",
					           "| | ||  |  | |  |  | |_  | |_| |",
					           "| |_||   |     |   |  _| |    / ",
					           "|____|    |___|    |___| |_| \\_\\"};
			
			show(scores,50-scores.length()/2,18);
			for(int i=0;i<gameover.length;i++){
				show(gameover[i],50-gameover[i].length()/2,5+i);
			}
			String trya="Try Again?";
			show(trya,50-trya.length()/2,20);
			String menugo="Go to Main Menu";
			show(menugo,50-menugo.length()/2,22);
			show("->",x,y);
			try{
				Thread.sleep(200);
			}
			catch (InterruptedException ie){
				ie.printStackTrace();
			}
		}
		return snake;

	}

	private Cobra gameOverRestart(Cobra snake, int dificulty, int y) {
		LinkedList<Position> food;
		LinkedList<Position> spikes;
		if(y==20){
			started=true;
			snake=newSnake();
			snake.setDificulty(dificulty);
			snake.crashed=false;
			food=makeFood(new LinkedList<Position>());
			spikes=makeSpikes(new LinkedList<Position>());
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

	private void printFoodSpikes(LinkedList<Position> food,
			LinkedList<Position> spikes) {
		for(Position p:food){
			show("ѽ",p.x,p.y);
		}
		for(Position p:spikes){
			show("✴",p.x,p.y);
		}
	}

	private LinkedList<Position> makeFood(LinkedList<Position> food) {
		Random r=new Random();
		Position f;
		for(int i=0;i<4;i++){
			f=new Position(r.nextInt(90)+3, r.nextInt(20)+3);
			food.add(f);
		}
		return food;
	}

	private LinkedList<Position> makeSpikes(LinkedList<Position> Spikes) {
		Random r=new Random();
		Position f;
		for(int i=0;i<4;i++){
			f=new Position(r.nextInt(90)+3, r.nextInt(20)+3);
			Spikes.add(f);
		}
		return Spikes;

	}

	private void show(String str, int x, int y)
	{
		term.moveCursor(x, y);

		int len = str.length();

		for (int i = 0; i < len; i++)
		{
			term.putCharacter(str.charAt(i));
		}
	}

	public static void main(String[] args)
	{
		new Snake();
	}
}

