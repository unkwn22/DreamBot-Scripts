import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;

@ScriptManifest(name = "Agility", description = "First script", author = "lsjc12911",
        version = 1.0, category = Category.AGILITY, image = "")
public class GnomeStrongholdAgilityCourse extends AbstractScript {

    State state;

    //radius
    Area start = new Area(2474, 3438, 2476, 3436);
    //single tile
    Tile startTile = new Tile(2474, 3436);

    //indicating end tiles
    Tile endLogTile = new Tile(2474,3429);
    Tile endNet1Tile = new Tile(2473, 3423, 1);
    Tile endTree1Tile = new Tile(2473, 3420, 2);
    Tile endRopeTile = new Tile(2483,3420, 2);
    Tile endTree2Tile = new Tile(2487, 3420);
    Tile endNet2Tile = new Tile(2485,3428, 0);
    Tile endPipeTile = new Tile(2484,3437);

    //indicating obstacle tiles
    Tile logTile = new Tile(2474,3435);
    Tile net1Tile = new Tile(2473,3425);
    Tile tree1Tile = new Tile(2473,3422, 1);
    Tile ropeTile = new Tile(2478,3420, 2);
    Tile tree2Tile = new Tile(2486, 3419, 2);
    Tile net2Tile = new Tile(2485, 3426);
    Tile pipeTile = new Tile(2484, 3431);

    //getGameObjects has been deprecated -> GameObject.closest()...
    GameObject log;
    GameObject net1;
    GameObject tree1;
    GameObject rope;
    GameObject tree2;
    GameObject net2;
    GameObject pipe;

    private enum State{
        LOG, NET1, TREE1, ROPE, TREE2, NET2, PIPE
    }

    //indicating the player's situation
    private State getState(){
        //start is the starting radius we indicated
        if(start.contains(getLocalPlayer()) || getLocalPlayer().getTile().equals(endPipeTile)){
            //then the next task the player should do is LOG enum
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

    @Override
    public int onLoop() {
        //the loop is constantly getting the state of  the player every tick
        if(getState().equals(State.LOG)){
            //indicating the log object name and its position
            log = GameObjects.closest(c -> c != null && c.getName().contentEquals("Log balance") && c.getTile().equals(logTile));
            //interact transaction
            log.interact("Walk-across");
                                                                        //giving the bot a flexible time inorder to proceed to the next task
            sleepUntil(() -> getLocalPlayer().getTile().equals(endLogTile), 10000);
        }else if(getState().equals(State.NET1)){
            net1 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Obstacle net") && c.getTile().equals(net1Tile));
            net1.interact("Climb-over");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endNet1Tile), 6000);
        }else if(getState().equals(State.TREE1)){
            tree1 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Tree branch") && c.getTile().equals(tree1Tile));
            log.interact("Climb");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endTree1Tile), 6000);
        }else if(getState().equals(State.ROPE)){
            rope = GameObjects.closest(c -> c != null && c.getName().contentEquals("Balancing rope") && c.getTile().equals(ropeTile));
            log.interact("Walk-on");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endRopeTile), 6000);
        }else if(getState().equals(State.TREE2)){
            tree2 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Tree branch") && c.getTile().equals(tree2Tile));
            log.interact("Climb-down");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endTree2Tile), 6000);
        }else if(getState().equals(State.NET2)){
            net2 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Obstacle net") && c.getTile().equals(net2Tile));
            log.interact("Climb-over");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endNet2Tile), 6000);
        }else if(getState().equals(State.PIPE)){
            pipe = GameObjects.closest(c -> c != null && c.getName().contentEquals("Obstacle pipe") && c.getTile().equals(pipeTile));
            log.interact("Squeeze-through");
            sleepUntil(() -> getLocalPlayer().getTile().equals(endPipeTile), 10000);
        }

        return 500;
    }

}
