package com.avallaintest.hosting.controller;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.avallaintest.hosting.config.AppConfig;
import com.avallaintest.hosting.dao.ResourceRepository;
import com.avallaintest.hosting.model.Resource;
import com.avallaintest.hosting.types.ResourceType;

@Controller
@RequestMapping("/resources")
public class ResourceController {

        @Autowired
        private ResourceRepository resourceRepository;

        @Autowired
        private AppConfig appConfig;

        private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

        @RequestMapping(value = "/{resource_name}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
        public String loadLO(@PathVariable("resource_name") String resourceName,
                        @RequestParam(value = "author_uid", required = false) String authorUid,
                        RedirectAttributes redirectAttributes) {
                logger.info("{}, {}", resourceName, authorUid);
                Resource resource = resourceRepository.findByName(resourceName);
                if (resource.getResourceType().equals(ResourceType.ISJ)
                                || resource.getResourceType().equals(ResourceType.LTI)) {
                        redirectAttributes.addAttribute("endpoint", appConfig.getLrsServerHost() + "/xapi");
                        redirectAttributes
                                        .addAttribute("actor",
                                                        "{\"mbox\":\"mailto:vikash.kumar@everest.engineering\",\"name\":\"Vikash\"}");
                        redirectAttributes
                                        .addAttribute("registration", "30f0dc7a-6989-4a5d-8940-99c4a67c1eb3");
                        redirectAttributes
                                        .addAttribute("activity_id",
                                                        appConfig.getStaticServerHost() + "/" + resourceName);
                        String auth=Base64.getEncoder().encodeToString((appConfig.getLrsKey()+":"+appConfig.getLrsSecret()).getBytes());
                        return String.format("redirect:%s/%s/",
                                        appConfig.getStaticServerHost(), resourceName)
                                        + "?auth=Basic%20" + auth;
                } else if (resource.getResourceType().equals(ResourceType.FLARE)) {
                        return String.format("redirect:%s/flare_engine?xml_url=%s/%s/%s.xml",
                                        appConfig.getStaticServerHost(),
                                        appConfig.getStaticServerHost(), resourceName, resourceName);
                } else if (resource.getResourceType().equals(ResourceType.SIE)) {
                        return String.format("redirect:%s/sie_engine?xml_url=%s/%s/%s.xml",
                                        appConfig.getStaticServerHost(),
                                        appConfig.getStaticServerHost(), resourceName, resourceName);
                }
                return "google.com";
        }

}
