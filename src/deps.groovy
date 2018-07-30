import groovy.xml.MarkupBuilder

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * @author inikolaev
 */
def folder = new File(args[0]);
def files = folder.listFiles({ File dir, String name -> name.toLowerCase().endsWith(".jar") } as FilenameFilter)
def pomWriter = new FileWriter("dependencies.txt")
def noPomWriter = new FileWriter("nopom.txt")
def xml = new MarkupBuilder(pomWriter)
xml.dependencies {
    files.each { jarFile ->
        def zipFile = new ZipFile(jarFile)
        def pom = zipFile.entries().find {
            !it.directory && it.name.toLowerCase().endsWith("/pom.properties")
        } as ZipEntry
        if (pom != null) {
            Properties props = new Properties()
            props.load(zipFile.getInputStream(pom))
            dependency {
                groupId props.getProperty("groupId")
                artifactId props.getProperty("artifactId")
                version props.getProperty("version")
            }
        } else {
            noPomWriter.write(jarFile.name + "\n")
        }
    }
}
pomWriter.close()
noPomWriter.close()