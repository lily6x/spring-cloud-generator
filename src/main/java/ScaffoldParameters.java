import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ScaffoldParameters {

    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;
    private final String dbSchema;

    private ScaffoldParameters(Properties properties) {
        this.dbUrl = require(properties, "db.url");
        this.dbUserName = require(properties, "db.username");
        this.dbPassword = require(properties, "db.password");
        this.dbSchema = require(properties, "db.schema");
    }

    public static ScaffoldParameters load(String location) {
        Properties properties = new Properties();
        try (InputStream inputStream = openStream(location)) {
            properties.load(inputStream);
            return new ScaffoldParameters(properties);
        } catch (IOException e) {
            throw new UncheckedIOException("无法加载生成配置：" + location, e);
        }
    }

    public void applyTo(Generator.GeneratorConf generatorConf) {
        generatorConf.setDbUrl(dbUrl);
        generatorConf.setDbUserName(dbUserName);
        generatorConf.setDbPassword(dbPassword);
        generatorConf.setDbSchema(dbSchema);
    }

    private static InputStream openStream(String location) throws IOException {
        if (location != null) {
            Path path = Path.of(location);
            if (Files.exists(path)) {
                return Files.newInputStream(path);
            }
        }
        InputStream resourceStream = ScaffoldParameters.class.getClassLoader().getResourceAsStream(location);
        if (resourceStream == null) {
            throw new IOException("配置文件不存在：" + location);
        }
        return resourceStream;
    }

    private String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("缺少必要的生成参数：" + key);
        }
        return value.trim();
    }
}
