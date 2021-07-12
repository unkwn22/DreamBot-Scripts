import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.Random;

@ScriptManifest(name = "DraynorWillow", description = "Seventh Script", author = "lsjc12911",
        version = 1.0, category = Category.WOODCUTTING, image = "")

public class DraynorWillow extends AbstractScript {

    Random ran = new Random();

    State state;

    String treeName = "Willow";
    String interactName = "Chop down";
    String itemName = "Willow logs";

    GroundItem item = null;
    String birdNest = "Bird nest";

    int animation = 867;

    GameObject tree = null;

    Area treeArea = new Area(3081, 3238, 3091, 3224);
    Area bankArea = new Area(3088, 3246, 3097, 3240);
    Tile treeAreaTile = new Tile(3088, 3237);

    int[] randomSleep = {500, 1000, 2000, 3000};



    private enum State{
        FINDING_TREE, BANKING, BIRD_NEST, WOOD_CUTTING, GO2TREES;
    }

    public State getState(){
        item = GroundItems.closest(birdNest);

        if(Inventory.isFull()) {
            state = State.BANKING;
        } else if(!Inventory.isFull()){
            if(bankArea.contains(getLocalPlayer())) {
                state = State.GO2TREES;
            } else if(treeArea.contains(getLocalPlayer())) {
                if(treeArea.contains(item)){
                    state = State.BIRD_NEST;
                } else if(!treeArea.contains(item)){
                    if(getLocalPlayer().getAnimation() == -1) {
                        state = State.FINDING_TREE;
                    } else if(getLocalPlayer().getAnimation() == animation) {
                        state =State.WOOD_CUTTING;
                    }
                }
            }
        }
        return state;
    }

    @Override
    public int onLoop() {

        if(getState().equals(State.FINDING_TREE)) {
            tree = GameObjects.closest(treeName);
            tree.interact(interactName);
            log("FINDING TREE");
            sleepUntil(()-> getLocalPlayer().getAnimation() == animation, randomSleep[ran.nextInt(2) + 2]);
        } else if(getState().equals(State.BANKING)) {
            //Since bank is closeby
            if(Bank.openClosest()){
                Bank.depositAll(itemName);
                sleepUntil(()-> !Inventory.contains(itemName), randomSleep[ran.nextInt(randomSleep.length)]);
                Bank.depositAll(birdNest);
                sleepUntil(()-> !Inventory.contains(birdNest), randomSleep[ran.nextInt(randomSleep.length)]);
                Bank.close();
            }else {
                sleepUntil(Bank::isOpen, 4000);
            }
        } else if (getState().equals(State.GO2TREES)) {
            Walking.walk(treeAreaTile);
            sleepUntil(()-> getLocalPlayer().getTile().equals(treeAreaTile), randomSleep[ran.nextInt(randomSleep.length)]);
        } else if (getState().equals(State.BIRD_NEST)) {
            item.interact("Take");
            sleepUntil(() -> !treeArea.contains(item), randomSleep[ran.nextInt(randomSleep.length)]);
        } else if (getState().equals(State.WOOD_CUTTING)) {
            log("WOODCUTTING");
            sleep(1500);
        }

        return 500;
    }
}
