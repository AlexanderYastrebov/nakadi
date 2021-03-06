package de.zalando.aruha.nakadi.domain;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;

import static com.google.common.collect.Sets.newTreeSet;

public class Subscription {

    @Nullable
    private String id;

    @NotNull
    private String owningApplication;

    @NotNull
    @Size(min = 1, max = 1)
    private Set<String> eventTypes;

    private String consumerGroup = "none";

    private DateTime createdAt = new DateTime(DateTimeZone.UTC);

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getOwningApplication() {
        return owningApplication;
    }

    public void setOwningApplication(final String owningApplication) {
        this.owningApplication = owningApplication;
    }

    public Set<String> getEventTypes() {
        return Collections.unmodifiableSet(eventTypes);
    }

    public void setEventTypes(final Set<String> eventTypes) {
        this.eventTypes = newTreeSet(eventTypes);
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(final String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Subscription that = (Subscription) o;
        return id != null ? id.equals(that.id) : that.id == null && owningApplication.equals(that.owningApplication) &&
                eventTypes.equals(that.eventTypes) && consumerGroup.equals(that.consumerGroup) && createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + owningApplication.hashCode();
        result = 31 * result + eventTypes.hashCode();
        result = 31 * result + consumerGroup.hashCode();
        result = 31 * result + createdAt.hashCode();
        return result;
    }
}
