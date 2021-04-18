package io.github.linwancen.maven.tool.deploy.task.repo;

class Pom {
    public final String groupId;
    public final String artifactId;
    public final String version;
    public final String packaging;
    public final String classifier;
    public final Pom parent;

    Pom(String groupId, String artifactId, String version, String packaging, String classifier) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.packaging = packaging == null ? "jar" : packaging;
        this.classifier = classifier;
        this.parent = null;
    }

    Pom(Pom parent, String groupId, String artifactId, String version, String packaging, String classifier) {
        this.groupId = groupId == null ? parent.groupId : groupId;
        this.artifactId = artifactId;
        this.version = version == null ? parent.version : version;
        this.packaging = packaging == null ? "jar" : packaging;
        this.classifier = classifier;
        this.parent = parent;
    }
}
