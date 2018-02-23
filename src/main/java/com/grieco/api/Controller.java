package com.grieco.api;

import com.grieco.api.mapper.AttributesMapper;
import com.grieco.api.mapper.OutputMapper;
import com.grieco.api.model.Parameters;
import com.grieco.domain.Poller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class Controller
{
    @Autowired
    private AttributesMapper attributesMapper;
    @Autowired
    private Poller poller;
    @Autowired
    private OutputMapper outputMapper;

    @RequestMapping(value = "/ssh", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String ssh(Parameters parameters)
    {
        return Optional.of(parameters)
                .map(attributesMapper::map)
                .map(poller::poll)
                .map(outputMapper::map)
                .orElse("Error");
    }
}
