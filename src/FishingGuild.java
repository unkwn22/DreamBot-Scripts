import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Random;

@ScriptManifest(name = "FishingGuild", description = "Fourth Script", author = "lsjc12911",
        version = 1.0, category = Category.FISHING, image = "")

public class FishingGuild extends AbstractScript{

    State state;

    Random ran = new Random();

    int[][] tiles = {{2600, 3422}, {2604, 3421}, {2604,3425}};

    Tile fishingTile = null;
    int randomNum = 0;
    Tile lastTile = null;
    int idle = 0;

    Area fishingArea = new Area(2594, 3418, 2606, 3427);
    Tile bankTile = new Tile(2587, 3419);
    Area bankArea = new Area(2586, 3418, 2588, 3421);

    private enum State{
        FISH, FISHING, BANKING, MOVE2FISH, MOVE2BANK
    }

    private State getState(){
        if((fishingArea.contains(getLocalPlayer())) && getLocalPlayer().getAnimation() == -1  && !Inventory.isFull()){
            log("FISH");
            state = State.FISH;
            log(idle);
            idle++;
        } else if(!bankArea.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() == -1 && Inventory.isFull()){
            log("MOVE2BANK");
            state = State.MOVE2BANK;
        } else if(bankArea.contains(getLocalPlayer()) && Inventory.isFull()){
            log("BANKING");
            state = State.BANKING;
        } else if(!fishingArea.contains(getLocalPlayer()) && !Inventory.isFull()){
            log("MOVE2FISH");
            state = State.MOVE2FISH;
        } else {
            log("FISHING");
            state = State.FISHING;
            idle = 0;
        }

        //idle state will initiate went idling for 3seconds
        if(idle > 3){
            log("IDLE initiating");
            state = State.MOVE2FISH;
        }
        return state;
    }

    @Override
    public int onLoop() {
        if(getState().equals(State.FISH)){
            //같은 기능
//            NPC fishingSpot = NPCs.closest(f -> f != null && f.getName().contentEquals("Fishing spot") && fishingArea.contains(f));
            NPC fishingSpot = NPCs.closest("Fishing spot");
            fishingSpot.interact("Cage");
            sleep(2000);
            sleepUntil(() -> getLocalPlayer().getAnimation() == -1, 8000);
        } else if(getState().equals(State.BANKING)){
            if(Bank.openClosest()){
                Bank.deposit("Raw lobster", 27);
//                if(Bank.count("Raw lobster") > 200){
//                    Bank.close();
//                    Tabs.logout();
//                    stop();
//                }
                Bank.close();
            }else {
                idle = 0;
                sleepUntil(Bank::isOpen, 4000);
            }
        } else if(getState().equals(State.MOVE2FISH)){
            //reseting idle count
            idle = 0;
            //using last tile log inorder to move when idling
            if(lastTile == null){
                randomNum = ran.nextInt(3);
                fishingTile = new Tile(tiles[randomNum][0], tiles[randomNum][1]);
                lastTile = fishingTile;
            }else{
                while(true){
                    randomNum = ran.nextInt(3);
                    fishingTile = new Tile(tiles[randomNum][0], tiles[randomNum][1]);
                    //wont break till randomized tile is not the same as last tile
                    if(fishingTile != lastTile){
                        lastTile = fishingTile;
                        break;
                    }
                }
            }
            Walking.walk(fishingTile);
            log("moving to: " + fishingTile);
            sleepUntil(() -> getLocalPlayer().getTile().equals(fishingTile), 6000);
        } else if(getState().equals(State.MOVE2BANK)){
            Walking.walk(bankTile);
            sleepUntil(() -> getLocalPlayer().getTile().equals(bankTile), 1000);
        } else if(getState().equals(State.FISHING)){
            sleep(1000);
        }
        return 0;
    }
}
