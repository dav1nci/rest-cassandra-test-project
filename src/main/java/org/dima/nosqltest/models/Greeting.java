package org.dima.nosqltest.models;

import com.datastax.driver.core.utils.UUIDs;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dima on 21.02.16.
 */
@Table(value = "greetings")
public class Greeting
{
    @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID id = UUIDs.timeBased();
    @PrimaryKeyColumn(name = "user", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String user;
    @Column(value = "greet")
    private String greet;
    @Column(value = "creation_date")
    private Date creationDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGreet() {
        return greet;
    }

    public void setGreet(String greet) {
        this.greet = greet;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
