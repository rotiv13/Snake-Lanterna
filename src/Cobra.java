import java.util.LinkedList;
import java.util.Random;

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