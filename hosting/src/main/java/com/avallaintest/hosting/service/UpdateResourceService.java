package com.avallaintest.hosting.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.avallaintest.hosting.config.AppConfig;
import com.avallaintest.hosting.dao.ResourceRepository;
import com.avallaintest.hosting.model.Resource;
import com.avallaintest.hosting.types.ResourceType;
import com.avallaintest.hosting.types.publishingjob.AvallainPublishingJobsData;

@Service
public class UpdateResourceService {

    private static final Logger logger = LoggerFactory.getLogger(PublishingJobService.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ResourceRepository resourceRepository;

    public void updateLO(AvallainPublishingJobsData jobsData) {
        Integer resourceId = Integer.parseInt(jobsData.getRelationships().getLearningObject().getData().getId());
        Resource resource = resourceRepository.findByResourceId(resourceId);
        if (resource == null) {
            resource = new Resource();
        }
        resource.setResourceId(resourceId);
        downloadLO(jobsData);
        if (!"EXTRACTED".equals(jobsData.getStatus())) {
            return;
        }
        logger.info("Extracted successfully");
        determineLOType(jobsData, resource);
        updateLOContent(jobsData, resource);
        resourceRepository.save(resource);
    }

    private void downloadLO(AvallainPublishingJobsData jobsData) {
        String folderName = jobsData.getRelationships().getLearningObject().getData().getId();
        jobsData.setStatus("EXTRACTED");
        jobsData.setDownloadFolder(appConfig.getStaticDir() + "/" + folderName);
        try {
            Files.deleteIfExists(Paths.get(appConfig.getStaticDir() + "/" + folderName + ".zip"));
            FileUtils.deleteDirectory(Paths.get(appConfig.getStaticDir() + "/" + folderName).toFile());
        } catch (Exception ex) {
            logger.error("Failed to delete", ex.getMessage());
        }
        try (BufferedInputStream in = new BufferedInputStream(
                new URI(jobsData.getAttributes().getDownloadUrl()).toURL().openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(appConfig.getStaticDir() + "/" +
                        folderName + ".zip")) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            unzip(Paths.get(appConfig.getStaticDir() + "/" + folderName + ".zip"));
        } catch (Exception e) {
            jobsData.setStatus(e.getMessage());
            logger.error("Failed to download", e.getMessage());
        }
    }

    private void unzip(Path path) throws IOException {
        String fileBaseName = FilenameUtils.getBaseName(path.getFileName().toString());
        Path destFolderPath = Paths.get(path.getParent().toString(), fileBaseName);

        try (ZipFile zipFile = new ZipFile(path.toFile(), ZipFile.OPEN_READ)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = destFolderPath.resolve(entry.getName());
                if (entryPath.normalize().startsWith(destFolderPath.normalize())) {
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        try (InputStream in = zipFile.getInputStream(entry)) {
                            try (OutputStream out = new FileOutputStream(entryPath.toFile())) {
                                IOUtils.copy(in, out);
                            }
                        }
                    }
                }
            }
        }
    }

    public void determineLOType(AvallainPublishingJobsData jobsData, Resource resource) {
        ResourceType loType = null;
        String learnosityActivityId = null;
        try {
            File file = new File(jobsData.getDownloadFolder() + "/LearningObjectInfo.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            XPath xpath = XPathFactory.newInstance().newXPath();
            String name = xpath.evaluate("/LearningObjectInfo/name", doc);
            logger.info("LO Name: {}", name);
            resource.setName(name);
            String code = xpath.evaluate("/LearningObjectInfo/metadata/lom/group[6]/item/value/@description", doc);
            System.out.println("Description: " + code);
            if (code.equals("")) {
                loType = ResourceType.ISJ;
            } else if (code.equals("sie")) {
                loType = ResourceType.SIE;
            } else if (code.equals("flare")) {
                loType = ResourceType.FLARE;
            } else if (code.startsWith("lti-dsa?")) {
                if (code.contains("action=reporting")) {
                    loType = ResourceType.LTI_REPORT;
                } else {
                    loType = ResourceType.LTI;
                    learnosityActivityId = code.split("template=")[1];
                }
            }
            resource.setResourceType(loType);
            resource.setResourceData(learnosityActivityId);
        } catch (Exception ex) {
            logger.error("xml parsing failed", ex);
        }
        logger.info("LO: {}", resource.toString());
    }

    private void updateLOContent(AvallainPublishingJobsData jobsData, Resource resource) {
        try {
            FileUtils.deleteDirectory(Paths.get(appConfig.getStaticDir() + "/" + resource.getName()).toFile());
            Files.move(Paths.get(jobsData.getDownloadFolder()),
                    Paths.get(appConfig.getStaticDir() + "/" + resource.getName()),
                    StandardCopyOption.REPLACE_EXISTING);

            jobsData.setDownloadFolder(appConfig.getStaticDir() + "/" + resource.getName());

            if (Files.exists(Paths.get(jobsData.getDownloadFolder() + "/engine"))) {
                FileUtils.deleteDirectory(Paths.get(appConfig.getStaticDir() + "/engine").toFile());
                Files.move(Paths.get(jobsData.getDownloadFolder() + "/engine"),
                        Paths.get(appConfig.getStaticDir() + "/engine"),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            if (Files.exists(Paths.get(jobsData.getDownloadFolder() + "/sk"))) {
                FileUtils.deleteDirectory(
                        Paths.get(appConfig.getStaticDir() + "/sk_" + jobsData.getAttributes().getDesignPackageId())
                                .toFile());
                Files.move(Paths.get(jobsData.getDownloadFolder() + "/sk"),
                        Paths.get(appConfig.getStaticDir() + "/sk_" + jobsData.getAttributes().getDesignPackageId()),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            if (Files.exists(Paths.get(jobsData.getDownloadFolder() + "/tincan.js"))) {
                Files.deleteIfExists(
                        Paths.get(appConfig.getStaticDir() + "/tincan.js"));
                Files.move(Paths.get(jobsData.getDownloadFolder() + "/tincan.js"),
                        Paths.get(appConfig.getStaticDir() + "/tincan.js"),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            if (Files.exists(Paths.get(jobsData.getDownloadFolder() + "/index.html"))) {
                Path indexFile = Paths.get(jobsData.getDownloadFolder() + "/index.html");
                Charset charset = StandardCharsets.UTF_8;
                String content = new String(Files.readAllBytes(indexFile), charset);
                content = content.replaceAll("engine/", "../engine/");
                content = content.replaceAll("sk/", "../sk_" + jobsData.getAttributes().getDesignPackageId() + "/");
                content = content.replaceAll(
                        "<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\" />",
                        """
                                        <meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\" />
                                        <script type="text/javascript" src="../tincan.js"></script>
                                """);
                content = content.replaceAll("A5.LMS_APIS = 'Console';", "A5.LMS_APIS = 'TinCan';");
                if (ResourceType.LTI.equals(resource.getResourceType())
                        || ResourceType.LTI_REPORT.equals(resource.getResourceType())) {
                    Path ltiIndexFile = Paths.get(appConfig.getLtiStaticDir() + "/index.html");
                    content = new String(Files.readAllBytes(ltiIndexFile), charset);
                }
                Files.write(indexFile, content.getBytes(charset));
            }
        } catch (Exception ex) {
            logger.error("Failed: ", ex);
        }
    }

}
