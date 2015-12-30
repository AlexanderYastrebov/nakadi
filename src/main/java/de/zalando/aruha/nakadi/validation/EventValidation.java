package de.zalando.aruha.nakadi.validation;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONObject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.aruha.nakadi.domain.EventType;
import de.zalando.aruha.nakadi.domain.EventTypeSchema;
import de.zalando.aruha.nakadi.domain.ValidationStrategyConfiguration;

public class EventValidation {

	private static final Map<String, EventTypeValidator> eventTypeValidators = Maps.newConcurrentMap();

	public static EventTypeValidator forType(final EventType eventType) {

		Preconditions.checkState(!eventTypeValidators.containsKey(eventType.getName()),
				"Validator for EventType {} already defined", eventType.getName());
		final EventTypeValidator etsv = new EventTypeValidator(eventType);
		eventTypeValidators.put(eventType.getName(), etsv);
		return etsv;
	}

	public static EventTypeValidator lookup(final EventType eventType) {
		return eventTypeValidators.get(eventType.getName());
	}

}

class EventTypeValidator {

	private final EventType eventType;
	private final List<EventValidator> validators = Lists.newArrayList();

	public EventTypeValidator(final EventType eventType) {
		this.eventType = eventType;
	}

	public void validate(final JSONObject event) {
		Preconditions.checkState(!validators.stream().anyMatch(new Predicate<EventValidator>() {

			@Override
			public boolean test(final EventValidator t) {
				return !t.isValidFor(event);
			}
		}), "Some validation failed");
	}

	public EventTypeValidator withConfiguration(final ValidationStrategyConfiguration vsc) {
		final ValidationStrategy vs = ValidationStrategy.lookup(vsc.getStrategyName());
		validators.add(vs.materialize(eventType, vsc));

		return this;
	}

	public EventTypeValidator init() {

		return this;
	}

}
