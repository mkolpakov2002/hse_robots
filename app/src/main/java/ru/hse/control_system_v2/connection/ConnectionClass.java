package ru.hse.control_system_v2.connection;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;

abstract class ConnectionClass {
    boolean isNeedToConnect;
    String connectionName;

    public void setNeedToConnect(boolean needToConnect) {
        isNeedToConnect = needToConnect;
    }

    public boolean isNeedToConnect() {
        return isNeedToConnect;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    abstract void sentData();

    abstract void closeConnection();

    abstract @NonNull Observable<Object> openConnection();


}
