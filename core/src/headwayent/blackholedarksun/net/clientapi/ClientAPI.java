/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.net.clientapi.tables.*;
import headwayent.hotshotengine.networking.ENG_NetUtility;
import retrofit.RestAdapter;

import java.util.List;

/**
 * Created by Sebastian on 09.04.2015.
 */
public class ClientAPI {

    private final IClientAPIResponseProvider responseProvider;
    /** @noinspection UnstableApiUsage*/
    private final EventBus bus;

    /** @noinspection UnstableApiUsage*/
    public ClientAPI() {
//        clientAPI = this;
        bus = MainApp.getGame().getEventBus();
        bus.register(this);
        //                .setConverter(new GsonConverter(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ssZ").create()))
        //    private static ClientAPI clientAPI;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(new ServerEndPoint())
//                .setConverter(new GsonConverter(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ssZ").create()))
                .build();
        responseProvider = restAdapter.create(IClientAPIResponseProvider.class);
//        List<Session> sessionList = responseProvider.getSessionList();
    }

    public static class LoginEvent extends BaseBusEvent {
        public final String username;
        public final String password;

        public LoginEvent(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class LoggedInEvent extends BaseBusEvent {
        public final User user;

        public LoggedInEvent(User user) {
            this.user = user;
        }
    }

    public static class LoginErrorEvent extends BaseBusEvent {
        public final String error;

        public LoginErrorEvent(String error) {
            this.error = error;
        }
    }

    public static class CreateUserEvent extends BaseBusEvent {
        public final String username;
        public final String password;

        public CreateUserEvent(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class UserCreatedEvent extends BaseBusEvent {
        public final User user;

        public UserCreatedEvent(User user) {
            this.user = user;
        }
    }

    public static class UserCreationErrorEvent extends BaseBusEvent {
        public final String error;

        public UserCreationErrorEvent(String error) {
            this.error = error;
        }
    }

    public static class GetSessionListEvent extends BaseBusEvent {

        private final int gameType;

        public GetSessionListEvent(int gameType) {
            this.gameType = gameType;
        }
    }

    public static class SessionListLoadedEvent extends BaseBusEvent {
        public final List<Session> sessionList;

        public SessionListLoadedEvent(List<Session> sessionList) {
            this.sessionList = sessionList;
        }
    }

    public static class SessionListLoadErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public SessionListLoadErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class GetMapListEvent extends BaseBusEvent {

    }

    public static class MapListLoadedEvent extends BaseBusEvent {
        public final List<Map> mapList;

        public MapListLoadedEvent(List<Map> mapList) {
            this.mapList = mapList;
        }
    }

    public static class MapListLoadErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public MapListLoadErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class CreateSessionEvent extends BaseBusEvent {
        public final String userToken;
        public final Session session;

        public CreateSessionEvent(String userToken, Session session) {
            this.userToken = userToken;
            this.session = session;
        }
    }

    public static class SessionCreatedEvent extends BaseBusEvent {
        public final Server session;

        public SessionCreatedEvent(Server server) {
            this.session = server;
        }
    }

    public static class SessionCreationErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public SessionCreationErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class JoinSessionEvent extends BaseBusEvent {
        public final String userToken;
        public final Session session;

        public JoinSessionEvent(String userToken, Session session) {
            this.userToken = userToken;
            this.session = session;
        }
    }

    public static class SessionJoinedEvent extends BaseBusEvent {
        public final Server session;

        public SessionJoinedEvent(Server server) {
            this.session = server;
        }
    }

    public static class SessionJoinErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public SessionJoinErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class LeaveSessionEvent extends BaseBusEvent {

        public LeaveSessionEvent() {
        }
    }

    public static class SessionLeftEvent extends BaseBusEvent {
//        public Server session;
        public final User user;

        public SessionLeftEvent(/*Server server*/User user) {
//            this.session = server;
            this.user = user;
        }
    }

    public static class SessionLeftErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public SessionLeftErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class UpdateUserAccountEvent extends BaseBusEvent {
        public final User user;

        public UpdateUserAccountEvent(User user) {
            this.user = user;
        }
    }

    public static class UserAccountUpdatedEvent extends BaseBusEvent {
        public final User user;

        public UserAccountUpdatedEvent(User user) {
            this.user = user;
        }
    }

    public static class UserAccountUpdateErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public UserAccountUpdateErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class AddFriendEvent extends BaseBusEvent {
        public final UserFriend userFriend;
        public final String authToken;

        public AddFriendEvent(String authToken, UserFriend userFriend) {
            this.authToken = authToken;
            this.userFriend = userFriend;
        }
    }

    public static class FriendAddedEvent extends BaseBusEvent {
        public final UserFriend userFriend;

        public FriendAddedEvent(UserFriend userFriend) {
            this.userFriend = userFriend;
        }
    }

    public static class AddFriendErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public AddFriendErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class GetFriendListEvent extends BaseBusEvent {
        public final String userToken;

        public GetFriendListEvent(String userToken) {
            this.userToken = userToken;
        }
    }

    public static class FriendListReceivedEvent extends BaseBusEvent {

        public final UserFriendList friendList;

        public FriendListReceivedEvent(UserFriendList friendList) {
            this.friendList = friendList;
        }
    }

    public static class GetFriendsListErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public GetFriendsListErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class GetFriendInvitationListEvent extends BaseBusEvent {
        private final String userToken;

        public GetFriendInvitationListEvent(String userToken) {
            this.userToken = userToken;
        }
    }

    public static class FriendInvitationListReceivedEvent extends BaseBusEvent {
        public final UserFriendInvitationList list;

        public FriendInvitationListReceivedEvent(UserFriendInvitationList list) {
            this.list = list;
        }
    }

    public static class GetFriendInvitationListErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public GetFriendInvitationListErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class AcceptFriendInvitationEvent extends BaseBusEvent {
        private final UserFriendInvitation userFriendInvitation;

        public AcceptFriendInvitationEvent(UserFriendInvitation userFriendInvitation) {
            this.userFriendInvitation = userFriendInvitation;
        }
    }

    public static class AcceptFriendInvitationSuccessEvent extends BaseBusEvent {
        public final UserFriendInvitation userFriendInvitation;

        public AcceptFriendInvitationSuccessEvent(UserFriendInvitation userFriendInvitation) {
            this.userFriendInvitation = userFriendInvitation;
        }
    }

    public static class AcceptFriendInvitationErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public AcceptFriendInvitationErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class GetFriendIdEvent extends BaseBusEvent {
//        public final String name;
        public final UserFriend userFriend;

        public GetFriendIdEvent(/*String name, */UserFriend userFriend) {
//            this.name = name;
            this.userFriend = userFriend;
        }
    }

    public static class FriendIdReceivedEvent extends BaseBusEvent {
        public final UserFriend userFriend;

        public FriendIdReceivedEvent(UserFriend userFriend) {
            this.userFriend = userFriend;
        }
    }

    public static class GetFriendIdErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public GetFriendIdErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class CreateLobbyEvent extends BaseBusEvent {
        public final String userToken;
        public final List<FriendData> friendDataList;

        public CreateLobbyEvent(String userToken, List<FriendData> friendDataList) {
            this.userToken = userToken;
            this.friendDataList = friendDataList;
        }
    }

    public static class LobbyCreatedEvent extends BaseBusEvent {
        public final Lobby lobby;

        public LobbyCreatedEvent(Lobby lobby) {
            this.lobby = lobby;
        }
    }

    public static class CreateLobbyErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public CreateLobbyErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }
    
    public static class GetLobbyInvitationsEvent extends BaseBusEvent {
        public final String userToken;

        public GetLobbyInvitationsEvent(String userToken) {
            this.userToken = userToken;
        }
    }

    public static class LobbyInvitationsReceivedEvent extends BaseBusEvent {
        public final LobbyList lobbyList;

        public LobbyInvitationsReceivedEvent(LobbyList lobbyList) {
            this.lobbyList = lobbyList;
        }
    }

    public static class GetLobbyInvitationsErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public GetLobbyInvitationsErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class JoinLobbyEvent extends BaseBusEvent {
        public final String userToken;
        public final Lobby lobby;

        public JoinLobbyEvent(String userToken, Lobby lobby) {
            this.userToken = userToken;
            this.lobby = lobby;
        }
    }

    public static class LobbyJoinedEvent extends BaseBusEvent {
        public final Lobby lobby;

        public LobbyJoinedEvent(Lobby lobby) {
            this.lobby = lobby;
        }
    }

    public static class JoinLobbyErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public JoinLobbyErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class GetLobbyStatusEvent extends BaseBusEvent {
        public final String userToken;
        public final Lobby lobby;

        public GetLobbyStatusEvent(String userToken, Lobby lobby) {
            this.userToken = userToken;
            this.lobby = lobby;
        }
    }

    public static class LobbyStatusReceivedEvent extends BaseBusEvent {
        public final LobbyInvitationList lobbyInvitationList;

        public LobbyStatusReceivedEvent(LobbyInvitationList lobbyInvitationList) {
            this.lobbyInvitationList = lobbyInvitationList;
        }
    }

    public static class GetLobbyStatusErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public GetLobbyStatusErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    public static class ExitLobbyEvent extends BaseBusEvent {
        public final String userToken;
        public final Lobby lobby;

        public ExitLobbyEvent(String userToken, Lobby lobby) {
            this.userToken = userToken;
            this.lobby = lobby;
        }
    }

    public static class LobbyExitedEvent extends BaseBusEvent {
        public final Lobby lobby;

        public LobbyExitedEvent(Lobby lobby) {
            this.lobby = lobby;
        }
    }

    public static class ExitLobbyErrorEvent extends BaseBusEvent {
        public final RestError restError;

        public ExitLobbyErrorEvent(RestError restError) {
            this.restError = restError;
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void exitLobby(ExitLobbyEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new ExitLobbyTask(event.userToken, event.lobby, new BaseApiAsyncTask.OnTaskFinishedListener<Lobby>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(Lobby lobby) {
                    bus.post(new LobbyExitedEvent(lobby));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new ExitLobbyErrorEvent(error));
                }
            }).execute();
        }
    }

    private class ExitLobbyTask extends BaseApiAsyncTask<Lobby> {

        private final String userToken;
        private final Lobby lobby;

        public ExitLobbyTask(String userToken, Lobby lobby, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
            this.lobby = lobby;
        }

        @Override
        protected Lobby doInBackground(Void... voids) {
            return responseProvider.exitLobby(userToken, lobby);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void getLobbyStatus(GetLobbyStatusEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new GetLobbyStatusTask(event.userToken, event.lobby, new BaseApiAsyncTask.OnTaskFinishedListener<LobbyInvitationList>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(LobbyInvitationList lobbyInvitationList) {
                    bus.post(new LobbyStatusReceivedEvent(lobbyInvitationList));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new GetLobbyStatusErrorEvent(error));
                }
            }).execute();
        }
    }

    private class GetLobbyStatusTask extends BaseApiAsyncTask<LobbyInvitationList> {

        private final String userToken;
        private final Lobby lobby;

        public GetLobbyStatusTask(String userToken, Lobby lobby, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
            this.lobby = lobby;
        }

        @Override
        protected LobbyInvitationList doInBackground(Void... voids) {
            return responseProvider.getLobbyStatus(userToken, lobby);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void joinLobby(JoinLobbyEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new JoinLobbyTask(event.userToken, event.lobby, new BaseApiAsyncTask.OnTaskFinishedListener<Lobby>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(Lobby lobby) {
                    bus.post(new LobbyJoinedEvent(lobby));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new JoinLobbyErrorEvent(error));
                }
            }).execute();
        }
    }

    private class JoinLobbyTask extends BaseApiAsyncTask<Lobby> {

        private final Lobby lobby;
        private final String userToken;

        public JoinLobbyTask(String userToken, Lobby lobby, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
            this.lobby = lobby;
        }

        @Override
        protected Lobby doInBackground(Void... voids) {
            return responseProvider.joinLobby(userToken, lobby);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void getLobbyInvitations(GetLobbyInvitationsEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new GetLobbyInvitationsTask(event.userToken, new BaseApiAsyncTask.OnTaskFinishedListener<LobbyList>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(LobbyList lobbyList) {
                    bus.post(new LobbyInvitationsReceivedEvent(lobbyList));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new GetLobbyInvitationsErrorEvent(error));
                }
            }).execute();
        }
    }

    private class GetLobbyInvitationsTask extends BaseApiAsyncTask<LobbyList> {

        private final String userToken;

        public GetLobbyInvitationsTask(String userToken, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
        }

        @Override
        protected LobbyList doInBackground(Void... voids) {
            return responseProvider.getLobbyInvitations(userToken);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void createLobby(CreateLobbyEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new CreateLobbyTask(event.userToken, event.friendDataList, new BaseApiAsyncTask.OnTaskFinishedListener<Lobby>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(Lobby lobby) {
                    bus.post(new LobbyCreatedEvent(lobby));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new CreateLobbyErrorEvent(error));
                }
            }).execute();
        }
    }

    private class CreateLobbyTask extends BaseApiAsyncTask<Lobby> {

        private final String userToken;
        private final List<FriendData> friendDataList;

        public CreateLobbyTask(String userToken, List<FriendData> friendDataList, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
            this.friendDataList = friendDataList;
        }

        @Override
        protected Lobby doInBackground(Void... voids) {
            return responseProvider.createLobby(userToken, friendDataList);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void getFriendList(GetFriendListEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new GetFriendListTask(event.userToken, new BaseApiAsyncTask.OnTaskFinishedListener<UserFriendList>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(UserFriendList friendList) {
                    bus.post(new FriendListReceivedEvent(friendList));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new GetFriendsListErrorEvent(error));
                }
            }).execute();
        }
    }

    private class GetFriendListTask extends BaseApiAsyncTask<UserFriendList> {

        private final String userToken;

        public GetFriendListTask(String userToken, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
        }

        @Override
        protected UserFriendList doInBackground(Void... voids) {
            return responseProvider.getFriendsList(userToken);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void getFriendInvitationList(GetFriendInvitationListEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new GetFriendInvitationListTask(event.userToken, new BaseApiAsyncTask.OnTaskFinishedListener<UserFriendInvitationList>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(UserFriendInvitationList userFriendInvitations) {
                    bus.post(new FriendInvitationListReceivedEvent(userFriendInvitations));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new GetFriendInvitationListErrorEvent(error));
                }
            }).execute();
        }
    }

    private class GetFriendInvitationListTask extends BaseApiAsyncTask<UserFriendInvitationList> {

        private final String userToken;

        public GetFriendInvitationListTask(String userToken, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
        }

        @Override
        protected UserFriendInvitationList doInBackground(Void... voids) {
            return responseProvider.getFriendInvitations(userToken);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void acceptFriendInvitation(AcceptFriendInvitationEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new AcceptFriendInvitationTask(event.userFriendInvitation, new BaseApiAsyncTask.OnTaskFinishedListener<UserFriendInvitation>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(UserFriendInvitation userFriendInvitation) {
                    bus.post(new AcceptFriendInvitationSuccessEvent(userFriendInvitation));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new AcceptFriendInvitationErrorEvent(error));
                }
            }).execute();
        }
    }

    private class AcceptFriendInvitationTask extends BaseApiAsyncTask<UserFriendInvitation> {

        private final UserFriendInvitation userFriendInvitation;

        public AcceptFriendInvitationTask(UserFriendInvitation userFriendInvitation, OnTaskFinishedListener callback) {
            super(callback);
            this.userFriendInvitation = userFriendInvitation;
        }

        @Override
        protected UserFriendInvitation doInBackground(Void... voids) {
            return responseProvider.acceptFriendInvitation(userFriendInvitation);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void getFriendId(GetFriendIdEvent userFriend) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new GetFriendIdTask(userFriend.userFriend.getUserId(), userFriend.userFriend.getFriendName(), new BaseApiAsyncTask.OnTaskFinishedListener<UserFriend>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(UserFriend userFriend) {
                    bus.post(new FriendIdReceivedEvent(userFriend));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new GetFriendIdErrorEvent(error));
                }
            }).execute();
        }
    }

    private class GetFriendIdTask extends BaseApiAsyncTask<UserFriend> {

        private final String friendName;
        private final long userId;

        public GetFriendIdTask(long userId, String friendName, OnTaskFinishedListener callback) {
            super(callback);
            this.userId = userId;
            this.friendName = friendName;
        }

        @Override
        protected UserFriend doInBackground(Void... voids) {
            UserFriend userFriend = new UserFriend();
            userFriend.setUserId(userId);
            userFriend.setFriendName(friendName);
            UserFriend userResponse = responseProvider.getFriendId(userFriend);
            if (userResponse.getError() != null) {
                setError(new RestError(userResponse.getError()));
            }
            return userResponse;
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void addFriend(AddFriendEvent userFriend) {
        new AddFriendTask(userFriend.authToken, userFriend.userFriend.getUserId(), userFriend.userFriend.getFriendId(), new BaseApiAsyncTask.OnTaskFinishedListener<UserFriend>() {
            /** @noinspection UnstableApiUsage*/
            @Override
            public void onTaskSuccess(UserFriend userFriend) {
                bus.post(new FriendAddedEvent(userFriend));
            }

            /** @noinspection UnstableApiUsage*/
            @Override
            public void onTaskError(RestError error) {
                bus.post(new AddFriendErrorEvent(error));
            }
        }).execute();
    }

    private class AddFriendTask extends BaseApiAsyncTask<UserFriend> {

        private final String authToken;
        private final long userId;
        private final long friendId;

        public AddFriendTask(String authToken, long userId, long friendId, OnTaskFinishedListener callback) {
            super(callback);
            this.authToken = authToken;
            this.userId = userId;
            this.friendId = friendId;
        }

        @Override
        protected UserFriend doInBackground(Void... voids) {
            UserFriend userFriend = new UserFriend();
            userFriend.setUserId(userId);
            userFriend.setFriendId(friendId);
            UserFriend userResponse = responseProvider.addFriend(authToken, userFriend);
            if (userResponse.getError() != null) {
                setError(new RestError(userResponse.getError()));
            }
            return userResponse;
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void login(LoginEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new LoginTask(event.username, event.password, new BaseApiAsyncTask.OnTaskFinishedListener<User>() {

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(User user) {
                    bus.post(new LoggedInEvent(user));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new LoginErrorEvent(error.getMessage()));
                }
            }).execute();
        }
    }

    private class LoginTask extends BaseApiAsyncTask<User> {

        private final String username;
        private final String password;

        public LoginTask(String username, String password, OnTaskFinishedListener callback) {
            super(callback);
            this.username = username;
            this.password = password;
        }

        @Override
        protected User doInBackground(Void... params) {
            User userResponse = loginUser(username, password);
            if (userResponse.getError() != null) {
                setError(new RestError(userResponse.getError()));
            }
            return userResponse;
        }

    }

    private User loginUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return responseProvider.login(user);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void createUser(CreateUserEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new CreateUserTask(event.username, event.password, new BaseApiAsyncTask.OnTaskFinishedListener<User>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(User user) {
                    bus.post(new UserCreatedEvent(user));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new UserCreationErrorEvent(error.getMessage()));
                }
            }).execute();
        }
    }

    private class CreateUserTask extends BaseApiAsyncTask<User> {

        private final String username;
        private final String password;

        public CreateUserTask(String username, String password, OnTaskFinishedListener callback) {
            super(callback);
            this.username = username;
            this.password = password;
        }

        @Override
        protected User doInBackground(Void... params) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            User userResponse = responseProvider.createUser(user);
            if (userResponse.getError() != null) {
                setError(new RestError(userResponse.getError()));
            }
            return userResponse;
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void getMapList(GetMapListEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new GetMapListTask(new BaseApiAsyncTask.OnTaskFinishedListener<List<Map>>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(List<Map> result) {
                    bus.post(new MapListLoadedEvent(result));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new MapListLoadErrorEvent(error));
                }
            }).execute();
        }
    }

    private class GetMapListTask extends BaseApiAsyncTask<List<Map>> {

        public GetMapListTask(OnTaskFinishedListener callback) {
            super(callback);
        }

        @Override
        protected List<Map> doInBackground(Void... params) {
            return responseProvider.getMapList();
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void getSessionList(GetSessionListEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new GetSessionListTask(event.gameType, new BaseApiAsyncTask.OnTaskFinishedListener<List<Session>>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(List<Session> result) {
//                    System.out.println("Task success");
                    bus.post(new SessionListLoadedEvent(result));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
//                    System.out.println("Task failed");
                    bus.post(new SessionListLoadErrorEvent(error));
                }
            }).execute();
        }
    }

    private class GetSessionListTask extends BaseApiAsyncTask<List<Session>> {

        private final int gameType;

        public GetSessionListTask(int gameType, OnTaskFinishedListener callback) {
            super(callback);
            this.gameType = gameType;
        }

        @Override
        protected List<Session> doInBackground(Void... params) {
            return responseProvider.getSessionList(gameType);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void createSession(CreateSessionEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new CreateSessionTask(event.userToken, event.session, new BaseApiAsyncTask.OnTaskFinishedListener<Server>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(Server server) {
                    bus.post(new SessionCreatedEvent(server));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
//                    System.out.println("Task failed");
                    bus.post(new SessionCreationErrorEvent(error));
                }
            }).execute();
        }
    }

    private class CreateSessionTask extends BaseApiAsyncTask<Server> {

        //        private String userToken;
        private final Session session;

        public CreateSessionTask(String userToken, Session session, OnTaskFinishedListener callback) {
            super(callback);
//            this.userToken = userToken;
            this.session = session;
        }

        @Override
        protected Server doInBackground(Void... params) {
            return (Server) executeWithReauth(() -> responseProvider.createSession(MainApp.getGame().getUser().getAuthToken(), CreateSessionTask.this.session));
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void joinSession(JoinSessionEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new JoinSessionTask(event.userToken, event.session, new BaseApiAsyncTask.OnTaskFinishedListener<Server>() {
                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(Server server) {
                    bus.post(new SessionJoinedEvent(server));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
//                    System.out.println("Task failed");
                    bus.post(new SessionJoinErrorEvent(error));
                }
            }).execute();
        }
    }

    private class JoinSessionTask extends BaseApiAsyncTask<Server> {

        //        private String userToken;
        private final Session session;

        public JoinSessionTask(String userToken, Session session, OnTaskFinishedListener callback) {
            super(callback);
//            this.userToken = userToken;
            this.session = session;
        }

        @Override
        protected Server doInBackground(Void... params) {
            return (Server) executeWithReauth(() -> responseProvider.joinSession(MainApp.getGame().getUser().getAuthToken(), JoinSessionTask.this.session));
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void leaveSession(LeaveSessionEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new LeaveSessionTask(new BaseApiAsyncTask.OnTaskFinishedListener<User>() {

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(User user) {
                    bus.post(new SessionLeftEvent(user));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    bus.post(new SessionLeftErrorEvent(error));
                }
            }).execute();
        }
    }

    private class LeaveSessionTask extends BaseApiAsyncTask<User> {

        public LeaveSessionTask(OnTaskFinishedListener callback) {
            super(callback);
        }

        @Override
        protected User doInBackground(Void... params) {
            //            Object o = responseProvider.leaveSession(MainApp.getGame().getUser().getAuthToken());
            return (User) executeWithReauth(() -> responseProvider.leaveSession(MainApp.getGame().getUser().getAuthToken()));
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void updateUserAccount(UpdateUserAccountEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new UpdateUserAccountTask(event.user, new BaseApiAsyncTask.OnTaskFinishedListener<User>() {

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskSuccess(User user) {
                    bus.post(new UserAccountUpdatedEvent(user));
                }

                /** @noinspection UnstableApiUsage*/
                @Override
                public void onTaskError(RestError error) {
                    System.out.println("resterror: " + error.getCode() + " message: " + error.getMessage() + " http code: " + error.getHttpCode());
                    bus.post(new UserAccountUpdateErrorEvent(error));
                }
            }).execute();
        }
    }

    private class UpdateUserAccountTask extends BaseApiAsyncTask<User> {

        private final User user;

        public UpdateUserAccountTask(User user, OnTaskFinishedListener callback) {
            super(callback);
            this.user = user;
        }

        @Override
        protected User doInBackground(Void... params) {
            return responseProvider.updateAccount(user);
        }
    }

    private GenericTransient executeWithReauth(RequestExecutor requestExecutor) {
        boolean noError = false;
        boolean fatalError = false;
        GenericTransient genericTransient;
        do {
            genericTransient = requestExecutor.executeWithReauth();
            switch (genericTransient.getErrorCode()) {
                case ErrorCodes.NO_ERROR:
                    noError = true;
                    break;
                case ErrorCodes.TOKEN_INVALID:
                case ErrorCodes.TOKEN_EXPIRED: {
                    User user = MainApp.getGame().getUser();
                    User loginUser = loginUser(user.getUsername(), user.getPassword());
                    switch (loginUser.getErrorCode()) {
                        case ErrorCodes.NO_ERROR:
                            MainApp.getGame().setUser(loginUser);
                            break;
                        default:
                            fatalError = true;
                    }

                }
                break;
                case ErrorCodes.USER_ALREADY_IN_SESSION: {
                    User leaveSession = responseProvider.leaveSession(MainApp.getGame().getUser().getAuthToken());
                    switch (leaveSession.getErrorCode()) {
                        case ErrorCodes.NO_ERROR:
                            MainApp.getGame().setUser(leaveSession);
                            break;
                        default:
                            fatalError = true;
                    }
                }
//                    fatalError = true;
                break;
                case ErrorCodes.SERVER_UNAVAILABLE:
                    fatalError = true;
                    break;
                case ErrorCodes.USER_NOT_IN_SESSION:
                    break;
                default:
                    throw new IllegalStateException("Invalid error code: " + genericTransient.getErrorCode() + " " + genericTransient.getError());
            }
        } while (!noError && !fatalError);
        return genericTransient;
    }

    private interface RequestExecutor {
        GenericTransient executeWithReauth();
    }

    public static ClientAPI getSingleton() {
//        if (clientAPI == null) {
//            throw new NullPointerException("clientAPI not initialized");
//        }
//        return clientAPI;
        return MainApp.getGame().getClientAPI();
    }
}
