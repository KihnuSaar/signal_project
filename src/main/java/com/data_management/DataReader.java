package com.data_management;

import java.io.IOException;

public interface DataReader {

    /**
     * Reads data from a source and stores it in DataStorage.
     * @param dataStorage storage where the data will be saved
     * @throws IOException if reading fails
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Stops the reader if it uses a continuous connection.
     */
    default void stop() {
        // Not needed for normal file readers
    }
}
