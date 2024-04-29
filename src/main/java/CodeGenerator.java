import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.lily.generator.EngineVelocityTemplateEngine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    public static void main(String[] args) {
        CodeGenerator.execute();
    }

    public static void execute(){
        //Step 1 生成项目名，模块名
        //project path
        Generator.GeneratorConf generatorConf = new Generator.GeneratorConf();
        generatorConf.setGroupId("com.lily.user10");
        generatorConf.setArtifactId("user-center10");
        generatorConf.setModel("user10");
        Generator.createDirectory(generatorConf.getProjectPath());
        Generator.createDirectory(generatorConf.getModelApiPath());

        Generator.createDirectory(generatorConf.getModelClientPath());
        Generator.createDirectory(generatorConf.getModelProviderPath());
        Generator.createDirectory(generatorConf.getModelProviderPath()+"/src/main/resources");
        Generator.createDirectory(generatorConf.getModelClientPath()+"/src/main/resources");
        Generator.createDirectory(generatorConf.getModelApiPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/domain");
        Generator.createDirectory(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/'));
        Generator.createDirectory(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/infrastructure/conf");
        Generator.createDirectory(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/infrastructure/common");

//        EngineVelocityTemplateEngine.generatorConf = generatorConf;
        EngineVelocityTemplateEngine.MODEL = generatorConf.getModel();
        EngineVelocityTemplateEngine.GROUP_ID = generatorConf.getGroupId();
        System.out.println(generatorConf);
        //Step 2 创建父pom 文件
        // 创建Velocity上下文
        VelocityContext context = new VelocityContext();
        context.put("model",generatorConf.getModel());
        context.put("groupId", generatorConf.getGroupId());
        context.put("artifactId", generatorConf.getArtifactId());
        context.put("api", generatorConf.getModelApi());
        context.put("client", generatorConf.getModelClient());
        context.put("provider", generatorConf.getModelProvider());
        Generator.generatorCus(generatorConf.getProjectPath()+"/pom.xml","templates/parent.pom.vm",context);
        Generator.generatorCus(generatorConf.getProjectPath()+"/.gitignore","templates/.gitignore.vm",context);
        Generator.generatorCus(generatorConf.getModelApiPath()+"/pom.xml","templates/api.pom.vm",context);
        Generator.generatorCus(generatorConf.getModelClientPath()+"/pom.xml","templates/client.pom.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/pom.xml","templates/provider.pom.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/src/main/resources/application.yml","templates/application.yml.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/src/main/resources/logback-spring.xml","templates/logback-spring.xml.vm",context);
        Generator.generatorCus(generatorConf.getModelClientPath()+"/src/main/resources/application.yml","templates/client-application.yml.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/ApplicationMain.java","templates/application.main.java.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/infrastructure/conf/SwaggerConfig.java","templates/swagger.config.java.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/infrastructure/conf/MyBatisPlusConfig.java","templates/mybatis.plus.config.java.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/infrastructure/conf/MyMetaObjectHandler.java","templates/mybatis.plus.fill.java.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/infrastructure/common/BaseEntity.java","templates/base.entity.java.vm",context);
        Generator.generatorCus(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/infrastructure/common/ObjectConvert.java","templates/object.convert.java.vm",context);
        Generator.generatorCus(generatorConf.getModelApiPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/domain/BaseDM.java","templates/base.dm.java.vm",context);

        //Step 3
        CodeGenerator.testSimple(generatorConf);
    }



    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder("jdbc:mysql://rm-wz912w7jddju3sglupo.mysql.rds.aliyuncs.com/dbtest?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true", "root", "Szyy2024")
//            .Builder("jdbc:mysql://127.0.0.1:3306/dbtest?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true", "root", "123456")
            .schema("dbtest")
            .build();

//    @Test
    public static void testSimple(Generator.GeneratorConf generatorConf) {

        /*
            步骤：
            1. 生成持久层文件到临时目录
            2. 生成项目框架
            3. Copy 持久层文件到项目
         */

        // 包配置
        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent(generatorConf.getGroupId()+".infrastructure.persistent")
//                .moduleName(generatorConf.getModel())
                .controller("api")
                .service("service")
                .serviceImpl("service.impl")
                .entity("entity")
                .mapper("mapper")
                .xml("mapper.xml")
                .build();


        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig.Builder()
//                .outputDir(System.getProperty("user.dir") + "/src/main/java")
                .outputDir(generatorConf.getModelProviderPath()+"/src/main/java")
                .author(Generator.AUTHOR)
//                .enableSwagger()
                .enableSpringdoc()
                .disableOpenDir()
                .build();

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .addInclude("user","account")
                //实体类策略配置
                .entityBuilder().enableLombok()
                .superClass(generatorConf.getGroupId()+".infrastructure.common.BaseEntity")
                .addSuperEntityColumns("created_by", "created_time", "updated_by", "updated_time", "deleted")
                .idType(IdType.AUTO)
                .logicDeleteColumnName("deleted")
//                .addTableFills(new Property("createdTime", FieldFill.INSERT))
//                .addTableFills(new Property("updatedTime", FieldFill.INSERT_UPDATE))
                .addTableFills(new Column("created_time", FieldFill.INSERT))
                .addTableFills(new Column("updated_time", FieldFill.INSERT_UPDATE))
//                .controllerBuilder().enableRestStyle().superClass("com.lily.demo.api.TUserDomainApi")
//                .controllerBuilder().enableRestStyle().superClass(generatorConf.getGroupId().replace('/','.') + ".api"+)
                .build();
        List<CustomFile> list = new ArrayList<>();
        list.add(new CustomFile.Builder()
                .fileName("DomainIApi.java")
                .filePath(generatorConf.getModelApiPath()+"/src/main/java")
                .packageName(generatorConf.getGroupId().replace('/','.') + ".api")
                .templatePath("/templates/ApiI.java.vm")
                .build());
        list.add(new CustomFile.Builder()
                .fileName("DM.java")
                .filePath(generatorConf.getModelApiPath()+"/src/main/java")
                .packageName(generatorConf.getGroupId().replace('/','.') + ".domain")
//                .packageName("com.lily.demo.domain")
                .templatePath("/templates/entityDM.java.vm")
                .build());
        list.add(new CustomFile.Builder()
                .fileName("DomainApiClient.java")
                .filePath(generatorConf.getModelClientPath()+"/src/main/java")
                .packageName(generatorConf.getGroupId().replace('/','.') + ".api")
//                .packageName("com.lily.demo.domain")
                .templatePath("/templates/client-api.java.vm")
                .build());

        list.add(new CustomFile.Builder()
                .fileName("Domain.java")
                .filePath(generatorConf.getModelProviderPath()+"/src/main/java")
                .packageName(generatorConf.getGroupId().replace('/','.') + ".domain")
//                .packageName("com.lily.demo.domain")
                .templatePath("/templates/domain.java.vm")
                .build());
        list.add(new CustomFile.Builder()
                .fileName("DomainApi.java")
                .filePath(generatorConf.getModelProviderPath()+"/src/main/java")
                .packageName(generatorConf.getGroupId().replace('/','.') + ".application")
//                .packageName("com.lily.demo.domain")
                .templatePath("/templates/domain.api.java.vm")
                .build());
        list.add(new CustomFile.Builder()
                .fileName("DomainService.java")
                .filePath(generatorConf.getModelProviderPath()+"/src/main/java")
                .packageName(generatorConf.getGroupId().replace('/','.') + ".domain")
//                .packageName("com.lily.demo.domain")
                .templatePath("/templates/domain.service.java.vm")
                .build());
        InjectionConfig injectionConfig = new InjectionConfig.Builder().customFile(list).build();


        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig);
        generator.global(globalConfig);
        generator.packageInfo(packageConfig);
        generator.injection(injectionConfig);
//        generator.template(new EngineVelocityTemplateEngine());

        generator.execute(new EngineVelocityTemplateEngine());

        //删除 持久层 API 目录
        Generator.deleteDirectory(new File(generatorConf.getModelProviderPath()+"/src/main/java/"+generatorConf.getGroupId().replace('.','/')+"/infrastructure/persistent/api"));


    }
}
