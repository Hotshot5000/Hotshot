package headwayent.blackholedarksun.net.clientapi.tables;

public class TransportStatus extends GenericTransient {

    private long stationId;
    private String stationName;
    private double stationDistance;
    private boolean transportTaken;

    public TransportStatus() {

    }

    public TransportStatus(Object[] resultSet) {
        stationId = (long) resultSet[0];
        stationName = (String) resultSet[1];
        stationDistance = (double) resultSet[2];
        transportTaken = (boolean) resultSet[3];
    }

    public long getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public double getStationDistance() {
        return stationDistance;
    }

    public boolean isTransportTaken() {
        return transportTaken;
    }
}
