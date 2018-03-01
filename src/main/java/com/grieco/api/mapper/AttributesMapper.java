package com.grieco.api.mapper;

import com.grieco.domain.model.Attributes;
import com.grieco.api.model.Parameters;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AttributesMapper
{
    public Attributes map(Parameters parameters)
    {
        return Optional.of(parameters)
                .map(Parameters::getCommand)
                .map(Attributes::new)
                .orElse(null);
    }
}
