import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;

@ScriptManifest(name = "CanifisRoofTops", description = "Third Script", author = "lsjc12911",
        version = 1.0, category = Category.AGILITY, image = "")

public class CanifisRoofTops extends AbstractScript {

    //camera
    final int PITCH = 383;
    final int YAW = 1343;

    State state;
    GroundItem item = null;

    //midWay
    Area midArea = new Area(3491, 3490, 3497, 3486);
    Tile midTile = new Tile(3495, 3489);

    GameObject tree;
    Tile treeTile = new Tile(3505, 3489);
    Tile treeEnd = new Tile(3506, 3492, 2);

    GameObject gap1;
    Tile gap1Tile = new Tile(3505, 3498, 2);
    Tile gap1End = new Tile(3502, 3504, 2);
    Area firstRoof = new Area(3504, 3498, 3511, 3492, 2);
    Area firstFall = new Area(3501, 3501, 3505, 3498);

    GameObject gap2;
    Tile gap2Tile = new Tile(3496, 3504, 2);
    Tile gap2End = new Tile(3492, 3504, 2);
    Area secondRoof = new Area(3496, 3507, 3504, 3503, 2);
    Area secondFall = new Area(3495, 3501, 3495, 3498);

    GameObject gap3;
    Tile gap3Tile = new Tile(3485, 3499, 2);
    Tile gap3End = new Tile(3479, 3499, 3);
    Area thirdRoof = new Area(3493, 3505, 3485, 3498, 2);
    Area thirdFall = new Area(3481, 3502, 3483, 3498);

    GameObject gap4;
    Tile gap4Tile = new Tile(3478, 3491, 3);
    Tile gap4End = new Tile(3478, 3486, 2);
    Area fourthRoof = new Area(3480, 3500, 3474, 3491, 3);
    Area fourthFall = new Area(3476, 3490, 3481, 3489);

    GameObject pole;
    Tile poleTile = new Tile(3480, 3483, 2);
    Tile poleEnd = new Tile(3489, 3476, 3);
    Area fifthRoof = new Area(3477, 3487, 3484, 3481, 2);
    Area fifthFall = new Area(3490, 3484, 3484, 3478);

    GameObject gap5;
    Tile gap5Tile = new Tile(3503, 3476, 3);
    Tile gap5End = new Tile(3510, 3476, 2);
    Area sixthRoof = new Area(3486, 3477, 3504, 3471, 3);
    Area lastFall = new Area(3504, 3484, 3507, 3474);

    GameObject gap6;
    Tile gap6Tile = new Tile(3510, 3483, 2);
    Tile gap6End = new Tile(3510, 3485);
    Area seventhRoof = new Area(3509, 3483, 3516, 3474, 2);

    private enum State{
        TREE, GAP1, GAP2, GAP3, GAP4, POLE, GAP5, GAP6, MID_FALL, ITEM
    }

    private State getState(){
        item = GroundItems.closest("Mark of grace");

        if(getLocalPlayer().getTile().equals(gap6End)){
            state = State.TREE;
        } else if(firstRoof.contains(getLocalPlayer())){
            if(!firstRoof.contains(item)){
                state = State.GAP1;
            }else if(firstRoof.contains(item)){
                state = State.ITEM;
            }
        } else if(secondRoof.contains(getLocalPlayer())){
            if(!secondRoof.contains(item)){
                state = State.GAP2;
            }else if(secondRoof.contains(item)){
                state = State.ITEM;
            }
        } else if(thirdRoof.contains(getLocalPlayer())){
            if(!thirdRoof.contains(item)){
                state = State.GAP3;
            }else if(thirdRoof.contains(item)){
                state = State.ITEM;
            }
        } else if(fourthRoof.contains(getLocalPlayer())){
            if(!fourthRoof.contains(item)){
                state = State.GAP4;
            }else if(fourthRoof.contains(item)){
                state = State.ITEM;
            }
        } else if(fifthRoof.contains(getLocalPlayer())){
            if(!fifthRoof.contains(item)){
                state = State.POLE;
            }else if(fifthRoof.contains(item)){
                state = State.ITEM;
            }
        } else if(sixthRoof.contains(getLocalPlayer())){
            if(!sixthRoof.contains(item)){
                state = State.GAP5;
            }else if(sixthRoof.contains(item)){
                state = State.ITEM;
            }
        } else if(seventhRoof.contains(getLocalPlayer())){
            if(!seventhRoof.contains(item)){
                state = State.GAP6;
            }else if(seventhRoof.contains(item)){
                state = State.ITEM;
            }
        } else if(midArea.contains(getLocalPlayer())){
            state = State.TREE;
        } else if(thirdFall.contains(getLocalPlayer()) || fourthFall.contains(getLocalPlayer()) || fifthFall.contains(getLocalPlayer())){
            state = State.MID_FALL;
        } else if(firstFall.contains(getLocalPlayer()) || secondFall.contains(getLocalPlayer()) || lastFall.contains(getLocalPlayer())){
            state = State.TREE;
        }
        return state;
    }

    @Override
    public int onLoop() {
        //customized camera angle for canifis roof tops
        if(Camera.getYaw() != YAW && Camera.getPitch() != PITCH){
            log("Initiating camera turn");
            Camera.keyboardRotateTo(YAW, PITCH);
            sleepUntil(() -> Camera.getYaw() == YAW && Camera.getPitch() == PITCH, 1000);
        }

        if(getState().equals(State.ITEM)){
            log("Found Mark");
            item.interact("Take");
            sleep(1000,2000);
        } else if(getState().equals(State.MID_FALL)){
            log("Going to mid mark");
            Walking.walk(midTile);
            sleepUntil(() -> getLocalPlayer().getTile().equals(midTile), 3500);
        }  else if(getState().equals(State.TREE)){
            tree = GameObjects.closest(c -> c != null && c.getName().contentEquals("Tall tree") && c.getTile().equals(treeTile));
            tree.interact("Climb");
            Camera.keyboardRotateTo(YAW, PITCH);
            log("Climbing tree");
            sleepUntil(() -> getLocalPlayer().getTile().equals(treeEnd), 5000);

        } else if(getState().equals(State.GAP1)){
            gap1 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Gap") && c.getTile().equals(gap1Tile));
            gap1.interact("Jump");
            log("Jump Gap1");
            sleepUntil(() -> getLocalPlayer().getTile().equals(gap1End), 5000);

        } else if(getState().equals(State.GAP2)){
            gap2 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Gap") && c.getTile().equals(gap2Tile));
            gap2.interact("Jump");
            log("Jump Gap2");
            sleepUntil(() -> getLocalPlayer().getTile().equals(gap2End), 5000);

        } else if(getState().equals(State.GAP3)){
            gap3 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Gap") && c.getTile().equals(gap3Tile));
            gap3.interact("Jump");
            log("Jump Gap3");
            sleepUntil(() -> getLocalPlayer().getTile().equals(gap3End), 5000);

        } else if(getState().equals(State.GAP4)){
            gap4 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Gap") && c.getTile().equals(gap4Tile));
            gap4.interact("Jump");
            log("Jump Gap4");
            sleepUntil(() -> getLocalPlayer().getTile().equals(gap4End), 5000);

        } else if(getState().equals(State.POLE)){
            pole = GameObjects.closest(c -> c != null && c.getName().contentEquals("Pole-vault") && c.getTile().equals(poleTile));
            pole.interact("Vault");
            log("Vault pole");
            sleepUntil(() -> getLocalPlayer().getTile().equals(poleEnd), 6000);

        } else if(getState().equals(State.GAP5)){
            gap5 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Gap") && c.getTile().equals(gap5Tile));
            gap5.interact("Jump");
            log("Jump Gap5");
            sleepUntil(() -> getLocalPlayer().getTile().equals(gap5End), 5000);

        } else if(getState().equals(State.GAP6)){
            gap6 = GameObjects.closest(c -> c != null && c.getName().contentEquals("Gap") && c.getTile().equals(gap6Tile));
            gap6.interact("Jump");
            log("Jump Gap6");
            sleepUntil(() -> getLocalPlayer().getTile().equals(gap6End), 5000);

        }
        return 500;
    }
}
