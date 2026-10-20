"""从当前项目POM派生隔离的测试POM，复用真实依赖，不修改后端构建文件。"""
from pathlib import Path
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
NS = "http://maven.apache.org/POM/4.0.0"
ET.register_namespace("", NS)


def tag(name):
    return "{" + NS + "}" + name


def setchild(element, key, value):
    child = element.find(tag(key))
    if child is None:
        child = ET.SubElement(element, tag(key))
    child.text = str(value)
    return child


def prepare_pom():
    document = ET.parse(ROOT / "knife4j-spring/pom.xml")
    project = document.getroot()
    setchild(project, "artifactId", "rocgit-whitebox")
    setchild(project, "packaging", "jar")
    properties = project.find(tag("properties"))
    setchild(properties, "project.build.sourceEncoding", "UTF-8")
    build = project.find(tag("build"))
    setchild(build, "sourceDirectory", ROOT / "knife4j-spring/src/main/java")
    setchild(build, "testSourceDirectory", ROOT / "testing/java")
    plugins = build.find(tag("plugins"))
    plugin = ET.SubElement(plugins, tag("plugin"))
    setchild(plugin, "groupId", "org.jacoco")
    setchild(plugin, "artifactId", "jacoco-maven-plugin")
    setchild(plugin, "version", "0.8.12")
    executions = ET.SubElement(plugin, tag("executions"))
    for name, goal in [("prepare", "prepare-agent"), ("coverage", "report")]:
        execution = ET.SubElement(executions, tag("execution"))
        setchild(execution, "id", name)
        if goal == "report":
            setchild(execution, "phase", "test")
        goals = ET.SubElement(execution, tag("goals"))
        setchild(goals, "goal", goal)
    path = ROOT / "artifacts/testing-java/pom.xml"
    path.parent.mkdir(parents=True, exist_ok=True)
    document.write(path, encoding="utf-8", xml_declaration=True)
    return path
