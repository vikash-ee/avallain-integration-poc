package com.avallaintest.hosting.controller;

import learnositysdk.request.Init;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avallaintest.hosting.config.LearnosityCreds;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/learnosity")
@CrossOrigin(origins = "*")
public class LearnosityController {

    private UUID user_id = UUID.randomUUID();
    private UUID session_id = UUID.randomUUID();

    @Autowired
    private LearnosityCreds learnosityCreds;

    @GetMapping("/request")
    public Object getRequest(HttpServletRequest request) {
        return initOptions(request.getServerName());
    }

    public String initOptions(String domain) {
        Map<String, String> security = createSecurityObject(domain);
        Map<String, String> request = createRequestObject();
        String secret = learnosityCreds.getConsumerSecret();
        try {
            Init init = new Init("items", security, secret, request);
            return init.generate();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Map<String, String> createSecurityObject(String domain) {
        var s = new HashMap<String, String>();
        s.put("domain", domain);
        s.put("consumer_key", learnosityCreds.getConsumerKey());
        return s;
    }

    private Map<String, String> createRequestObject() {
        var r = new HashMap<String, String>();
        r.put("user_id", this.user_id.toString());
        r.put("activity_template_id", "places_to_visit");
        r.put("session_id", this.session_id.toString());
        r.put("activity_id", "places_to_visit");
        r.put("rendering_type", "assess");
        r.put("type", "submit_practice");
        r.put("name", "Items API Quickstart");
        r.put("state", "initial");
        return r;
    }
    
}
