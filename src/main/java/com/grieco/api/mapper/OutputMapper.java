package com.grieco.api.mapper;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OutputMapper
{
    public String map(String pollResult)
    {
        return pollResult;
    }
}
