import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class NewBot implements Bot {
	public static void main(String[] args) {
		Ants.run(new NewBot());
	}

	public NewBot() {
		System.err.println("HELLO!");
	}
	
	private Random random;
	private int[][] influenceMap;
	private List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));

	public void setup(Ants ants) {
		influenceMap = new int[ants.cols()][ants.rows()];
		random = new Random(42);
	}


	// bot logic, run once per turn
	public void do_turn(Ants ants) {
		Set<Tile> destinations = new HashSet<Tile>();
		for (Tile location : ants.myAnts()) {
			// Mark all of the water tiles as 'Don't go here'
			for (Aim aim : directions) {
				Tile nextTile = ants.tile(location, aim);
				if (!ants.ilk(nextTile).isPassable()) {
					influenceMap[nextTile.col()][nextTile.row()] = 300;
				}
			}

			// Choose the lowest location around us
			// Randomly choosing one if they are equal
			int min = 255;
			List<SimpleEntry<Tile, Aim>> possibilities = new LinkedList<SimpleEntry<Tile, Aim>>();
			for (Aim aim : directions) {
				Tile tile = ants.tile(location, aim);
				// If the tile has nothing there and another ant isn't already
				// ordered to move there for the next turn
				if (ants.ilk(tile).isUnoccupied() && !destinations.contains(tile)) {
					int val = influenceMap[tile.col()][tile.row()];
					if (val < min) {
						possibilities.clear();
						min = val;
						possibilities.add(new SimpleEntry<Tile, Aim>(tile, aim));
					} else if (val == min) {
						possibilities.add(new SimpleEntry<Tile, Aim>(tile, aim));
					}
				}
			}

			// Okay we have all of the possible moves
			if (!possibilities.isEmpty()) {
				// Choose one at random to move to
				// Also, add the order to the destination list so another
				// ant doesn't try and move to the same location this turn
				SimpleEntry<Tile, Aim> order = possibilities.get(random.nextInt(possibilities.size()));	
				destinations.add(order.getKey());
				ants.issueOrder(location, order.getValue());
			} else {
				// Uh-oh, no valid moves were found!
			}

			influenceMap[location.col()][location.row()] = Math.min(255, 
					influenceMap[location.col()][location.row()]+10);
		}
	}
}
