package simo.homes.records;

import simo.homes.models.Home;

import java.util.Map;
import java.util.UUID;

public record HomeLoadResult(
        boolean success,
        Map<UUID, Map<String, Home>> homes) {}
