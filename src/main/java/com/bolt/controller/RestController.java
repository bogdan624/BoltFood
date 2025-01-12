package com.bolt.controller;

import com.bolt.services.IPlayerGamerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping
public class RestController {

    private IPlayerGamerService services;

    @Autowired
    public RestController(IPlayerGamerService services) {
        this.services = services;
    }

}
