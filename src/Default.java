import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(name = "Default", description = " Script", author = "lsjc12911",
        version = 1.0, category = Category.UTILITY, image = "")

public class Default extends AbstractScript {

    State state;

    private enum State{

    }


    public State getState(){
        return state;
    }

    @Override
    public int onLoop() {
        return 0;
    }
}