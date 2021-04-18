package io.github.linwancen.maven.tool.deploy.task.repo;

class Pom {
    public final String groupId;
    public final String artifactId;
    public final String version;
    public final String packaging;
    public final String classifier;

    Pom(String groupId, String artifactId, String version, String packaging, String classifier) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.packaging = packaging == null ? "jar" : packaging;
        this.classifier = classifier;
    }

    Pom(Pom parent, String groupId, String artifactId, String version, String packaging, String classifier) {
        this.groupId = groupId == null ? parent.groupId : groupId;
        this.artifactId = artifactId;
        this.version = version == null ? parent.version : version;
        this.packaging = packaging == null ? "jar" : packaging;
        this.classifier = classifier;
    }

    /**
     * <br/>groupId:artifactId:version:packaging:classifier
     */
    @Override
    public String toString() {
        return (groupId == null ? "" : groupId) + ":" +
                (artifactId == null ? "" : artifactId) + ":" +
                (version == null ? "" : version) + ":" +
                packaging + ":" +
                (classifier == null ? "" : classifier);
    }
}
