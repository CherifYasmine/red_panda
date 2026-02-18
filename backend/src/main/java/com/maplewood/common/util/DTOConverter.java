package com.maplewood.common.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for converting entities to DTOs
 */
public class DTOConverter {
    
    /**
     * Convert a single entity to DTO
     */
    public static <E, D> D convert(E entity, Function<E, D> mapper) {
        return entity != null ? mapper.apply(entity) : null;
    }
    
    /**
     * Convert a list of entities to list of DTOs
     */
    public static <E, D> List<D> convertList(List<E> entities, Function<E, D> mapper) {
        if (entities == null) return List.of();
        return entities.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }
}
