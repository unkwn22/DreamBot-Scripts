import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;

@ScriptManifest(name = "Agility", description = "First script", author = "lsjc12911",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class Agility extends AbstractScript {

    State state;

    //radius
    Area start = new Area(2474, 3438, 2476, 3436);
    //single tile
    Tile startTile = new Tile(2474, 3436);

    Tile endLogTile = new Tile(2474,3429);
    Tile endNet1Tile = new Tile(2473, 3423, 1);
    Tile endTree1Tile = new Tile(2473, 3420, 2);
    Tile endRopeTile = new Tile(2483,3420, 2);
    Tile endTree2Tile = new Tile(2487, 3420);
    Tile endNet2Tile = new Tile(2485,3428, 0);
    Tile endPipeTile = new Tile(2484,3437);

    GameObject log;
    GameObject net1;
    GameObject tree1;
    GameObject rope;
    GameObject tree2;
    GameObject net2;
    GameObject pipe;

    Tile logTile = new Tile(2474,3435);
    Tile net1Tile = new Tile(2473,3425);
    Tile tree1Tile = new Tile(2473,3422, 1);
    Tile ropeTile = new Tile(2478,3420, 2);
    Tile tree2Tile = new Tile(2486, 3419, 2);
    Tile net2Tile = new Tile(2485, 3426);
    Tile pipeTile = new Tile(2484, 3431);

    @Override
    public int onLoop() {
        if(getState().equals(State.LOG)){
            log = GameObjects.closest(c -> c != null && c.getName().contentEquals("Log balance") && c.getTile().equals(logTile));
            log.interact("Walk-across");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endLogTile), 10000);
        }else if(getState().equals(State.NET1)){
            net1 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Obstacle net") && c.getTile().equals(net1Tile));
            net1.interact("Climb-over");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endNet1Tile), 6000);
        }else if(getState().equals(State.TREE1)){
            log = GameObjects.closest(c -> c != null && c.getName().contentEquals("Tree branch") && c.getTile().equals(tree1Tile));
            log.interact("Climb");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endTree1Tile), 6000);
        }else if(getState().equals(State.ROPE)){
            log = GameObjects.closest(c -> c != null && c.getName().contentEquals("Balancing rope") && c.getTile().equals(ropeTile));
            log.interact("Walk-on");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endRopeTile), 6000);
        }else if(getState().equals(State.TREE2)){
            log = GameObjects.closest(c -> c != null && c.getName().contentEquals("Tree branch") && c.getTile().equals(tree2Tile));
            log.interact("Climb-down");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endTree2Tile), 6000);
        }else if(getState().equals(State.NET2)){
            log = GameObjects.closest(c -> c != null && c.getName().contentEquals("Obstacle net") && c.getTile().equals(net2Tile));
            log.interact("Climb-over");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endNet2Tile), 6000);
        }else if(getState().equals(State.PIPE)){
            log = GameObjects.closest(c -> c != null && c.getName().contentEquals("Obstacle pipe") && c.getTile().equals(pipeTile));
            log.interact("Squeeze-through");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endPipeTile), 10000);
        }
        return 500;
    }

    private enum State{
        LOG, NET1, TREE1, ROPE, TREE2, NET2, PIPE
    }

    private State getState(){
        if(start.contains(getLocalPlayer()) || getLocalPlayer().getTile().equals(endPipeTile)){
            state = State.LOG;
        }else if(getLocalPlayer().getTile().equals(endLogTile)){
            state = State.NET1;
        }else if(getLocalPlayer().getTile().equals(endNet1Tile)){
            state = State.TREE1;
        }else if(getLocalPlayer().getTile().equals(endTree1Tile)){
            state = State.ROPE;
        }else if(getLocalPlayer().getTile().equals(endRopeTile)){
            state = State.TREE2;
        }else if(getLocalPlayer().getTile().equals(endTree2Tile)){
            state = State.NET2;
        }else if(getLocalPlayer().getTile().equals(endNet2Tile)){
            state = State.PIPE;
        }
        return state;
    }

}
