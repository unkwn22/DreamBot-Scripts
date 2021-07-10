import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;


@ScriptManifest(name = "Thieving", description = "Fifth script", author = "lsjc12911",
        version = 1.0, category = Category.THIEVING, image = "")
public class Thieving extends AbstractScript {

    State state;

    String food = "Lobster";

    private enum State{
        THIEVING, OPEN_ALL, STUNNED, HEAL
    }

    //indicating the player's situation
    private State getState(){
        //start is the starting radius we indicated
        log(getLocalPlayer().getHealthPercent());
        if(Inventory.count("Coin pouch") == 28){
            state = State.OPEN_ALL;
        } else if(getLocalPlayer().getAnimation() == -1 && Inventory.count("Coin pouch") < 28){
           state = State.THIEVING;
        } else if(getLocalPlayer().getAnimation() == 424){
            state = State.STUNNED;
        } else if(getLocalPlayer().getHealthPercent() <= 32){
            state = State.HEAL;
        }
        return state;
    }

    @Override
    public int onLoop() {
        //the loop is constantly getting the state of  the player every tick
        if(getState().equals(State.THIEVING)){
            NPC man = NPCs.closest("MAN");
            man.interact("Pickpocket");
            sleep(500, 1500);
        }else if(getState().equals(State.OPEN_ALL)){
            Inventory.interact("Coin pouch", "Open-all");
            sleepUntil(() -> !Inventory.contains("Coin pouch"), 1500);
        }else if(getState().equals(State.STUNNED)){
            log("Stunned!");
            sleep(3500);
        }else if(getState().equals(State.HEAL)){
            Inventory.interact(food, "Eat");
        }

        return 0;
    }
}
