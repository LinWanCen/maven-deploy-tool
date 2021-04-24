package io.github.linwancen.maven.tool.deploy.task.repo;

import io.github.linwancen.util.PathUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

class PomParser {

    private static final Logger LOG = LoggerFactory.getLogger(PomParser.class);

    private PomParser() {}

    static Pom parse(File pomFile) {
        SAXReader reader = new SAXReader();
        Document document;
        try {
            // 避免 XXE attacks
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            document = reader.read(pomFile);
        } catch (Exception e) {
            String path = PathUtils.canonicalPath(pomFile);
            LOG.error("parse pom.xml Exception\tfile:///{}", path, e);
            return null;
        }
        return parse(document);
    }

    private static Pom parse(Document document) {
        Element root = document.getRootElement();
        Pom parentPom = parse(root.element("parent"), null);
        return parse(root, parentPom);
    }

    private static Pom parse(Element root, Pom parentPom) {
        if (root == null) {
            return null;
        }
        String groupId = root.elementText("groupId");
        String artifactId = root.elementText("artifactId");
        String version = root.elementText("version");
        String packaging = root.elementText("packaging");
        String classifier = root.elementText("classifier");
        if (parentPom != null) {
            return new Pom(parentPom, groupId, artifactId, version, packaging, classifier);
        } else {
            return new Pom(groupId, artifactId, version, packaging, classifier);
        }
    }
}
