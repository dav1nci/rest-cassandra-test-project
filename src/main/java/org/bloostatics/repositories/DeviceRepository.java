package org.bloostatics.repositories;

import org.bloostatics.models.Device;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.UUID;

/**
 * Created by stdima on 03.04.16.
 */
public interface DeviceRepository extends CassandraRepository<Device> {
    @Query("select * from device where id = ?0")
    Device findById(UUID deviceId);
    @Query("update device set is_empty = 0 where id = ?0")
    Device setDeviceIsBusy(UUID deviceId);
}
