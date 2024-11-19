package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }
    
	public Optional<List<ElectricityReading>> getReadings(String smartMeterId, int noOfDays) {

		Instant now = Instant.now();
		Instant startInstant = now.minus(noOfDays, ChronoUnit.DAYS);
		return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId).stream().filter(reading -> {
			Instant readtime = reading.time();
			return !readtime.isBefore(startInstant) && !readtime.isAfter(now);
		}).sorted(Comparator.comparing(ElectricityReading::time)).collect(Collectors.toList()))
				.filter(filteredReadings -> !filteredReadings.isEmpty());
	}

    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!meterAssociatedReadings.containsKey(smartMeterId)) {
            meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
        }
        meterAssociatedReadings.get(smartMeterId).addAll(electricityReadings);
    }
}
