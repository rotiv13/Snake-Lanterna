import java.util.LinkedList;
import java.util.Random;

class SnakeModel{
	LinkedList<Position> body = new LinkedList<>();
	private Random rand = new Random();
	private Direction direction;
	boolean crashed = false;
	boolean eat = false;
	private int dificulty = 300;
	private static final int HARD = 70;
	private static final int MEDIUM = 120;
	private static final int EASY = 170;
	LinkedList<Position> food = new LinkedList<>();
	LinkedList<Position> spikes = new LinkedList<>();
	SnakeModel(int x,int y,int length, Direction dir){
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
		return !(direction == Direction.LEFT && dir == Direction.RIGHT) && !(direction == Direction.UP && dir == Direction.DOWN) && !(direction == Direction.DOWN && dir == Direction.UP) && !(direction == Direction.RIGHT && dir == Direction.LEFT);
	}

	/**
	 * Prevents the snake on colliding with itself when pressing an opposite direction of which the snake is going
	 * @param dir
	 */
	private void dontGoTheOppositeDirection(Direction dir) {
		if(oposite(dir)){
			this.direction=dir;

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

	boolean equals(Position pos){
		return body.get(0).x == pos.x && body.get(0).y == pos.y;

	}
	/**
	 * Makes all the targets/obstacles for the game
	 */
	void produceObstaclesTargets() {
		food=new LinkedList<Position>();
		spikes=new LinkedList<Position>();
		makeFood();
		makeSpikes();
	}

	/**
	 * Makes food for the snake to eat
	 * @return
	 */
	private void makeFood() {
		Random r=new Random();

		int size=0;
		if(getDificulty()==EASY){
			size=15;
		}
		if(getDificulty()==MEDIUM){
			size=10;
		}
		if(getDificulty()==HARD){
			size=5;
		}
		produceFood(r, size);


	}

	/**
	 * Makes those nasty obstacles
	 * @return
	 */
	private void makeSpikes() {
		Random r=new Random();
		int size=0;
		if(getDificulty()==EASY)
			size=10;
		if(getDificulty()==MEDIUM)
			size=20;
		if(getDificulty()==HARD)
			size=35;
		produceSpikes( r, size);
	}

	/**
	 * Produces food on with the size, that depends on the dificulty
	 * @return
	 */
	private void produceFood( Random r, int size) {
		Position f;
		for(int i=0;i<size;i++){
			f=new Position(r.nextInt(90)+3, r.nextInt(20)+3);
			food.add(f);
		}
	}

	/**
	 * Produces spikes on with the size, that depends on the dificulty 
	 * @return
	 */
	private void produceSpikes( Random r, int size) {
		Position f;
		for(int i=0;i<size;i++){
			f=new Position(r.nextInt(90)+3, r.nextInt(20)+3);
			spikes.add(f);
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
		}
	}

	/**
	 * Adss a new end of tail
	 * @param auxx
	 * @param auxy
     */
	private void addEndOfTail(int auxx, int auxy) {
		body.add(new Position(auxx,auxy));
	}

	/**
	 * Checks if the snake has eaten a fruit
	 * @return
	 */
	boolean hasEaten(){
		for(int i=0;i<food.size();i++){
			if(equals(food.get(i))){
				food.set(i,new Position(rand.nextInt(90)+4, rand.nextInt(20)+4));
				return true;
			}
		}	
		return false;
	}

	/**
	 * Checks if the snake has collided with a spike
	 * @return
	 */
	boolean gotSpikes(){
		for (Position spike : spikes) {
			if (equals(spike)) {
				return true;
			}
		}	
		return false;
	}

	/**
	 * Check if it collided with itself
	 * @return
	 */
	boolean hitMe(){
		for(int i=2;i<body.size();i++){
			if(equals(body.get(i))){
				return true;
			}
		}
		return false;
	}
	//END OF SNAKE CONTROL ----------------------------------------------------------
	private void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Makes the next step
	 */
	void makestep(){
		makeStep(direction);
	}

	/**
	 * Pushes forward in that direction dir
	 * @param dir
	 */
	void makeStep(Direction dir){
		eatAndGrow();
		dontGoTheOppositeDirection(dir);

	}

	int getDificulty() {
		return dificulty;
	}

	void setDificulty(int dificulty) {
		this.dificulty = dificulty;
		produceObstaclesTargets();
	}
}