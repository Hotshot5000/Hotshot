package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

public class LevelEnd {

    public final ComparatorNode endEvents = new ComparatorNode();
    public final ArrayList<String> endEventList = new ArrayList<>();
    public final ArrayList<String> endEventIgnoreLossList = new ArrayList<>();

    public ComparatorNode getEndEvents() {
        return endEvents;
    }

    public ArrayList<String> getEndEventList() {
        return endEventList;
    }

    public ArrayList<String> getEndEventIgnoreLossList() {
        return endEventIgnoreLossList;
    }
}
