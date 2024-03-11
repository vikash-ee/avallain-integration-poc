package com.avallaintest.hosting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.avallaintest.hosting.dao.ModuleRepository;
import com.avallaintest.hosting.dao.ResourceRepository;
import com.avallaintest.hosting.dao.CourseRepository;
import com.avallaintest.hosting.model.Course;
import com.avallaintest.hosting.model.Module;
import com.avallaintest.hosting.model.Resource;
import com.avallaintest.hosting.types.publishingjob.AvallainPublishingJobsResponse;
import com.avallaintest.hosting.types.structure.AvallainStructureAttributeEntity;
import com.avallaintest.hosting.types.structure.AvallainStructureData;
import com.avallaintest.hosting.types.structure.AvallainStructureNode;
import com.avallaintest.hosting.types.structure.AvallainStructureResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private ModuleRepository moduleRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	private static final Logger logger = LoggerFactory.getLogger(PublishingJobService.class);

	public void syncCourses() {
		logger.info("Requesting avallain publising jobs");
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			header.add("Authorization",
					"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2OTEzYTQ3YS0zMmY5LTExZWItYWM4NS0wYTQ1MDAzNTkxNzQiLCJzdWIiOiI4ODciLCJzY29wZSI6WyJhbGwiXSwiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjIwMDAwMDAwMDB9.fxG4ljzzgyRCDukPHEM5DyGdrBIF0n9wsuuWO49PLsM");

			List<String> structureIds = new ArrayList<String>();
			int pageNo = 1;
			int totalPages = Integer.MAX_VALUE;
			do {
				logger.info("Fetching pageNo: {}", pageNo);
				UriComponents structuresUrlBuilder = UriComponentsBuilder
						.fromHttpUrl("https://api-ori-author6-prod.avallain.net/api/v2/structures")
						.queryParam("page[number]", String.valueOf(pageNo))
						.build();
				MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<String, String>();
				HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestParams,
						header);
				ResponseEntity<AvallainPublishingJobsResponse> response = restTemplate.exchange(
						structuresUrlBuilder.toUriString(),
						HttpMethod.GET, httpEntity, AvallainPublishingJobsResponse.class);
				structureIds.addAll(
						response.getBody().getData().stream().map(data -> data.getId()).collect(Collectors.toList()));
				pageNo++;
				totalPages = response.getBody().getMeta().getTotalPages();
				logger.info("Total pages: {}", totalPages);
			} while (pageNo <= totalPages);

			for (String structureId : structureIds) {
				UriComponents builder = UriComponentsBuilder
						.fromHttpUrl("https://api-ori-author6-prod.avallain.net/api/v2/structures/" + structureId)
						.build();
				MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<String, String>();
				HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestParams,
						header);
				ResponseEntity<AvallainStructureResponse> response = restTemplate.exchange(
						builder.toUriString(),
						HttpMethod.GET, httpEntity, AvallainStructureResponse.class);

				AvallainStructureData data = response.getBody().getData();
				String rootNodeId = data.getAttributes().getData().getRootStructureNodeId();
				String structureName = data.getAttributes().getData().getEntities().getStructureNodes().get(rootNodeId)
						.getLabel();
				var entity = data.getAttributes().getData().getEntities();

				Course course = courseRepository.findByStructureId(structureId);
				if (course == null) {
					course = new Course();
				}
				course.setStructureId(structureId);
				course.setRootNodeId(rootNodeId);
				course.setName(structureName);
				course.setRootModule(setModules(entity, rootNodeId, null, course, new HashMap<Integer, Resource>()));

				courseRepository.saveAndFlush(course);
			}
		} catch (Exception ex) {
			logger.error("Execption while requesting avallain structures : {} ", ex);
			throw ex;
		}
	}

	private Module setModules(AvallainStructureAttributeEntity entity, String nodeId,
			Module parentModule, Course course, Map<Integer, Resource> newResources) {
		AvallainStructureNode node = entity.getStructureNodes().get(nodeId);
		Module module = moduleRepository.findByModuleId(node.getId());
		if (module == null) {
			module = new Module();
		}
		module.setLabel(node.getLabel());
		module.setModuleId(node.getId());
		module.setCourse(course);
		module.setParent(parentModule);
		List<Module> childModules = new ArrayList<Module>();
		for (String childNodeId : node.getChildrenIds()) {
			Module childModule = setModules(entity, childNodeId, module, null, newResources);
			childModules.add(childModule);
		}
		for (int resourceId : node.getLearningObjectIds()) {
			Resource resource = resourceRepository.findByResourceId(resourceId);
			if (resource == null) {
				resource = newResources.get(resourceId);
				if (resource == null) {
					resource = new Resource();
				}
			}
			resource.setResourceId(resourceId);
			resource.setName(entity.getLearningObjects().get(String.valueOf(resourceId)).getName());
			resource.getModules().add(module);
			module.getResources().add(resource);
			newResources.put(resourceId, resource);
		}
		module.getChildModules().addAll(childModules);
		return module;
	}
}