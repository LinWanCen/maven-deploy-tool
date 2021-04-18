package io.github.linwancen.maven.tool;

public class MvnKey {

    public static final String GROUP_ID = " -D groupId=";
    public static final String ARTIFACT_ID = " -D artifactId=";
    public static final String VERSION = " -D version=";
    public static final String PACKAGING = " -D packaging=";
    // 未来 GavFromPath 支持 classifier 用
    @SuppressWarnings("unused")
    public static final String CLASSIFIER = " -D classifier=";

    public static final String SETTINGS = " -s";
    public static final String URL = " -D url=";
    public static final String REPOSITORY_ID = " -D repositoryId=";

    public static final String JAR_PACKAGING_FILE = PACKAGING + "jar -D file=";
    public static final String POM_PACKAGING_FILE = PACKAGING + "pom -D file=";
    public static final String SRC = " -D sources=";
    public static final String DOC = " -D javadoc=";
    public static final String POM_FILE = " -D pomFile=";
    public static final String NO_GEN_POM = " -D generatePom=false";

    private MvnKey() {}
}
