package headwayent.hotshotengine;

/**
 * Created by sebas on 17.11.2015.
 */
public interface ENG_GameDescriptionEventsListener {

    void onGameStart();

    void onGameActivation(boolean activated);

    void onGameEnd();
}
