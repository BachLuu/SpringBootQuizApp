package com.example.springbootweb.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
 componentModel = "spring",
 injectionStrategy = InjectionStrategy.CONSTRUCTOR,
 unmappedTargetPolicy = ReportingPolicy.ERROR, 
 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CommonMapperConfig {
}
