package headwayent.blackholedarksun.net.clientapi;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import headwayent.blackholedarksun.net.clientapi.tables.*;
import headwayent.hotshotengine.networking.ENG_NetUtility;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Sebastian on 09.04.2015.
 */
public class ClientAPI {

    private static final String URL = /*APP_Game.APP_SERVER_IP*/ "http://localhost:8080/odyssey-server-1.0-SNAPSHOT/helloworld/";
    //    private static ClientAPI clientAPI;
    private final Retrofit restAdapter;
    private final IClientAPIResponseProvider responseProvider;
    private EventBus bus;

    public ClientAPI(EventBus bus) {
//        clientAPI = this;
        this.bus = bus;
        this.bus.register(this);
        restAdapter = new Retrofit.Builder()
                .baseUrl(URL)
//                .setLogLevel(Retrofit.LogLevel.FULL)
//                .setEndpoint(new ServerEndPoint())
//                .setConverter(new GsonConverter(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ssZ").create()))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        responseProvider = restAdapter.create(IClientAPIResponseProvider.class);
//        List<Session> sessionList = responseProvider.getSessionList();
    }

    public static class UpdatePositionTestEvent extends BaseBusEvent {
        public final String userToken;
        public final double longitude;
        public final double latitude;
        public final boolean transportTaken;

        public UpdatePositionTestEvent(String userToken, double longitude, double latitude, boolean transportTaken) {
            this.userToken = userToken;
            this.longitude = longitude;
            this.latitude = latitude;
            this.transportTaken = transportTaken;
        }
    }

    public static class UpdatePositionEvent extends BaseBusEvent {
        public final String userToken;
        public final double longitude;
        public final double latitude;

        public UpdatePositionEvent(String userToken, double longitude, double latitude) {
            this.userToken = userToken;
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }

    public static class PositionUpdatedEvent extends BaseBusEvent {
        public final TransportStatus transportStatus;

        public PositionUpdatedEvent(TransportStatus transportStatus) {
            this.transportStatus = transportStatus;
        }
    }

    public static class UpdatePositionErrorEvent extends BaseBusEvent {
        public final String error;

        public UpdatePositionErrorEvent(String error) {
            this.error = error;
        }
    }

    public static class ClearSessionDataEvent extends BaseBusEvent {

    }

    public static class SessionDataClearedEvent extends BaseBusEvent {

    }

    public static class ClearSessionDataErrorEvent extends BaseBusEvent {
        public final String error;

        public ClearSessionDataErrorEvent(String error) {
            this.error = error;
        }
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

//    public static class CreateSessionEvent extends BaseBusEvent {
//        public final String userToken;
//        public final Session session;
//
//        public CreateSessionEvent(String userToken, Session session) {
//            this.userToken = userToken;
//            this.session = session;
//        }
//    }
//
//    public static class SessionCreatedEvent extends BaseBusEvent {
//        public final Server session;
//
//        public SessionCreatedEvent(Server server) {
//            this.session = server;
//        }
//    }
//
//    public static class SessionCreationErrorEvent extends BaseBusEvent {
//        public final RestError restError;
//
//        public SessionCreationErrorEvent(RestError restError) {
//            this.restError = restError;
//        }
//    }
//
//    public static class JoinSessionEvent extends BaseBusEvent {
//        public final String userToken;
//        public final Session session;
//
//        public JoinSessionEvent(String userToken, Session session) {
//            this.userToken = userToken;
//            this.session = session;
//        }
//    }
//
//    public static class SessionJoinedEvent extends BaseBusEvent {
//        public final Server session;
//
//        public SessionJoinedEvent(Server server) {
//            this.session = server;
//        }
//    }

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

    @Subscribe
    public void clearSessionData(ClearSessionDataEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new ClearSessionDataTask(new BaseApiAsyncTask.OnTaskFinishedListener<Boolean>() {
                @Override
                public void onTaskSuccess(Boolean aBoolean) {
//                    String s = aBoolean ? "Session data cleared!" : "Session data could not be cleared";
//                    System.out.println(s);
                }

                @Override
                public void onTaskError(RestError error) {
//                    System.out.println("Failed to clear session data: " + error.getMessage());
                }
            }).execute();
        }
    }

    private Call<GenericTransient> genericTransientCall;
    private boolean called;

    private class ClearSessionDataTask extends BaseApiAsyncTask<GenericTransient> {

        public ClearSessionDataTask(OnTaskFinishedListener callback) {
            super(callback);
        }

        @Override
        protected GenericTransient doInBackground(Void... voids) {
            if (called) {
                return null;
            }
            genericTransientCall = responseProvider.clearSessionData();
            GenericTransient genericTransient = extractBody(genericTransientCall);
            called = true;
//            Boolean b = (Boolean) booleanCall;
            return null;
        }
    }

    @Subscribe
    public void updatePositionTest(UpdatePositionTestEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new UpdatePositionTestTask(event.userToken, event.longitude, event.latitude, event.transportTaken, new BaseApiAsyncTask.OnTaskFinishedListener<GenericTransient>() {
                @Override
                public void onTaskSuccess(GenericTransient genericTransient) {
                    int errorCode = genericTransient.getErrorCode();
                }

                @Override
                public void onTaskError(RestError error) {
                    bus.post(new UpdatePositionErrorEvent(error.getMessage()));
                }
            }).execute();
        }
    }

    private class UpdatePositionTestTask extends BaseApiAsyncTask<GenericTransient> {

        private final String userToken;
        private final double longitude;
        private final double latitude;
        private final boolean transportTaken;

        public UpdatePositionTestTask(String userToken, double longitude, double latitude, boolean transportTaken, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
            this.longitude = longitude;
            this.latitude = latitude;
            this.transportTaken = transportTaken;
        }

        @Override
        protected GenericTransient doInBackground(Void... voids) {
            Call<GenericTransient> transportStatusCall =
                    responseProvider.updatePositionTest(userToken, longitude, latitude, transportTaken);
            return extractBody(transportStatusCall);
        }
    }

    @Subscribe
    public void updatePosition(UpdatePositionEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new UpdatePositionTask(event.userToken, event.longitude, event.latitude, new BaseApiAsyncTask.OnTaskFinishedListener<TransportStatus>() {
                @Override
                public void onTaskSuccess(TransportStatus transportStatus) {
                    if (transportStatus.isTransportTaken()) {
                        System.out.println("Transport taken!");
                    }
                    bus.post(new PositionUpdatedEvent(transportStatus));
                }

                @Override
                public void onTaskError(RestError error) {
                    bus.post(new UpdatePositionErrorEvent(error.getMessage()));
                }
            }).execute();
        }
    }

    private class UpdatePositionTask extends BaseApiAsyncTask<TransportStatus> {

        private final String userToken;
        private final double longitude;
        private final double latitude;

        public UpdatePositionTask(String userToken, double longitude, double latitude, OnTaskFinishedListener callback) {
            super(callback);
            this.userToken = userToken;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        @Override
        protected TransportStatus doInBackground(Void... params) {
            Call<TransportStatus> transportStatusCall = responseProvider.updatePosition(userToken, longitude, latitude);
            return extractBody(transportStatusCall);
        }
    }

    @Subscribe
    public void login(LoginEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new LoginTask(event.username, event.password, new BaseApiAsyncTask.OnTaskFinishedListener<User>() {

                @Override
                public void onTaskSuccess(User user) {
                    bus.post(new LoggedInEvent(user));
                }

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

    @Subscribe
    public void createUser(CreateUserEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new CreateUserTask(event.username, event.password, new BaseApiAsyncTask.OnTaskFinishedListener<User>() {
                @Override
                public void onTaskSuccess(User user) {
                    bus.post(new UserCreatedEvent(user));
                }

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

//    @Subscribe
//    public void createSession(CreateSessionEvent event) {
//        if (ENG_NetUtility.isNetworkAvailable()) {
//            new CreateSessionTask(event.userToken, event.session, new BaseApiAsyncTask.OnTaskFinishedListener<Server>() {
//                @Override
//                public void onTaskSuccess(Server server) {
//                    bus.post(new SessionCreatedEvent(server));
//                }
//
//                @Override
//                public void onTaskError(RestError error) {
////                    System.out.println("Task failed");
//                    bus.post(new SessionCreationErrorEvent(error));
//                }
//            }).execute();
//        }
//    }
//
//    private class CreateSessionTask extends BaseApiAsyncTask<Server> {
//
//        //        private String userToken;
//        private final Session session;
//
//        public CreateSessionTask(String userToken, Session session, OnTaskFinishedListener callback) {
//            super(callback);

    /// /            this.userToken = userToken;
//            this.session = session;
//        }
//
//        @Override
//        protected Server doInBackground(Void... params) {
//            return (Server) executeWithReauth(new RequestExecutor() {
//                @Override
//                public GenericTransient executeWithReauth() {
//                    return responseProvider.createSession(App.getGameDesc().getUser().getAuthToken(), CreateSessionTask.this.session);
//                }
//            });
//        }
//    }
    @Subscribe
    public void updateUserAccount(UpdateUserAccountEvent event) {
        if (ENG_NetUtility.isNetworkAvailable()) {
            new UpdateUserAccountTask(event.user, new BaseApiAsyncTask.OnTaskFinishedListener<User>() {

                @Override
                public void onTaskSuccess(User user) {
                    bus.post(new UserAccountUpdatedEvent(user));
                }

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
//                    User user = App.getGameDesc().getUser();
//                    User loginUser = loginUser(user.getUsername(), user.getPassword());
//                    switch (loginUser.getErrorCode()) {
//                        case ErrorCodes.NO_ERROR:
//                            App.getGameDesc().setUser(loginUser);
//                            break;
//                        default:
//                            fatalError = true;
//                    }

                }
                break;
                case ErrorCodes.USER_ALREADY_IN_SESSION: {
//                    User leaveSession = responseProvider.leaveSession(App.getGameDesc().getUser().getAuthToken());
//                    switch (leaveSession.getErrorCode()) {
//                        case ErrorCodes.NO_ERROR:
//                            App.getGameDesc().setUser(leaveSession);
//                            break;
//                        default:
//                            fatalError = true;
//                    }
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

//    public static ClientAPI getSingleton() {
//        return App.getGameDesc().getClientAPI();
//    }
}
