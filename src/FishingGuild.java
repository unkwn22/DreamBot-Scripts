import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.Random;

@ScriptManifest(name = "FishingGuild", description = "Fourth Script", author = "lsjc12911",
        version = 1.0, category = Category.FISHING, image = "")

public class FishingGuild extends AbstractScript{

    State state;

    Random randomNum = new Random();

    Tile fishingTile = new Tile(2599, 3422);
    Area fishingArea = new Area(2595, 3419, 2605, 3426);
    Tile bankTile = new Tile(2587, 3419);
    Area bankArea = new Area(2586, 3418, 2588, 3421);

    NPC fishingSpot;
    GameObject bank;

    private enum State{
        FISH, FISHING, BANKING, MOVE2FISH, MOVE2BANK
    }

    private State getState(){

        if(fishingArea.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() == -1 && !Inventory.isFull()){
            log("fish");
            state = State.FISH;
            sleep(1000, 1050);
        } else if(fishingArea.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() != -619 && Inventory.isFull()){
            log("banking");
            state = State.MOVE2BANK;
        } else if(bankArea.contains(getLocalPlayer()) && Inventory.isFull()){
            state = State.BANKING;
        } else if(bankArea.contains(getLocalPlayer()) && !Inventory.isFull()){
            state = State.MOVE2FISH;
        } else {
            state = State.FISHING;
        }
        return state;
    }

    @Override
    public int onLoop() {
        if(getState().equals(State.FISH)){
            fishingSpot = NPCs.closest(f -> f != null && f.getName().contentEquals("Fishing spot") && fishingArea.contains(f));
            fishingSpot.interact("Cage");
            sleepUntil(() -> getLocalPlayer().getAnimation() == -1, 100000);
        } else if(getState().equals(State.BANKING)){
            if(Bank.openClosest()){
                Bank.deposit("Raw lobster", 27);
                if(Bank.count("Raw lobster") == 600){
                    stop();
                }
            }else {
                sleepUntil(Bank::isOpen, 3000);
            }
        } else if(getState().equals(State.MOVE2FISH)){
            Walking.walk(fishingTile);
            sleepUntil(() -> getLocalPlayer().getTile().equals(fishingTile), 1000);
        } else if(getState().equals(State.MOVE2BANK)){
            Walking.walk(bankTile);
            sleepUntil(() -> getLocalPlayer().getTile().equals(bankTile), 1000);
        }
        return 0;
    }
}