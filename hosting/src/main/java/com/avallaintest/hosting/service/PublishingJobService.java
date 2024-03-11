package com.avallaintest.hosting.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.avallaintest.hosting.types.publishingjob.AvallainPublishingJobsData;
import com.avallaintest.hosting.types.publishingjob.AvallainPublishingJobsResponse;

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
public class PublishingJobService {

	@Autowired
	private UpdateResourceService updateResourceService;

	private static final Logger logger = LoggerFactory.getLogger(PublishingJobService.class);

	public void getPublishingJobs(Date lastRun) {
		logger.info("Requesting avallain publising jobs");
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String lastRunString = df.format(lastRun);
			logger.info("lastRunString: {}", lastRunString);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			header.add("Authorization",
					"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2OTEzYTQ3YS0zMmY5LTExZWItYWM4NS0wYTQ1MDAzNTkxNzQiLCJzdWIiOiI4ODciLCJzY29wZSI6WyJhbGwiXSwiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjIwMDAwMDAwMDB9.fxG4ljzzgyRCDukPHEM5DyGdrBIF0n9wsuuWO49PLsM");

			List<AvallainPublishingJobsData> publisedLOs = new ArrayList<AvallainPublishingJobsData>();
			int pageNo = 1;
			int totalPages = Integer.MAX_VALUE;
			do {
				logger.info("Fetching pageNo: {}", pageNo);

				UriComponents builder = UriComponentsBuilder
						.fromHttpUrl("https://api-ori-author6-prod.avallain.net/api/v2/publishing_jobs")
						.queryParam("filter[from_date]", lastRunString)
						.queryParam("filter[statuses][]",
								"finished")
						.queryParam("page[number]", String.valueOf(pageNo)).build();

				MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<String, String>();
				HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestParams,
						header);

				ResponseEntity<AvallainPublishingJobsResponse> response = restTemplate.exchange(
						builder.toUriString(),
						HttpMethod.GET, httpEntity, AvallainPublishingJobsResponse.class);

				publisedLOs.addAll(response.getBody().getData());
				pageNo++;
				totalPages = response.getBody().getMeta().getTotalPages();
				logger.info("Total pages: {}", totalPages);
			} while (pageNo <= totalPages);

			List<AvallainPublishingJobsData> offlinePublisedLOs = publisedLOs.stream()
					.filter(data -> data.getRelationships().getPublishingType() != null
							&& data.getRelationships().getLearningObject() != null
							&& data.getAttributes().isDownloadable()
							&& ("1".equals(data.getRelationships().getPublishingType().getData().getId())
									|| "13".equals(data.getRelationships().getPublishingType().getData().getId())
									|| "18".equals(data.getRelationships().getPublishingType().getData().getId())))
					.collect(Collectors.toList());
			logger.info("publishing jobs count: {}", offlinePublisedLOs.size());
			for (AvallainPublishingJobsData jobsData : offlinePublisedLOs) {
				updateResourceService.updateLO(jobsData);
			}
		} catch (Exception ex) {
			logger.error("Execption while requesting avallain publising jobs : {} ", ex);
			throw ex;
		}
	}
}