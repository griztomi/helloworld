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
    private AttributesMapper attributesMapper;
    private Poller poller;
    private OutputMapper outputMapper;

    @Autowired
    public Controller(AttributesMapper attributesMapper, Poller poller, OutputMapper outputMapper)
    {
        this.attributesMapper = attributesMapper;
        this.poller = poller;
        this.outputMapper = outputMapper;
    }

    @RequestMapping(value = "/poll", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String poll(Parameters parameters)
    {
        return Optional.of(parameters)
                .map(attributesMapper::map)
                .map(poller::poll)
                .map(outputMapper::mapPollResult)
                .orElse("Unexpected Error. See logs!");
    }
}
