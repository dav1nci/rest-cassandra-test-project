package org.bloostatics.models;

import com.datastax.driver.core.utils.UUIDs;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by stdima on 02.04.16.
 */
@Table
public class Device implements Serializable
{
    @PrimaryKey
    private UUID id = UUIDs.timeBased();
    @Column(value = "is_empty")
    private int isEmpty;

    public Device() {
        this.isEmpty = 1;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(int isEmpty) {
        this.isEmpty = isEmpty;
    }
}
