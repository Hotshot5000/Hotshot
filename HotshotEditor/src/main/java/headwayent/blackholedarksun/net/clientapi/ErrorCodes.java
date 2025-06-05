package headwayent.blackholedarksun.net.clientapi;

/**
 * Created by sebas on 17.10.2015.
 */
public class ErrorCodes {

    public static final int MINIMUM_USERNAME_LENGTH = 3;
    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    public static final int NO_ERROR = 0;

    public static final int LOGIN_INVALID = 1;
    public static final String LOGIN_INVALID_STR = "Invalid login";

    public static final int ACCOUNT_ALREADY_EXISTS = 2;
    public static final String ACCOUNT_ALREADY_EXISTS_STR = "Account already exists";

    public static final int SESSION_NAME_INVALID = 3;

    public static final int SESSION_NAME_MULTIPLE = 4;

    public static final int USER_ALREADY_IN_SESSION = 5;
    public static final String USER_ALREADY_IN_SESSION_STR = "User already in session";

    public static final int SERVER_UNAVAILABLE = 6;
    public static final String SERVER_UNAVAILABLE_STR = "No server available";

    public static final int TOKEN_INVALID = 7;
    public static final String TOKEN_INVALID_STR = "Invalid token";

    public static final int TOKEN_EXPIRED = 8;
    public static final String TOKEN_EXPIRED_STR = "Token expired";

    public static final int MAP_ALREADY_EXISTS = 9;
    public static final String MAP_ALREADY_EXISTS_STR = "Map name already exists";

    public static final int SERVER_DOES_NOT_EXIST = 10;
    public static final String SERVER_DOES_NOT_EXIST_STR = "Server does not exist";

    public static final int SERVER_INVALID = 11;
    public static final String SERVER_INVALID_STR = "Server invalid";

    public static final int SESSION_INVALID_ID = 12;
    public static final String SESSION_INVALID_ID_STR = "Invalid id";

    public static final int USER_NOT_IN_SESSION = 13;
    public static final String USER_NOT_IN_SESSION_STR = "User not in session";

    public static final int ACCOUNT_DOES_NOT_EXIST = 14;
    public static final String ACCOUNT_DOES_NOT_EXIST_STR = "Account does not exist";

    public static final int SERVER_GAME_TYPE_INCOMPATIBLE = 15;
    public static final String SERVER_GAME_TYPE_INCOMPATIBLE_STR = "Incompatible platforms connection attempt! You must be on the same platform type as the server.";

    public static final int USER_FRIEND_ALREADY_EXISTS = 16;
    public static final String USER_FRIEND_ALREADY_EXISTS_STR = "Friend already added";

    public static final int USER_FRIEND_NOT_FOUND = 17;
    public static final String USER_FRIEND_NOT_FOUND_STR = "Friend with that name cannot be found";

    public static final int USER_FRIEND_TOO_MANY = 18;
    public static final String USER_FRIEND_TOO_MANY_STR = "Too many people found beginning with that name";

    public static final int USER_FRIEND_ADDING_YOURSELF = 19;
    public static final String USER_FRIEND_ADDING_YOURSELF_STR = "You're trying to add yourself to the friend list :)";

    public static final int ACCOUNT_USERNAME_TOO_SHORT = 20;
    public static final String ACCOUNT_USERNAME_TOO_SHORT_STR = "Your username must have at least " + MINIMUM_USERNAME_LENGTH + " characters";

    public static final int ACCOUNT_PASSWORD_TOO_SHORT = 21;
    public static final String ACCOUNT_PASSWORD_TOO_SHORT_STR = "Your password must have at least " + MINIMUM_PASSWORD_LENGTH + " characters";

    public static final int GET_FRIENDS_LIST_CANNOT_GET_FRIEND_STATUS = 22;
    public static final String GET_FRIENDS_LIST_CANNOT_GET_FRIEND_STATUS_STR = "Cannot get friend status";

    public static final int ACCEPT_INVITATION_NO_INVITATION_PENDING = 23;
    public static final String ACCEPT_INVITATION_NO_INVITATION_PENDING_STR = "There is no invitation from that user";

    public static final int ACCEPT_INVITATION_USER_NO_LONGER_AVAILABLE = 24;
    public static final String ACCEPT_INVITATION_USER_NO_LONGER_AVAILABLE_STR = "Cannot accept invitation of invalid player";

    public static final int ACCEPT_INVITATION_COULD_NOT_NOTIFY_USER = 25;
    public static final String ACCEPT_INVITATION_COULD_NOT_NOTIFY_USER_STR = "Could not notify user of invitation acceptance";

    public static final int GET_LOBBY_INVITATIONS_CANNOT_FIND_LOBBY = 26;
    public static final String GET_LOBBY_INVITATIONS_CANNOT_FIND_LOBBY_STR = "Could not find the lobby for your invitation";

    public static final int GET_LOBBY_INVITATIONS_MULTIPLE_LOBBIES = 27;
    public static final String GET_LOBBY_INVITATIONS_MULTIPLE_LOBBIES_STR = "Multiple lobbies with the same id";

    public static final int GET_LOBBY_STATUS_LOBBY_NOT_FOUND = 28;
    public static final String GET_LOBBY_STATUS_LOBBY_NOT_FOUND_STR = "Could not update lobby";

    public static final int GET_LOBBY_STATUS_MULTIPLE_LOBBIES = 29;
    public static final String GET_LOBBY_STATUS_MULTIPLE_LOBBIES_STR = "Multiple lobbies with the same id";

    public static final int GET_LOBBY_STATUS_NOBODY_INVITED = 30;
    public static final String GET_LOBBY_STATUS_NOBODY_INVITED_STR = "Nobody invited to the lobby";

    public static final int JOIN_LOBBY_LOBBY_NOT_FOUND = 31;
    public static final String JOIN_LOBBY_LOBBY_NOT_FOUND_STR = "Could not update lobby";

    public static final int JOIN_LOBBY_MULTIPLE_LOBBIES = 32;
    public static final String JOIN_LOBBY_MULTIPLE_LOBBIES_STR = "Multiple lobbies with the same id";

    public static final int JOIN_LOBBY_CANNOT_FIND_INVITATION = 33;
    public static final String JOIN_LOBBY_CANNOT_FIND_INVITATION_STR = "Cannot find invitation";

    public static final int JOIN_LOBBY_CANNOT_UPDATE_LOBBY_INVITATION = 34;
    public static final String JOIN_LOBBY_CANNOT_UPDATE_LOBBY_INVITATION_STR = "Cannot update the lobby invitation";

    public static final int EXIT_LOBBY_LOBBY_NOT_FOUND = 35;
    public static final String EXIT_LOBBY_LOBBY_NOT_FOUND_STR = "Could not update lobby";

    public static final int EXIT_LOBBY_MULTIPLE_LOBBIES = 36;
    public static final String EXIT_LOBBY_MULTIPLE_LOBBIES_STR = "Multiple lobbies with the same id";

    public static final int EXIT_LOBBY_CANNOT_FIND_INVITATION = 37;
    public static final String EXIT_LOBBY_CANNOT_FIND_INVITATION_STR = "Cannot find invitation";

    public static final int EXIT_LOBBY_CANNOT_UPDATE_LOBBY_INVITATION = 38;
    public static final String EXIT_LOBBY_CANNOT_UPDATE_LOBBY_INVITATION_STR = "Cannot update the lobby invitation";

    public static final int EXIT_LOBBY_STATUS_NOBODY_INVITED = 39;
    public static final String EXIT_LOBBY_STATUS_NOBODY_INVITED_STR = "Nobody invited to the lobby";

    public static final int EXIT_LOBBY_CANNOT_UPDATE_LOBBY_LEADER = 40;
    public static final String EXIT_LOBBY_CANNOT_UPDATE_LOBBY_LEADER_STR = "Cannot update lobby leader";

    public static final int GET_LOBBY_INVITATIONS_NO_INVITATIONS = 41;
    public static final String GET_LOBBY_INVITATIONS_NO_INVITATIONS_STR = "No current invitations";
}
