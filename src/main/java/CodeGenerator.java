import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.lily.generator.EngineVelocityTemplateEngine;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class CodeGenerator {

    private static final String DEFAULT_CONFIG_LOCATION = "generator-config.properties";

    public static void main(String[] args) {
        String configLocation = args.length > 0 ? args[0] : DEFAULT_CONFIG_LOCATION;
        new CodeGenerator().runInteractive(configLocation);
    }

    public static void execute() {
        new CodeGenerator().runInteractive(DEFAULT_CONFIG_LOCATION);
    }

    public static void execute(Generator.GeneratorConf generatorConf) {
        new CodeGenerator().generate(generatorConf);
    }

    public static void uuid() {
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString().replace("-", "");
        String shortUuid = uuidStr.substring(0, 32);
        System.out.println("32位UUID: " + shortUuid);
    }

    private void runInteractive(String configLocation) {
        ScaffoldParameters dataSource = ScaffoldParameters.load(configLocation);
        Generator.GeneratorConf generatorConf = promptGeneratorConf();
        dataSource.applyTo(generatorConf);
        generate(generatorConf);
    }

    private void generate(Generator.GeneratorConf generatorConf) {
        configureTemplateEngine(generatorConf);
        prepareDirectories(generatorConf);
        VelocityContext context = buildVelocityContext(generatorConf);
        generateProjectSkeleton(generatorConf, context);
        runAutoGenerator(generatorConf);
    }

    private Generator.GeneratorConf promptGeneratorConf() {
        Scanner scanner = new Scanner(System.in);
        Generator.GeneratorConf generatorConf = new Generator.GeneratorConf();
        generatorConf.setGroupId(promptRequired(scanner, "请输入 groupId"));
        generatorConf.setArtifactId(promptRequired(scanner, "请输入 artifactId"));
        generatorConf.setModel(promptRequired(scanner, "请输入模块名 model"));
        generatorConf.setInclude(promptRequired(scanner, "请输入需要生成的表名（逗号分隔）"));
        generatorConf.setBasePath(promptWithDefault(scanner, "请输入项目输出的根路径", Generator.BASE_PATH));
        generatorConf.setAuthor(promptWithDefault(scanner, "请输入作者", Generator.AUTHOR));
        return generatorConf;
    }

    private String promptRequired(Scanner scanner, String message) {
        while (true) {
            System.out.print(message + ": ");
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("该参数必填，请重新输入。");
        }
    }

    private String promptWithDefault(Scanner scanner, String message, String defaultValue) {
        System.out.printf("%s (默认: %s): ", message, defaultValue);
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? defaultValue : value;
    }

    private void configureTemplateEngine(Generator.GeneratorConf generatorConf) {
        EngineVelocityTemplateEngine.MODEL = generatorConf.getModel();
        EngineVelocityTemplateEngine.GROUP_ID = generatorConf.getGroupId();
    }

    private void prepareDirectories(Generator.GeneratorConf generatorConf) {
        String basePackagePath = generatorConf.getGroupId().replace('.', '/');
        List<String> directories = List.of(
                generatorConf.getProjectPath(),
                generatorConf.getModelApiPath(),
                generatorConf.getModelClientPath(),
                generatorConf.getModelProviderPath(),
                generatorConf.getModelProviderPath() + "/src/main/resources",
                generatorConf.getModelClientPath() + "/src/main/resources",
                generatorConf.getModelProviderPath() + "/src/main/java/" + basePackagePath,
                generatorConf.getModelProviderPath() + "/src/main/java/" + basePackagePath + "/infrastructure/conf",
                generatorConf.getModelProviderPath() + "/src/main/java/" + basePackagePath + "/infrastructure/common",
                generatorConf.getModelApiPath() + "/src/main/java/" + basePackagePath + "/domain",
                generatorConf.getModelApiPath() + "/src/main/java/" + basePackagePath + "/common"
        );
        directories.forEach(Generator::createDirectory);
    }

    private VelocityContext buildVelocityContext(Generator.GeneratorConf generatorConf) {
        VelocityContext context = new VelocityContext();
        context.put("model", generatorConf.getModel());
        context.put("groupId", generatorConf.getGroupId());
        context.put("artifactId", generatorConf.getArtifactId());
        context.put("api", generatorConf.getModelApi());
        context.put("client", generatorConf.getModelClient());
        context.put("provider", generatorConf.getModelProvider());
        return context;
    }

    private void generateProjectSkeleton(Generator.GeneratorConf generatorConf, VelocityContext context) {
        String basePackagePath = generatorConf.getGroupId().replace('.', '/');
        String providerJavaBase = generatorConf.getModelProviderPath() + "/src/main/java/" + basePackagePath;
        String apiJavaBase = generatorConf.getModelApiPath() + "/src/main/java/" + basePackagePath;

        Map<String, String> templateMapping = new LinkedHashMap<>();
        templateMapping.put(generatorConf.getProjectPath() + "/pom.xml", "templates/parent.pom.vm");
        templateMapping.put(generatorConf.getProjectPath() + "/Dockerfile", "templates/Dockerfile.vm");
        templateMapping.put(generatorConf.getProjectPath() + "/.gitignore", "templates/.gitignore.vm");
        templateMapping.put(generatorConf.getModelApiPath() + "/pom.xml", "templates/api.pom.vm");
        templateMapping.put(generatorConf.getModelClientPath() + "/pom.xml", "templates/client.pom.vm");
        templateMapping.put(generatorConf.getModelProviderPath() + "/pom.xml", "templates/provider.pom.vm");
        templateMapping.put(generatorConf.getModelProviderPath() + "/src/main/resources/application.yml", "templates/application.yml.vm");
        templateMapping.put(generatorConf.getModelProviderPath() + "/src/main/resources/logback-spring.xml", "templates/logback-spring.xml.vm");
        templateMapping.put(generatorConf.getModelProviderPath() + "/src/main/resources/bootstrap.yml", "templates/bootstrap.yml.vm");
        templateMapping.put(generatorConf.getModelClientPath() + "/src/main/resources/application.yml", "templates/client-application.yml.vm");
        templateMapping.put(providerJavaBase + "/ApplicationMain.java", "templates/application.main.java.vm");
        templateMapping.put(providerJavaBase + "/infrastructure/conf/SwaggerConfig.java", "templates/swagger.config.java.vm");
        templateMapping.put(providerJavaBase + "/infrastructure/conf/MyBatisPlusConfig.java", "templates/mybatis.plus.config.java.vm");
        templateMapping.put(providerJavaBase + "/infrastructure/conf/MyMetaObjectHandler.java", "templates/mybatis.plus.fill.java.vm");
        templateMapping.put(providerJavaBase + "/infrastructure/common/BaseEntity.java", "templates/base.entity.java.vm");
        templateMapping.put(providerJavaBase + "/infrastructure/common/ObjectConvert.java", "templates/object.convert.java.vm");
        templateMapping.put(apiJavaBase + "/domain/BaseDM.java", "templates/base.dm.java.vm");
        templateMapping.put(apiJavaBase + "/common/BaseResponse.java", "templates/base.response.java.vm");
        templateMapping.put(apiJavaBase + "/common/PageDM.java", "templates/page.dm.java.vm");

        templateMapping.forEach((target, template) -> Generator.generatorCus(target, template, context));
    }

    private void runAutoGenerator(Generator.GeneratorConf generatorConf) {
        AutoGenerator autoGenerator = new AutoGenerator(buildDataSourceConfig(generatorConf));
        autoGenerator.packageInfo(buildPackageConfig(generatorConf));
        autoGenerator.global(buildGlobalConfig(generatorConf));
        autoGenerator.strategy(buildStrategyConfig(generatorConf));
        autoGenerator.injection(buildInjectionConfig(generatorConf));
        autoGenerator.execute(new EngineVelocityTemplateEngine());
        cleanupPersistentApi(generatorConf);
    }

    private DataSourceConfig buildDataSourceConfig(Generator.GeneratorConf generatorConf) {
        return new DataSourceConfig.Builder(
                generatorConf.getDbUrl(),
                generatorConf.getDbUserName(),
                generatorConf.getDbPassword())
                .schema(generatorConf.getDbSchema())
                .build();
    }

    private PackageConfig buildPackageConfig(Generator.GeneratorConf generatorConf) {
        return new PackageConfig.Builder()
                .parent(generatorConf.getGroupId() + ".infrastructure.persistent")
                .controller("api")
                .service("service")
                .serviceImpl("service.impl")
                .entity("entity")
                .mapper("mapper")
                .xml("mapper.xml")
                .build();
    }

    private GlobalConfig buildGlobalConfig(Generator.GeneratorConf generatorConf) {
        return new GlobalConfig.Builder()
                .outputDir(generatorConf.getModelProviderPath() + "/src/main/java")
                .author(generatorConf.getAuthor())
                .enableSpringdoc()
                .disableOpenDir()
                .build();
    }

    private StrategyConfig buildStrategyConfig(Generator.GeneratorConf generatorConf) {
        return new StrategyConfig.Builder()
                .addInclude(generatorConf.getInclude())
                .entityBuilder()
                .enableLombok()
                .superClass(generatorConf.getGroupId() + ".infrastructure.common.BaseEntity")
                .addSuperEntityColumns("created_by", "created_time", "updated_by", "updated_time", "deleted")
                .idType(IdType.ASSIGN_UUID)
                .logicDeleteColumnName("deleted")
                .addTableFills(new Column("created_time", FieldFill.INSERT))
                .addTableFills(new Column("updated_time", FieldFill.INSERT_UPDATE))
                .build();
    }

    private InjectionConfig buildInjectionConfig(Generator.GeneratorConf generatorConf) {
        return new InjectionConfig.Builder()
                .customFile(createCustomFiles(generatorConf))
                .build();
    }

    private List<CustomFile> createCustomFiles(Generator.GeneratorConf generatorConf) {
        String basePackage = generatorConf.getGroupId();
        List<CustomFile> customFiles = new ArrayList<>();
        customFiles.add(new CustomFile.Builder()
                .fileName("DomainIApi.java")
                .filePath(generatorConf.getModelApiPath() + "/src/main/java")
                .packageName(basePackage + ".api")
                .templatePath("/templates/ApiI.java.vm")
                .build());
        customFiles.add(new CustomFile.Builder()
                .fileName("DM.java")
                .filePath(generatorConf.getModelApiPath() + "/src/main/java")
                .packageName(basePackage + ".domain")
                .templatePath("/templates/entityDM.java.vm")
                .build());
        customFiles.add(new CustomFile.Builder()
                .fileName("DomainApiClient.java")
                .filePath(generatorConf.getModelClientPath() + "/src/main/java")
                .packageName(basePackage + ".api")
                .templatePath("/templates/client-api.java.vm")
                .build());
        customFiles.add(new CustomFile.Builder()
                .fileName("Domain.java")
                .filePath(generatorConf.getModelProviderPath() + "/src/main/java")
                .packageName(basePackage + ".domain")
                .templatePath("/templates/domain.java.vm")
                .build());
        customFiles.add(new CustomFile.Builder()
                .fileName("DomainApi.java")
                .filePath(generatorConf.getModelProviderPath() + "/src/main/java")
                .packageName(basePackage + ".application")
                .templatePath("/templates/domain.api.java.vm")
                .build());
        customFiles.add(new CustomFile.Builder()
                .fileName("DomainService.java")
                .filePath(generatorConf.getModelProviderPath() + "/src/main/java")
                .packageName(basePackage + ".domain")
                .templatePath("/templates/domain.service.java.vm")
                .build());
        return customFiles;
    }

    private void cleanupPersistentApi(Generator.GeneratorConf generatorConf) {
        String path = generatorConf.getModelProviderPath() + "/src/main/java/" +
                generatorConf.getGroupId().replace('.', '/') + "/infrastructure/persistent/api";
        Generator.deleteDirectory(new File(path));
    }
}
