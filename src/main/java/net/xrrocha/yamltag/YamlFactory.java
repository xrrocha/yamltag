package net.xrrocha.yamltag;

import org.yaml.snakeyaml.Yaml;

public interface YamlFactory {
    Yaml newYaml();
}