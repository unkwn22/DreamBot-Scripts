import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.GroundItem;

@ScriptManifest(name = "SandCrabs", description = "Sixth script", author = "lsjc12911",
        version = 1.0, category = Category.COMBAT, image = "")
public class SandCrabs extends AbstractScript {

    State state;
    String food = "Lobster";
    GroundItem item = null;
    String itemName = "Iron arrow";

    int idle = 0;

    Tile returnTile = new Tile(1749, 3469);
    Tile awayTile = new Tile(1750,3484);
    Tile awayTile2 = new Tile(1748, 3496);

    Area area = new Area(1745, 3467, 1752, 3472);

    final int PITCH = 383;
    final int YAW = 0;

    private enum State{
        FIGHTING, RESET_AGRO, RETURNING, HEAL, PICK_UP
    }

    private State getState(){
        item = GroundItems.closest(itemName);

        if(!getLocalPlayer().getTile().equals(returnTile)){
            state = State.RETURNING;
        } else if(getLocalPlayer().getHealthPercent() <= 30){
            state = State.HEAL;
        } else if(!getLocalPlayer().isInteractedWith() && getLocalPlayer().getTile().equals(returnTile)){
            idle++;
            sleep(1000);
        } else if(getLocalPlayer().isInteractedWith() && getLocalPlayer().getTile().equals(returnTile) && getLocalPlayer().getAnimation() == 426){
            state = State.FIGHTING;
        }

        if(area.contains(item) && item.getAmount() > 10) {
            state = State.PICK_UP;
        }

        if(idle > 3){
            state = State.RESET_AGRO;
        }

        return state;
    }

    @Override
    public int onLoop() {

        if(Camera.getYaw() != YAW && Camera.getPitch() != PITCH){
            log("Initiating camera turn");
            Camera.keyboardRotateTo(YAW, PITCH);
            sleepUntil(() -> Camera.getYaw() == YAW && Camera.getPitch() == PITCH, 1000);
        }

        if(getState().equals(State.FIGHTING)){
            log("FIGHTING");
            log(getLocalPlayer().getHealthPercent());
            sleep(2000);
        } else if(getState().equals(State.RESET_AGRO)){
            Walking.walk(awayTile);
            log("RESETTING AGRO");
            sleepUntil(() -> getLocalPlayer().getTile().equals(awayTile), 5000);
            Walking.walk(awayTile2);
            sleepUntil(() -> getLocalPlayer().getTile().equals(awayTile2), 6000);
            Walking.walk(awayTile);
            sleepUntil(() -> getLocalPlayer().getTile().equals(awayTile), 5000);
            idle = 0;
        } else if(getState().equals(State.RETURNING)){
            Walking.walk(returnTile);
            log("RETURNING");
            sleep(1000);
            sleepUntil(() -> getLocalPlayer().getTile().equals(returnTile), 3000);
        } else if(getState().equals(State.HEAL)){
            log("HEALING");
            Inventory.interact(food, "Eat");
            sleepUntil(() -> getLocalPlayer().getHealthPercent() >= 32, 3000);
        } else if(getState().equals(State.PICK_UP)){
            item.interact("Take");
            sleep(1000,2000);
        }
        return 500;
    }
}
