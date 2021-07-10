import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;


@ScriptManifest(name = "Thieving", description = "Fifth script", author = "lsjc12911",
        version = 1.0, category = Category.THIEVING, image = "")
public class Thieving extends AbstractScript {

    State state;

    String food = "Lobster";
    String targetNpc = "Warrior woman";

    Area pickingArea = new Area(2624, 3307, 2643, 3288);
    Tile pickTile = new Tile(2631, 3295);
    Area bankArea = new Area(2649, 3286, 2654, 3281);
    Tile bankTile = new Tile(2651, 3284);

    private enum State{
        THIEVING, OPEN_ALL, STUNNED, HEAL, MOVE2BANK, RESTOCK, MOVE2PICK
    }

    //indicating the player's situation
    private State getState(){
        //start is the starting radius we indicated
        log(getLocalPlayer().getHealthPercent());

        if(Inventory.count("Coin pouch") == 28){
            state = State.OPEN_ALL;
        } else if(!getLocalPlayer().isInteractedWith() && Inventory.count("Coin pouch") < 28 && pickingArea.contains(getLocalPlayer()) && Inventory.contains(food)){
           state = State.THIEVING;
        } else if(getLocalPlayer().getAnimation() == 424){
            state = State.STUNNED;
        } else if(!Inventory.contains(food) && !bankArea.contains(getLocalPlayer())){
            state = State.MOVE2BANK;
        } else if(bankArea.contains(getLocalPlayer()) && !Inventory.contains(food)){
            state = State.RESTOCK;
        } else if(!pickingArea.contains(getLocalPlayer()) && Inventory.contains(food)){
            state = State.MOVE2PICK;
        }

        if(getLocalPlayer().getHealthPercent() <= 32) {
            state = State.HEAL;
        }
        return state;
    }

    @Override
    public int onLoop() {
        //the loop is constantly getting the state of  the player every tick
        if(getState().equals(State.THIEVING)){
            NPC npc = NPCs.closest(targetNpc);
            npc.interact("Pickpocket");
            log("pickpocketing");
            sleep(500, 1500);
        }else if(getState().equals(State.OPEN_ALL)){
            Inventory.interact("Coin pouch", "Open-all");
            sleepUntil(() -> !Inventory.contains("Coin pouch"), 1500);
        }else if(getState().equals(State.STUNNED)){
            log("Stunned!");
            sleep(3500);
        }else if(getState().equals(State.HEAL)){
            log("Eating!");
            Inventory.interact(food, "Eat");
            sleep(1000);
        }else if(getState().equals(State.MOVE2BANK)){
            Walking.walk(bankTile);
            log("Moving to bank");
            sleep(1000);
            sleepUntil(() -> getLocalPlayer().getTile().equals(bankTile), 6000);
        }else if(getState().equals(State.RESTOCK)){
            log("BANKING");
            if(Bank.openClosest()){
                Bank.withdraw("Lobster", 25);
                Bank.close();
            }else {
                sleepUntil(Bank::isOpen, 4000);
            }
        }else if(getState().equals(State.MOVE2PICK)){
            Walking.walk(pickTile);
            log("Moving back to pick location");
            sleepUntil(() -> getLocalPlayer().getTile().equals(pickTile), 4000);
        }

        return 500;
    }
}
