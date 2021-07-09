import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;

@ScriptManifest(name = "DraynorRoofTop", description = "Second Script", author = "lsjc12911",
        version = 1.0, category = Category.WOODCUTTING, image = "")

public class DryanorRoopTops extends AbstractScript{

    State state;

    GroundItem item = null;

    Area firstRoof = new Area(3097, 3281, 3102, 3277, 3);
    Area secondRoof = new Area(3088, 3275, 3090, 3278, 3);
    Area thirdRoof = new Area(3092, 3267, 3094, 3265, 3);
    Area fourthRoof = new Area(3088, 3261, 3088, 3257, 3);
    Area fifthRoof = new Area(3087, 3255, 3094, 3255, 3);
    Area sixthRoof = new Area(3096, 3261, 3101, 3256, 3);

    Tile wall1End = new Tile(3102, 3279, 3);
    Tile rope1End = new Tile(3090, 3276, 3);
    Tile rope2End = new Tile(3092, 3266, 3);
    Tile narrowEnd = new Tile(3088, 3261, 3);
    Tile wall2End = new Tile(3088, 3255, 3);
    Tile gapEnd = new Tile(3096, 3256, 3);
    Tile crateEnd = new Tile(3103, 3261);

    Tile wall1Tile = new Tile(3103, 3279);
    Tile rope1Tile = new Tile(3098, 3277, 3);
    Tile rope2Tile = new Tile(3092, 3276, 3);
    Tile narrowTile = new Tile(3089, 3264, 3);
    Tile wall2Tile = new Tile(3088,3256, 3);
    Tile gapTile = new Tile(3095, 3255, 3);
    Tile crateTile = new Tile(3102, 3261, 3);

    GameObject wall1;
    GameObject rope1;
    GameObject rope2;
    GameObject narrow;
    GameObject wall2;
    GameObject gap;
    GameObject crate;


    private enum State{
        WALL1, ROPE1, ROPE2, NARROW, WALL2, GAP, CRATE, ITEM
    }

    private State getState(){
        item = GroundItems.closest("Mark of grace");
        if(getLocalPlayer().getTile().equals(crateEnd) && getLocalPlayer().getAnimation() == -1){
            state = State.WALL1;
        }else if(firstRoof.contains(getLocalPlayer())){
            if(firstRoof.contains(item) && firstRoof.contains(getLocalPlayer())){
                MethodProvider.log("ITEM");
                state = State.ITEM;
            }else if (firstRoof.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() == -1){
                MethodProvider.log("ROPE1");
                state = State.ROPE1;
            }
        }else if(secondRoof.contains(getLocalPlayer())){
            if(secondRoof.contains(item) && secondRoof.contains(getLocalPlayer())){
                MethodProvider.log("ITEM");
                state = State.ITEM;
            }else if(secondRoof.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() == -1){
                MethodProvider.log("ROPE2");
                state = State.ROPE2;
            }
        }else if(thirdRoof.contains(getLocalPlayer())){
            if(thirdRoof.contains(item) && thirdRoof.contains(getLocalPlayer())){
                MethodProvider.log("ITEM");
                state = State.ITEM;
            }else if (thirdRoof.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() == -1){
                MethodProvider.log("NARROW");
                state = State.NARROW;
            }
        }else if(fourthRoof.contains(getLocalPlayer())){
            if(fourthRoof.contains(item) && fourthRoof.contains(getLocalPlayer())){
                MethodProvider.log("ITEM");
                state = State.ITEM;
            }else if(fourthRoof.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() == -1){
                MethodProvider.log("WALL2");
                state = State.WALL2;
            }
        }else if(fifthRoof.contains(getLocalPlayer())){
            if(fifthRoof.contains(item) && fifthRoof.contains(getLocalPlayer())){
                MethodProvider.log("ITEM");
                state = State.ITEM;
            }else if(fifthRoof.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() == -1){
                MethodProvider.log("GAP");
                state = State.GAP;
            }
        }else if(sixthRoof.contains(getLocalPlayer())){
            if(sixthRoof.contains(item) && sixthRoof.contains(getLocalPlayer())){
                MethodProvider.log("ITEM");
                state = State.ITEM;
            }else if(sixthRoof.contains(getLocalPlayer()) && getLocalPlayer().getAnimation() == -1){
                MethodProvider.log("CRATE");
                state = State.CRATE;
            }
        }else{
            state = State.WALL1;
        }
        return state;
    }

    @Override
    public int onLoop() {
        if(getState().equals(State.ITEM)){
            item.interact("Take");
            sleep(2000,3000);
        } else if(getState().equals(State.WALL1)){
            wall1 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Rough wall") && c.getTile().equals(wall1Tile));
            wall1.interact("Climb");
            sleepUntil(() -> getLocalPlayer().getTile().equals(wall1End), 6000);
        }else if(getState().equals(State.ROPE1)){
            rope1 = wall1 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Tightrope") && c.getTile().equals(rope1Tile));
            rope1.interact("Cross");
            sleepUntil(() -> getLocalPlayer().getTile().equals(rope1End), 10000);
        }else if(getState().equals(State.ROPE2)){
            rope2 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Tightrope") && c.getTile().equals(rope2Tile));
            rope2.interact("Cross");
            sleepUntil(() -> getLocalPlayer().getTile().equals(rope2End), 10000);
        }else if(getState().equals(State.NARROW)){
            narrow = GameObjects.closest(c -> c != null && c.getName().contentEquals("Narrow wall") && c.getTile().equals(narrowTile));
            narrow.interact("Balance");
            sleepUntil(() -> getLocalPlayer().getTile().equals(narrowEnd), 6000);
        }else if(getState().equals(State.WALL2)){
            wall2 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Wall") && c.getTile().equals(wall2Tile));
            wall2.interact("Jump-up");
            sleepUntil(() -> getLocalPlayer().getTile().equals(wall2End), 6000);
        }else if(getState().equals(State.GAP)){
            gap = GameObjects.closest(c -> c != null && c.getName().contentEquals("Gap") && c.getTile().equals(gapTile));
            gap.interact("Jump");
            sleepUntil(() -> getLocalPlayer().getTile().equals(gapEnd), 6000);
        }else if(getState().equals(State.CRATE)){
            crate = GameObjects.closest(c -> c != null && c.getName().contentEquals("Crate") && c.getTile().equals(crateTile));
            crate.interact("Climb-down");
            sleepUntil(() -> getLocalPlayer().getTile().equals(crateEnd), 6000);
        }
        return 0;
    }
}
