import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.depositbox.DepositBox;
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

@ScriptManifest(name = "WoodCuttingGuildYew", description = "Seventh Script", author = "lsjc12911",
        version = 1.0, category = Category.WOODCUTTING, image = "")

public class WCGuildYew extends AbstractScript {

    Random ran = new Random();

    State state;

    String treeName = "Yew";
    String interactName = "Chop down";
    String itemName = "Yew logs";
    GameObject tree = null;

    GroundItem item = null;
    String birdNest = "Bird nest";

    //Rune axe
    int animation = 867;
//    Dragon axe
//    int animation = 2846;

    Area treeArea = new Area(1584, 3477, 1598, 3495);
    Tile treeAreaTile = new Tile(1596, 3483);
    Area bankArea = new Area(1589, 3480, 1593, 3475);

    int[] travelSleep = {2000, 2200, 2300, 2400, 2500, 2600, 2800};
    int[] bankingSleep = {1500, 1750, 2000};
    int[] closeSleep = {1000, 1500, 1800, 2000};


    private enum State{
        FINDING_TREE, BANKING, BIRD_NEST, WOOD_CUTTING, GO2TREES;
    }

    public State getState(){
        item = GroundItems.closest(birdNest);

        if(Inventory.isFull()) {
            state = State.BANKING;
        } else if(!Inventory.isFull()){
            if(bankArea.contains(getLocalPlayer())){
                state = State.GO2TREES;
            } else {
                if(treeArea.contains(item)){
                    state = State.BIRD_NEST;
                } else if (!treeArea.contains(item)){
                    if(getLocalPlayer().getAnimation() == -1){
                        state = State.FINDING_TREE;
                    }else if(getLocalPlayer().getAnimation() == animation){
                        state = State.WOOD_CUTTING;
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
            sleepUntil(()-> getLocalPlayer().getAnimation() == animation, closeSleep[ran.nextInt(closeSleep.length)]);
        } else if(getState().equals(State.BANKING)) {
            //Since bank is closeby
            log("BANKING");
            if(Bank.openClosest()){
                Bank.depositAll(itemName);
                sleepUntil(()-> !Inventory.contains(itemName), bankingSleep[ran.nextInt(bankingSleep.length)]);
                if(Inventory.contains(birdNest)){
                    Bank.depositAll(birdNest);
                    sleepUntil(()-> !Inventory.contains(birdNest), bankingSleep[ran.nextInt(bankingSleep.length)]);
                }
                Bank.close();
            }else {
                sleepUntil(Bank::isOpen, 4000);
            }
        } else if (getState().equals(State.GO2TREES)) {
            Walking.walk(treeAreaTile);
            sleepUntil(()-> getLocalPlayer().getTile().equals(treeAreaTile), travelSleep[ran.nextInt(travelSleep.length)]);
        } else if (getState().equals(State.BIRD_NEST)) {
            log("BIRD NEST FOUND!");
            item.interact("Take");
            sleepUntil(() -> !treeArea.contains(item), closeSleep[ran.nextInt(closeSleep.length)]);
        } else if (getState().equals(State.WOOD_CUTTING)) {
            log("WOODCUTTING");
            sleep(1500);
        }

        return 500;
    }
}
